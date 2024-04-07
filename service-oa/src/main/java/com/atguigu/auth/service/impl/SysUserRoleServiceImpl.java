package com.atguigu.auth.service.impl;

import com.atguigu.auth.mapper.SysUserRoleMapper;
import com.atguigu.auth.service.SysRoleService;
import com.atguigu.auth.service.SysUserRoleService;
import com.atguigu.model.system.SysRole;
import com.atguigu.model.system.SysUserRole;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户角色 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2024-04-03
 */
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements SysUserRoleService {

    @Autowired
    SysRoleService service;

    @Override
    public String findRoleNameByUserId(Long userId) {
        //查询RoleId
        LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserRole::getUserId, userId);
        List<SysUserRole> sysUserRoles = baseMapper.selectList(queryWrapper);
        SysUserRole sysUserRole = sysUserRoles.get(0);
        Long roleId = sysUserRole.getRoleId();
        //根据RoleId查询RoleName
        SysRole role = service.getById(roleId);
        String roleName = role.getRoleName();
        return roleName;
    }
}
