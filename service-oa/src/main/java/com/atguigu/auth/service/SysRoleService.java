package com.atguigu.auth.service;

import com.atguigu.vo.system.AssignRoleVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.model.system.SysRole;

import java.util.Map;


public interface SysRoleService extends IService<SysRole> {
    Map<String, Object> findRoleDataByUserId(Long userId);

    void doAssign(AssignRoleVo assignRoleVo);
}
