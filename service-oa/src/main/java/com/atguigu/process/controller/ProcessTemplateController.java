package com.atguigu.process.controller;


import com.atguigu.common.result.Result;
import com.atguigu.model.process.ProcessTemplate;
import com.atguigu.process.service.ProcessTemplateService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 审批模板 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2024-04-15
 */
@Api(tags = "审批模板")
@RestController
@RequestMapping("/admin/process/processTemplate")
public class ProcessTemplateController {
    @Autowired
    ProcessTemplateService processTemplateService;

    @ApiOperation(value = "模板分页")
    @GetMapping("/{page}/{limit}")
    public Result index(@PathVariable Long page,
                        @PathVariable Long limit){
        Page<ProcessTemplate> pageParam  = new Page<>(page, limit);
        IPage<ProcessTemplate> pageModel = processTemplateService.selectProcessTemplate(pageParam);
        return  Result.success(pageModel);
    }

    //@PreAuthorize("hasAuthority('bnt.processTemplate.list')")
    @ApiOperation(value = "获取")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        ProcessTemplate processTemplate = processTemplateService.getById(id);
        return Result.success(processTemplate);
    }

    //@PreAuthorize("hasAuthority('bnt.processTemplate.templateSet')")
    @ApiOperation(value = "新增")
    @PostMapping("save")
    public Result save(@RequestBody ProcessTemplate processTemplate) {
        processTemplateService.save(processTemplate);
        return Result.success(null);
    }

    //@PreAuthorize("hasAuthority('bnt.processTemplate.templateSet')")
    @ApiOperation(value = "修改")
    @PutMapping("update")
    public Result updateById(@RequestBody ProcessTemplate processTemplate) {
        processTemplateService.updateById(processTemplate);
        return Result.success(null);
    }

    //@PreAuthorize("hasAuthority('bnt.processTemplate.remove')")
    @ApiOperation(value = "删除")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        processTemplateService.removeById(id);
        return Result.success(null);
    }
    @ApiOperation("流程文件上传接口")
    @PostMapping("/uploadProcessDefinition")
    public Result uploadProcessDefinition(MultipartFile file) {
        //获取classpath绝对路径
        File path = new File(ProcessTemplateController.class.getClassLoader().getResource("").getPath());
        String forward = path+"/processes";
        if (!new File(forward+"/").exists()) {
            new File(forward+"/").mkdirs();
        }
        File fileForward = new File(forward+"/"+file.getOriginalFilename());
        try {
            file.transferTo(fileForward);
        } catch (IOException e) {
            return Result.fail("文件上传失败");
        }

        Map<String, Object> map = new HashMap<>();
        //根据上传地址后续部署流程定义，文件名称为流程定义的默认key
        map.put("processDefinitionPath", "processes/" + file.getOriginalFilename());
        map.put("processDefinitionKey", file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf(".")));
        return Result.success(map);
    }
    @ApiOperation("发布")
    @GetMapping("/publish/{id}")
    public Result publish(@PathVariable Long id) {
        processTemplateService.publish(id);
        return Result.success(null);
    }
}


