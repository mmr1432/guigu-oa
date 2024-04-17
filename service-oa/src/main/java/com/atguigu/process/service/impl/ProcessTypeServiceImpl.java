package com.atguigu.process.service.impl;

import com.atguigu.model.process.ProcessTemplate;
import com.atguigu.model.process.ProcessType;
import com.atguigu.process.mapper.ProcessTypeMapper;
import com.atguigu.process.service.ProcessTemplateService;
import com.atguigu.process.service.ProcessTypeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 审批类型 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2024-04-15
 */
@Service
public class ProcessTypeServiceImpl extends ServiceImpl<ProcessTypeMapper, ProcessType> implements ProcessTypeService {


    @Autowired
    ProcessTemplateService processTemplateService;
    @Override
    public List<ProcessType> findProcessType() {
        //获取所有集合
        List<ProcessType> processTypes = baseMapper.selectList(null);

        for (ProcessType processType:
             processTypes) {
            //遍历获取id
            Long id = processType.getId();
            //根据id查模板
            LambdaQueryWrapper<ProcessTemplate> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ProcessTemplate::getProcessTypeId,id);
            List<ProcessTemplate> list = processTemplateService.list(wrapper);
            //将审批模板封装
            processType.setProcessTemplateList(list);

        }
        return processTypes;
    }
}
