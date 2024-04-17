package com.atguigu.process.service;

import com.atguigu.vo.process.ApprovalVo;
import com.atguigu.vo.process.ProcessFormVo;
import com.atguigu.vo.process.ProcessQueryVo;
import com.atguigu.vo.process.ProcessVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.model.process.Process;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * <p>
 * 审批类型 服务类
 * </p>
 *
 * @author atguigu
 * @since 2024-04-16
 */
public interface ProcessService extends IService<Process> {

    IPage<ProcessVo> selectPage(Page<ProcessVo> pageParam,@Param("vo") ProcessQueryVo processQueryVo);
    //部署流程定义
    void deployByZip(String path);

    void startUp(ProcessFormVo processFormVo);

    IPage<ProcessVo> findPending(Page<Process> pageParam);

    Map<String, Object> show(Long id);

    void approve(ApprovalVo approvalVo);

    IPage<ProcessVo> findProcess(Page<Process> pageParam);

    IPage<ProcessVo> findStarted(Page<ProcessVo> pageParam);
}
