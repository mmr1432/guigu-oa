package com.atguigu.process.controller;

import com.atguigu.auth.service.SysUserService;
import com.atguigu.common.result.Result;
import com.atguigu.model.process.Process;
import com.atguigu.model.process.ProcessTemplate;
import com.atguigu.model.process.ProcessType;
import com.atguigu.process.service.ProcessService;
import com.atguigu.process.service.ProcessTemplateService;
import com.atguigu.process.service.ProcessTypeService;
import com.atguigu.vo.process.ApprovalVo;
import com.atguigu.vo.process.ProcessFormVo;
import com.atguigu.vo.process.ProcessVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "前端员工审批")
@RestController
@RequestMapping("/admin/process")
@CrossOrigin//跨域
public class ProcessWechatController {
    @Autowired
    private ProcessTypeService processTypeService;

    @Autowired
    ProcessService processService;

    @Autowired
    ProcessTemplateService processTemplateService;

    @Autowired
    private SysUserService sysUserService;
    //查询审批模板
    @GetMapping("findProcessType")
    public Result findProcessType() {
        List<ProcessType> list = processTypeService.findProcessType();
        return Result.success(list);
    }
    //获取审批模板数据
    @GetMapping("getProcessTemplate/{processTemplateId}")
    public Result getProcessTemplate(@PathVariable("processTemplateId") Long processTemplateId) {
        ProcessTemplate templateService = processTemplateService.getById(processTemplateId);
        return Result.success(templateService);

    }

    @ApiOperation("启动流程")
    @GetMapping("/startUp")
    public Result startUp(@RequestBody ProcessFormVo processFormVo) {
        processService.startUp(processFormVo);
        return Result.success(null);
    }
    @ApiOperation("待处理")
    @GetMapping("/findPending/{page}/{limit}")
    public Result findPending(@ApiParam(name = "page",value = "当前页码",required = true) @PathVariable("page") Long page,
                              @ApiParam(name = "limit",value = "每页记录数",required = true)@PathVariable("limit") Long limit) {
        Page<Process> pageParam = new Page<>(page, limit);
        IPage<ProcessVo> pageModel = processService.findPending(pageParam);
        return Result.success(pageModel);
    }
    //查询审批详情
    @GetMapping("show/{id}")
    public Result show(@PathVariable("id") Long id) {
        Map<String ,Object> map = processService.show(id);
        return Result.success(map);
    }
    //审批
    @PostMapping("approve")
    public Result approve(@RequestBody ApprovalVo approvalVo) {
        processService.approve(approvalVo);
        return Result.success(null);
    }

    //已处理
    @ApiOperation(value = "已处理")
    @GetMapping("/findProcessed/{page}/{limit}")
    public Result findProcessed(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Long page,
            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Long limit) {
        Page<Process> pageParam = new Page<>(page, limit);
        IPage<ProcessVo> pageModel = processService.findProcess(pageParam);
        return Result.success(pageModel);
    }

    //已发起（发起申请）
    @ApiOperation(value = "已发起")
    @GetMapping("/findStarted/{page}/{limit}")
    public Result findStarted(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Long page,

            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Long limit) {
        Page<ProcessVo> pageParam = new Page<>(page, limit);
        return Result.success(processService.findStarted(pageParam));
    }


    @GetMapping("getCurrentUser")
    public Result getCurrentUser() {
        return Result.success(sysUserService.getCurrentUser());
    }
}
