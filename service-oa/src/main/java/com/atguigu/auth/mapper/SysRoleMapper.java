package com.atguigu.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.atguigu.model.system.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface SysRoleMapper extends BaseMapper<SysRole> {
}
