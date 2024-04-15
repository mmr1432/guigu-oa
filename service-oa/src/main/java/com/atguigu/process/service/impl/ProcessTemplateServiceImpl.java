package com.atguigu.process.service.impl;

import com.atguigu.model.process.ProcessTemplate;
import com.atguigu.model.process.ProcessType;
import com.atguigu.process.mapper.ProcessTemplateMapper;
import com.atguigu.process.service.ProcessTemplateService;
import com.atguigu.process.service.ProcessTypeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 审批模板 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2024-04-15
 */
@Service
public class ProcessTemplateServiceImpl extends ServiceImpl<ProcessTemplateMapper, ProcessTemplate> implements ProcessTemplateService {


    @Autowired
    ProcessTypeService processTypeService;
    @Override
    public IPage<ProcessTemplate> selectProcessTemplate(Page<ProcessTemplate> pageParam) {
        /*//调用方法实现分页查询
        Page<ProcessTemplate> page = baseMapper.selectPage(pageParam, null);
        //从分页数据中获取list
        List<ProcessTemplate> processTemplateList = page.getRecords();
        //得到审批id
        *//*processTemplateList.stream()
                .forEach(s->{
                    //根据审批id获取审批名称
                    LambdaQueryWrapper<ProcessType> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(ProcessType::getId, s.getProcessTypeId());
                    ProcessType one = processTypeService.getOne(queryWrapper);

                    if(one!=null){
                        return;
                    }
                    else
                    {
                        s.setProcessTypeName(one.getName());
                    }
                });*//*
        for (ProcessTemplate processTemplate: processTemplateList) {
            Long typeId = processTemplate.getProcessTypeId();
            LambdaQueryWrapper<ProcessType> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ProcessType::getId, processTemplate.getProcessTypeId());

            ProcessType one = processTypeService.getOne(queryWrapper);
            if (one != null) {
                continue;
            }else
            {
                processTemplate.setProcessTypeName(one.getName());
            }
        }



        //封装
        return page;*/
//查询获取ProcessTemplate
        LambdaQueryWrapper<ProcessTemplate> queryWrapper = new LambdaQueryWrapper<ProcessTemplate>();
        queryWrapper.orderByDesc(ProcessTemplate::getId);
        IPage<ProcessTemplate> page = baseMapper.selectPage(pageParam, queryWrapper);
        List<ProcessTemplate> processTemplateList = page.getRecords();
//获取typeId
        List<Long> processTypeIdList = processTemplateList.stream()
                .map(processTemplate -> processTemplate.getProcessTypeId())
                .collect(Collectors.toList());

        if(!CollectionUtils.isEmpty(processTypeIdList)) {
            Map<Long, ProcessType> processTypeIdToProcessTypeMap = processTypeService.list(new LambdaQueryWrapper<ProcessType>()
                            .in(ProcessType::getId, processTypeIdList))
                    .stream()
                    .collect(Collectors
                            .toMap(ProcessType::getId, ProcessType -> ProcessType));
            for(ProcessTemplate processTemplate : processTemplateList) {
                ProcessType processType = processTypeIdToProcessTypeMap.get(processTemplate.getProcessTypeId());
                if(null == processType) continue;
                processTemplate.setProcessTypeName(processType.getName());
            }
        }
        return page;
    }
}
