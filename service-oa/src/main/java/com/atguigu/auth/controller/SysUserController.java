package com.atguigu.auth.controller;


import com.atguigu.auth.service.SysUserService;
import com.atguigu.common.MD5;
import com.atguigu.common.result.Result;
import com.atguigu.model.system.SysUser;
import com.atguigu.vo.system.SysUserQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2024-04-03
 */
@Api(tags = "角色管理系统")
@RestController
@RequestMapping("/admin/system/sysUser")
public class SysUserController {

    @Autowired
    SysUserService sysUserService;


    @ApiOperation("条件分页查询")
    @GetMapping("{page}/{limit}")
    public Result index(@PathVariable Long page,
                        @PathVariable Long limit,
                        SysUserQueryVo sysUserQueryVo){

        //创建page对象
        Page<SysUser> pageParam = new Page<>(page, limit);
        //封装条件
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        //获取条件值
        String username = sysUserQueryVo.getKeyword();
        String createTimeBegin = sysUserQueryVo.getCreateTimeBegin();
        String createTimeEnd = sysUserQueryVo.getCreateTimeEnd();
        //判断条件是否为空，不为空就封装条件

        if (!StringUtils.isEmpty(username)){
            wrapper.like(SysUser::getName,username);
        }
        //ge为大于
        if (!StringUtils.isEmpty(createTimeBegin)){
            wrapper.ge(SysUser::getCreateTime,createTimeBegin);
        }
        //le为小于
        if (!StringUtils.isEmpty(createTimeEnd)){
            wrapper.ge(SysUser::getCreateTime,createTimeEnd);
        }

        //调用mp方法
        IPage<SysUser> sysUserIPage = sysUserService.page(pageParam,wrapper);
        return Result.success(sysUserIPage);
    }
    @ApiOperation(value = "获取用户")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        SysUser user = sysUserService.getById(id);
        return Result.success(user);
    }

    @ApiOperation(value = "保存用户")
    @PostMapping("save")
    public Result save(@RequestBody SysUser user) {
        user.setPassword(MD5.encrypt(user.getPassword()));
        sysUserService.save(user);
        return Result.success(null);
    }

    @ApiOperation(value = "更新用户")
    @PutMapping("update")
    public Result updateById(@RequestBody SysUser user) {
        sysUserService.updateById(user);
        return Result.success(null);
    }

    @ApiOperation(value = "删除用户")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        sysUserService.removeById(id);
        return Result.success(null);
    }
    @ApiOperation(value = "更新状态")
    @GetMapping("updateStatus/{id}/{status}")
    public Result updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        sysUserService.updateStatus(id, status);
        return Result.success(null);
    }


}


