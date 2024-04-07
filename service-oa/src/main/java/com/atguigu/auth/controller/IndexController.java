package com.atguigu.auth.controller;

import com.atguigu.auth.service.SysMenuService;
import com.atguigu.auth.service.SysUserRoleService;
import com.atguigu.auth.service.SysUserService;
import com.atguigu.common.MD5;
import com.atguigu.common.exception.GuiguException;
import com.atguigu.common.jwt.JwtHelper;
import com.atguigu.common.result.Result;
import com.atguigu.model.system.SysUser;
import com.atguigu.vo.system.LoginVo;
import com.atguigu.vo.system.RouterVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@Api(tags = "后台登录管理")
@RestController
@RequestMapping("/admin/system/index")
public class IndexController {
    @Autowired
    SysUserService sysUserService;
    @Autowired
    SysMenuService sysMenuService;
    @Autowired
    SysUserRoleService sysUserRoleService;


    @PostMapping("/login")
    //查询数据库，检查用户是否存在
    //判断用户是否被禁用
    //用token保持登录状态
    public Result login(@RequestBody LoginVo loginVo){
        /*Map<String,Object> map = new HashMap<>();
        map.put("token","admin-token");
        return Result.success(map);*/
        //获取用户名和密码
        String username = loginVo.getUsername();
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(SysUser::getUsername, username);
        SysUser sysUser = sysUserService.getOne(queryWrapper);
        //判断用户名是否正确
        if(sysUser == null){
            throw new GuiguException(201,"用户名错误");
        }
        String password = loginVo.getPassword();
        if (password == null || "".equals(password) || MD5.encrypt(password).equals(sysUser.getPassword())) {
            throw new GuiguException(201,"密码错误");
        }
        //根据status判断用户是否被禁用
        if (sysUser.getStatus()==0){
            throw new GuiguException(201,"该用户以被禁用");
        }
        //确认正确后，返回用JWT封装的token对象
        String token = JwtHelper.createToken(sysUser.getId(), sysUser.getUsername());
        HashMap<String ,String > map = new HashMap<>();
        map.put("token", token);
        return Result.success(map);

    }
    @GetMapping("/info")
    public Result info(@RequestHeader("token") String token){
        //获取id和name
        Long userId = JwtHelper.getUserId(token);
        String username = JwtHelper.getUsername(token);
        //获取RoleName
        String roleName = sysUserRoleService.findRoleNameByUserId(userId);

        //获取操作菜单和按钮
        List<RouterVo> userMenuVoByUserId = sysMenuService.findUserMenuVoByUserId(userId);
        List<String> userPermsByUserId = sysMenuService.findUserPermsByUserId(userId);

        HashMap<String, Object> map = new HashMap<>();
        map.put("roles",roleName);
        map.put("name",username);
        map.put("avatar","https://oss.aliyuncs.com/aliyun_id_photo_bucket/default_handsome.jpg");
        //TODO 返回可以操作的菜单
        map.put("routers",userMenuVoByUserId);
        //TODO 返回可以操作的按钮
        map.put("buttons",userPermsByUserId);

        return Result.success(map);
    }
    @PostMapping("logout")
    public Result logout(){
        return Result.success(null);
    }

}
