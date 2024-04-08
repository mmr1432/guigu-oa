package com.atguigu.auth.service.impl;

import com.atguigu.auth.service.SysMenuService;
import com.atguigu.auth.service.SysUserService;
import com.atguigu.model.system.SysUser;
import com.atguigu.security.custom.CustomUser;
import com.atguigu.security.custom.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysMenuService sysMenuService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //根据用户名称查询
        SysUser user = sysUserService.getUserByUserName(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        //根据用户id查询权限
        List<String> userPermsByUserId = sysMenuService.findUserPermsByUserId(user.getId());
        //封装到SimpleGrantedAuthority中
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
        for (String perms : userPermsByUserId) {
            authorityList.add(new SimpleGrantedAuthority(perms.trim()));
        }
        return new CustomUser(user, authorityList);
    }
}
