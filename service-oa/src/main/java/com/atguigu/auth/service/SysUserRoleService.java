package com.atguigu.auth.service;

import com.atguigu.model.system.SysUserRole;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户角色 服务类
 * </p>
 *
 * @author atguigu
 * @since 2024-04-03
 */
public interface SysUserRoleService extends IService<SysUserRole> {
    String findRoleNameByUserId(Long userId);
}
