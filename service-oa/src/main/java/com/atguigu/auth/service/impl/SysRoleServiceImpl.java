package com.atguigu.auth.service.impl;

import com.atguigu.auth.service.SysUserRoleService;
import com.atguigu.model.system.SysUserRole;
import com.atguigu.vo.system.AssignRoleVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.auth.mapper.SysRoleMapper;
import com.atguigu.auth.service.SysRoleService;
import com.atguigu.model.system.SysRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
    //查询所有角色 和 当前用户所属角色
    @Autowired
    private SysUserRoleService service;
    @Override
    public Map<String, Object> findRoleDataByUserId(Long userId) {
        //查询所有角色合集，返回List
        List<SysRole> allSysRoles = baseMapper.selectList(null);
        //根据userId查询 查询userId所对应的角色id
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId,userId);
        List<SysUserRole> list = service.list(wrapper);//当前用户所属角色
        //根据角色id，找到对应的角色信息
        List<Long> ids = list.stream().map(user -> user.getRoleId()).collect(Collectors.toList());//对应角色id集合
        //根据所取id，到所有角色的list集合中进行比较
        List<SysRole> assignRoleList = new ArrayList<>();
        for (SysRole sysRole : allSysRoles){
            if (ids.contains(sysRole.getId())){
                assignRoleList.add(sysRole);
            }
        }

        //封装到map集合中
        Map<String, Object> roleMap = new HashMap<>();
        roleMap.put("assginRoleList", assignRoleList);
        roleMap.put("allRolesList", allSysRoles);
        return roleMap;
    }

    //分配角色
    @Override
    public void doAssign(AssignRoleVo assignRoleVo) {
        //删除之前分配的角色
        //在用户角色关系表中根据用户id删除
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId,assignRoleVo.getUserId());
        service.remove(wrapper);

        //重新分配
        List<Long> roleIdList = assignRoleVo.getRoleIdList();
        for (Long roleId:roleIdList){
            if (StringUtils.isEmpty(roleId)){
                continue;
            }
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setUserId(assignRoleVo.getUserId());
            sysUserRole.setRoleId(roleId);
            service.save(sysUserRole);
        }
    }
}
