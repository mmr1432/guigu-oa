package com.atguigu.process.controller;


import com.atguigu.common.result.Result;
import com.atguigu.model.process.Process;
import com.atguigu.process.service.ProcessService;
import com.atguigu.vo.process.ProcessQueryVo;
import com.atguigu.vo.process.ProcessVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 审批类型 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2024-04-16
 */
@Api(tags = "审批流管理")
@RestController
@RequestMapping("/admin/process")
public class ProcessController {
    @Autowired
    ProcessService service;
    //审批管理列表
    @ApiOperation("分页查询")
    @GetMapping("{page}/{limit}")
    public Result index(@PathVariable int page,
                        @PathVariable int limit,
                        ProcessQueryVo processQueryVo) {
        Page<ProcessVo> pageParam = new Page<>(page, limit);
        IPage<ProcessVo> pageModel = service.selectPage(pageParam, processQueryVo);
        return Result.success(pageModel);
    }
}

