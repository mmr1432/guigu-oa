package com.atguigu.auth.controller;

import com.atguigu.auth.service.SysRoleService;
import com.atguigu.common.result.Result;
import com.atguigu.model.system.SysRole;
import com.atguigu.vo.system.AssignRoleVo;
import com.atguigu.vo.system.SysRoleQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "用户管理")
@RestController
@RequestMapping("/admin/system/sysRole")
public class SysRoleController {
    @Autowired
    SysRoleService sysRoleService;

    //查询所有角色 和 当前用户所属角色
    @ApiOperation("获取角色")
    @GetMapping("/toAssign/{userId}")
    public Result toAssign(@PathVariable Long userId){
        Map<String,Object> role = sysRoleService.findRoleDataByUserId(userId);
        return Result.success(role);
    }

    //分配角色
    @ApiOperation("为用户分配角色")
    @PostMapping("/doAssign")
    public Result doAssign(@RequestBody AssignRoleVo assignRoleVo){
        sysRoleService.doAssign(assignRoleVo);
        return Result.success(null);
    }


    @ApiOperation("获取全部角色")
    @GetMapping("/findAll")
    public Result<List<SysRole>> findAll(){
        List<SysRole> list = sysRoleService.list();
        return Result.success(list);
    }

    @ApiOperation("条件分页查询")
    @GetMapping("{page}/{limit}")
    public Result pageQueryRole(@PathVariable long page,
                                @PathVariable long limit,
                                SysRoleQueryVo sysRoleQueryVo){

        //创建Page对象
        Page<SysRole> pageParam = new Page<>(page,limit);
        //封装查询对象
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        //判断名字是否为空
        String roleName = sysRoleQueryVo.getRoleName();
        //模糊搜索
        if (!StringUtils.isEmpty(roleName)){
            wrapper.like(SysRole::getRoleName,roleName);
        }

        IPage<SysRole> rolePage = sysRoleService.page(pageParam, wrapper);

        return Result.success(rolePage);

    }

    @ApiOperation("添加用户")
    @PostMapping("save")
    public Result save(@RequestBody SysRole sysRole){
        boolean isSuccess = sysRoleService.save(sysRole);
        if (isSuccess)
        return Result.success(null);
        else return Result.fail(null);
    }
    @ApiOperation("根据id查询")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id){
        SysRole sysRole = sysRoleService.getById(id);
        return Result.success(sysRole);
    }
    @ApiOperation("修改角色")
    @PutMapping("update")
    public Result update(@RequestBody SysRole sysRole){
        boolean isSuccess = sysRoleService.updateById(sysRole);
        if (isSuccess)
            return Result.success(null);
        else return Result.fail(null);
    }
    @ApiOperation("根据id删除")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id){
        boolean isSuccess = sysRoleService.removeById(id);
        if (isSuccess)
            return Result.success(null);
        else
            return Result.fail(null);
    }
    @ApiOperation("批量删除")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> ids){

        boolean isSuccess = sysRoleService.removeByIds(ids);
        if(isSuccess)
            return Result.success(null);
        else
            return Result.fail(null);
    }
}
