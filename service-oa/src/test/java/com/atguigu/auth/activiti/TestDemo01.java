package com.atguigu.auth.activiti;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestDemo01 {
    @Autowired
    RepositoryService repositoryService;
    @Autowired
    RuntimeService runtimeService;
    @Autowired
    TaskService taskService;
    //挂起流程
    @Test
    public void suspendProcess(){
        //获取流程实例
        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("请假")
                .singleResult();

        System.out.println(definition.isSuspended());
        if (!definition.isSuspended()){
            repositoryService.suspendProcessDefinitionById(definition.getId());
        }else {
            repositoryService.activateProcessDefinitionById(definition.getId());
        }
    }


    //创建流程实例 绑定key
    @Test
    public void startProcessAddBusinessKey(){
        ProcessInstance qingjia
                = runtimeService.startProcessInstanceByKey("请假", "1001");
    }
    @Test
    public void startProcess(){
        ProcessInstance qingjia = runtimeService.startProcessInstanceByKey("请假");
        System.out.println("qingjia.getProcessInstanceId() = " + qingjia.getProcessInstanceId());
        System.out.println("qingjia.getName() = " + qingjia.getName());
        System.out.println("qingjia.getId() = " + qingjia.getId());
    }

    //单个文件部署
    @Test
    public void deployProcess(){
        Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource("process/请假.bpmn20.xml")
                .addClasspathResource("process/qingjia.png")
                .name("请假流程")
                .deploy();

        System.out.println(deploy.getCategory());
        System.out.println("deploy.getId() = " + deploy.getId());
        System.out.println("deploy.getName() = " + deploy.getName());
    }

    @Test
    public void deployProcess2(){
        Deployment deployment = repositoryService.createDeployment()
                        .addClasspathResource("process/jiaban02.bpmn20.xml")
                        .addClasspathResource("process/qingjia.png")
                        .name("加班03")
                        .deploy();
        System.out.println(deployment.getId());
    }

    @Test
    public void startProcess2(){
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("jiaban02","加班");
        System.out.println(instance.getId());
        System.out.println(instance.getProcessInstanceId());
    }
    @Test
    public void taskAssign(){
        TaskQuery taskQuery = taskService.createTaskQuery();
        taskQuery.taskAssignee("tom").list().forEach(s->{
            System.out.println(s.getId());
            System.out.println(s.getAssignee());
            System.out.println(s.getName());
        });
    }
    @Test
    public void taskComplete(){
        TaskQuery taskQuery = taskService.createTaskQuery();
        Task jack = taskQuery.taskAssignee("jack").singleResult();

        taskService.complete(jack.getId());
    }

    //拾取组任务
    @Test
    public void claimTask(){
        Task tom = taskService.createTaskQuery()
                .taskCandidateUser("tom")
                .singleResult();

        if (tom!=null){

        }
    }

}
