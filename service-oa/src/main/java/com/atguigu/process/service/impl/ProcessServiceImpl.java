package com.atguigu.process.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.auth.service.SysUserService;
import com.atguigu.model.process.ProcessRecord;
import com.atguigu.model.process.ProcessTemplate;
import com.atguigu.model.system.SysUser;
import com.atguigu.process.mapper.ProcessMapper;
import com.atguigu.process.service.ProcessRecordService;
import com.atguigu.process.service.ProcessService;
import com.atguigu.process.service.ProcessTemplateService;
import com.atguigu.security.custom.LoginUserInfoHelper;
import com.atguigu.vo.process.ApprovalVo;
import com.atguigu.vo.process.ProcessFormVo;
import com.atguigu.vo.process.ProcessQueryVo;
import com.atguigu.vo.process.ProcessVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atguigu.model.process.Process;
import org.springframework.util.CollectionUtils;

import java.io.InputStream;
import java.util.*;
import java.util.zip.ZipInputStream;

/**
 * <p>
 * 审批类型 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2024-04-16
 */
@Service
public class ProcessServiceImpl extends ServiceImpl<ProcessMapper, Process> implements ProcessService {

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private ProcessTemplateService processTemplateService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskServiceBean;

    @Autowired
    private ProcessRecordService recordService;
    @Autowired
    private HistoryService historyServiceBean;

    @Override
    public IPage<ProcessVo> selectPage(Page<ProcessVo> pageParam, @Param("vo") ProcessQueryVo processQueryVo) {
        IPage<ProcessVo> selectPage  = baseMapper.selectPage(pageParam,processQueryVo);
        return selectPage;
    }

    @Override
    public void deployByZip(String path) {
        InputStream is = this.getClass().getResourceAsStream(path);
        ZipInputStream zipInputStream = new ZipInputStream(is);
        //部署

        Deployment deploy = repositoryService.createDeployment()
                .addZipInputStream(zipInputStream)
                .deploy();
        System.out.println("deploy.getId()+deploy.getName() = " + deploy.getId() + deploy.getName());
    }

    @Override
    public void startUp(ProcessFormVo processFormVo) {
        //根据id获取用户信息
        SysUser sysUser = sysUserService.getById(LoginUserInfoHelper.getUserId());//通过ThreadLocal取出
        //根据模板id查询模板信息
        ProcessTemplate processTemplate = processTemplateService.getById(processFormVo.getProcessTemplateId());

        Process process = new Process();
        BeanUtils.copyProperties(processFormVo,process);//将一个对象的值赋值给一个空对象
        process.setStatus(1);//0是驳回
        process.setProcessCode(System.currentTimeMillis()+"");//当前时间
        process.setUserId(sysUser.getId());
        process.setTitle(sysUser.getName()+"发起"+ processTemplate.getName());
        process.setFormValues(processFormVo.getFormValues());
        baseMapper.insert(process);
        //启动流程实例
        //流程定义key
        String processDefinitionKey = processTemplate.getProcessDefinitionKey();
        //业务key processId
        Long business = process.getId();
        //将json成map
        String formValues = processFormVo.getFormValues();
        JSONObject jsonObject = JSON.parseObject(formValues);
        JSONObject formData = jsonObject.getJSONObject("formData");
        //遍历得到内容（key，value）
        HashMap<String , Object> map = new HashMap<>();
        for (Map.Entry<String ,Object> entry : formData.entrySet()) {
            map.put(entry.getKey(), entry.getValue());
        }
        HashMap<String, Object> finalMap = new HashMap<>();
        finalMap.put("data",map);

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey,
                String.valueOf(business),
                finalMap);
        //查询下一个审批人 taskService
        List<Task> list = this.getCurrentTaskList(processInstance.getId());
        List<String > nameList = new ArrayList<>();
        for (Task task : list) {
            String name = task.getName();
            SysUser user = sysUserService.getUserByUserName(name);
            String realName = user.getName();
            nameList.add(realName);
            //TODO:推送消息
        }
        //将模板信息保存到oa_process
        process.setProcessInstanceId(processInstance.getId());
        process.setDescription("等待"+ StringUtils.join(nameList.toArray())+"审批");
        baseMapper.updateById(process);

        //将记录审批信息记录
        recordService.record(process.getId(),1,"发起申请");

    }
    //分页处理任务列表
    @Override
    public IPage<ProcessVo> findPending(Page<Process> pageParam) {
        //封装查询条件 根据当前登录的用户查询查询
        TaskQuery taskQuery = taskServiceBean.createTaskQuery()
                .taskAssignee(LoginUserInfoHelper.getUsername())
                .orderByTaskCreateTime()
                .desc();
        //调用分页条件查询，返回list集合 封装到List<ProcessVo>的对象中
        List<Task> tasks = taskQuery.listPage((int) ((pageParam.getCurrent() - 1) * pageParam.getSize()),//开始位置和每页记录数
                (int) (pageParam.getSize()));
        int total = tasks.size();
        //封装成vo对象
        List<ProcessVo> list = new ArrayList<>();
        for (Task task : tasks) {
            ProcessVo processVo = new ProcessVo();
            String instanceId = task.getProcessInstanceId();
            //获取实例id 获取业务key
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(instanceId)
                    .singleResult();
            String businessKey = processInstance.getBusinessKey();
            //根据业务key获取process对象
            if (businessKey != null) {
                continue;
            }
            Process process = baseMapper.selectById(Long.valueOf(businessKey));
            BeanUtils.copyProperties(process,processVo);
            processVo.setTaskId(task.getId());

            list.add(processVo);
        }

        //封装返回iPage对象
        IPage<ProcessVo> page = new Page<ProcessVo>(pageParam.getCurrent(),
                                                    pageParam.getSize(),
                                                    total);

        page.setRecords(list);

        return page;
    }

    @Override
    public Map<String, Object> show(Long id) {

        //根据流程id获取process信息
        Process process = baseMapper.selectById(id);
        //根据流程id查询record信息
        LambdaQueryWrapper<ProcessRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProcessRecord::getProcessId, id);
        List<ProcessRecord> list = recordService.list(wrapper);
        //查询，模板信息
        ProcessTemplate template = processTemplateService.getById(process.getProcessTemplateId());
        //判断当前用户能否审批 以及不能出现重复审批
        boolean isApprove = false;
        List<Task> currentTaskList = this.getCurrentTaskList(process.getProcessInstanceId());
        for (Task task : currentTaskList) {
            if (task.getAssignee().equals(LoginUserInfoHelper.getUsername()))
                isApprove = true;
        }

        //封装到map

        HashMap<String , Object> map = new HashMap<>();
        map.put("process",process);
        map.put("processRecordList",list);
        map.put("processTemplate",template);
        map.put("isApprove",isApprove);
        return map;
    }
//审批 通过修改isApprove
    @Override
    public void approve(ApprovalVo approvalVo) {
        //从approvalVo获取任务id 根据任务id获取流程变量
        String taskId = approvalVo.getTaskId();
        Map<String, Object> variables = taskServiceBean.getVariables(taskId);
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());
        }
        //获取审批状态 1 -1不通过
        Integer status = approvalVo.getStatus();
        if (status==1){
            taskServiceBean.complete(taskId,variables);
        }else {
            this.endTask(taskId);//结束流程
        }
        //保存到对应的表中 oa_process_record
        recordService.record(approvalVo.getProcessId(),
                            approvalVo.getStatus(),
                            (approvalVo.getStatus()==1?"已通过":"已驳回"));
        //查询下一个审批人 更新流程表中的记录
        Process process = baseMapper.selectById(approvalVo.getProcessId());
        List<Task> currentTaskList = this.getCurrentTaskList(process.getProcessInstanceId());
        //判断是否结束
        if (!CollectionUtils.isEmpty(currentTaskList)) {
            ArrayList<String > assignList = new ArrayList<>();
            for (Task task : currentTaskList) {
                String assignee = task.getAssignee();
                SysUser user = sysUserService.getUserByUserName(assignee);
                assignList.add(user.getName());//获取姓名
                //TODO 公众号推送
            }
            process.setDescription("等待"+ StringUtils.join(assignList)+"审批");
            process.setStatus(1);
        }
        else {
            if (approvalVo.getStatus()==1){
                process.setDescription("审批通过");
                process.setStatus(2);//通过
            }else {
                process.setDescription("审批驳回");
                process.setStatus(-1);
            }
        }
        baseMapper.updateById(process);


    }
//已处理
    @Override
    public IPage<ProcessVo> findProcess(Page<Process> pageParam) {
       //封装查询条件
        HistoricTaskInstanceQuery history = historyServiceBean.createHistoricTaskInstanceQuery()
                .taskAssignee(LoginUserInfoHelper.getUsername())
                .finished()
                .orderByTaskCreateTime()
                .desc();
        //调用方法条件分页查询
        List<HistoricTaskInstance> list = history.listPage((int) ((pageParam.getCurrent() - 1) * pageParam.getSize()), (int) pageParam.getSize());

        long totalCount = history.count();
        //遍历返回的list 封装processVo
        List<ProcessVo> processVoList = new ArrayList<>();
        for (HistoricTaskInstance historicTaskInstance : list) {
            ProcessVo processVo = new ProcessVo();
            String instanceId = historicTaskInstance.getProcessInstanceId();
            LambdaQueryWrapper<Process> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Process::getProcessInstanceId, instanceId);
            Process process = baseMapper.selectOne(wrapper);
            if (process == null) {
                continue;
            }
            BeanUtils.copyProperties(process,processVo);

            processVoList.add(processVo);

        }
        //用IPage封装
        IPage<ProcessVo> pageModel = new Page<ProcessVo>(pageParam.getCurrent(),
                                                        pageParam.getSize(),
                                                        totalCount);
        pageModel.setRecords(processVoList);
        return pageModel;
    }
//已发起
    @Override
    public IPage<ProcessVo> findStarted(Page<ProcessVo> pageParam) {
        ProcessQueryVo processQueryVo = new ProcessQueryVo();
        processQueryVo.setUserId(LoginUserInfoHelper.getUserId());
        IPage<ProcessVo> page = baseMapper.selectPage(pageParam, processQueryVo);
        return page;
    }

    private void endTask(String taskId) {
        //根据任务id获取从任务对象
        Task task = taskServiceBean.createTaskQuery().taskId(taskId).singleResult();
        //获取bpmn模型
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        List<EndEvent> endEventList = bpmnModel
                .getMainProcess()
                .findFlowElementsOfType(EndEvent.class);
        //获取结束流向节点
        if (CollectionUtils.isEmpty(endEventList)) {
            return;
        }
        FlowNode endFlowNode = (FlowNode)endEventList.get(0);
        //当前流向节点
        FlowNode currentFlowNode = (FlowNode)bpmnModel.getMainProcess().getFlowElement(task.getProcessDefinitionId());
        //清理当前流动方向
        currentFlowNode.getOutgoingFlows().clear();
        //创建新流向
        SequenceFlow newSequenceFlow = new SequenceFlow();
        newSequenceFlow.setSourceFlowElement(currentFlowNode);
        newSequenceFlow.setTargetFlowElement(endFlowNode);//目标节点
        //当前节点指向西南方向
        List newSqeuenceFlowList = new ArrayList();
        newSqeuenceFlowList.add(newSequenceFlow);
        currentFlowNode.setOutgoingFlows(newSqeuenceFlowList);
        //完成当前任务
        taskServiceBean.complete(taskId);


    }

    private List<Task> getCurrentTaskList(String id) {

        TaskQuery taskQuery = taskServiceBean.createTaskQuery();
        List<Task> list = taskQuery.processInstanceId(id).list();
        return list;
    }
}
