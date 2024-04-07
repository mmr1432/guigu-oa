package com.atguigu.auth.service.impl;

import com.atguigu.auth.mapper.SysMenuMapper;
import com.atguigu.auth.service.SysMenuService;
import com.atguigu.auth.service.SysRoleMenuService;
import com.atguigu.auth.util.MenuHelper;
import com.atguigu.common.exception.GuiguException;
import com.atguigu.model.system.SysMenu;
import com.atguigu.model.system.SysRoleMenu;
import com.atguigu.vo.system.AssignMenuVo;
import com.atguigu.vo.system.RouterVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2024-04-04
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Autowired
    private SysRoleMenuService sysRoleMenuService;
    @Override
    public List<SysMenu> findNodes() {
        //查询所有菜单
        List<SysMenu> sysMenuList = baseMapper.selectList(null);
        //构建树形结构
        List<SysMenu> list = MenuHelper.buildTree(sysMenuList);
        return list;
    }

    @Override
    public void removeMenuById(Long id) {
        //判断是否有子菜单 没有才可以删除

        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getParentId,id);

        Integer i = baseMapper.selectCount(wrapper);//返回个数
        if (i>0){
            throw new GuiguException(201,"菜单不能删除");
        }
        baseMapper.deleteById(id);//删除
    }
//获取全部菜单以及角色分配的菜单
    @Override
    public List<SysMenu> findMenuByRoleId(Long roleId) {
        //1 查询所有菜单- 添加条件 status=1
        LambdaQueryWrapper<SysMenu> wrapperSysMenu = new LambdaQueryWrapper<>();
        wrapperSysMenu.eq(SysMenu::getStatus,1);
        List<SysMenu> allSysMenuList = baseMapper.selectList(wrapperSysMenu);

        //2 根据角色id roleId查询 角色菜单关系表里面 角色id对应所有的菜单id
        LambdaQueryWrapper<SysRoleMenu> wrapperSysRoleMenu = new LambdaQueryWrapper<>();
        wrapperSysRoleMenu.eq(SysRoleMenu::getRoleId,roleId);
        List<SysRoleMenu> sysRoleMenuList = sysRoleMenuService.list(wrapperSysRoleMenu);

        //3 根据获取菜单id，获取对应菜单对象
        List<Long> menuIdList = sysRoleMenuList.stream().map(c -> c.getMenuId()).collect(Collectors.toList());

        //3.1 拿着菜单id 和所有菜单集合里面id进行比较，如果相同封装
        allSysMenuList.stream().forEach(item -> {
            if(menuIdList.contains(item.getId())) {
                item.setSelect(true);
            } else {
                item.setSelect(false);
            }
        });

        //4 返回规定树形显示格式菜单列表
        List<SysMenu> sysMenuList = MenuHelper.buildTree(allSysMenuList);
        return sysMenuList;
    }
//分配角色菜单
    @Override
    public void doAssign(AssignMenuVo assignMenuVo) {
        //根据角色id删除角色菜单表中的数据
        LambdaQueryWrapper<SysRoleMenu> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysRoleMenu::getRoleId,assignMenuVo.getRoleId());
        sysRoleMenuService.remove(lambdaQueryWrapper);
        //从assignMenuVo中遍历，添加
        assignMenuVo.getMenuIdList().stream().forEach(menuId->{
            if (StringUtils.isEmpty(menuId))
                return;
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setMenuId(menuId);
            sysRoleMenu.setRoleId(assignMenuVo.getRoleId());
            sysRoleMenuService.save(sysRoleMenu);
        });


    }

    @Override
    public List<RouterVo> findUserMenuVoByUserId(Long userId) {
        List<SysMenu> list = new ArrayList<>();
        //判断是否是管理员 如果是就返回全部菜单
        if (userId==1){
            LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysMenu::getStatus,1);
            queryWrapper.orderByAsc(SysMenu::getSortValue);
            list = baseMapper.selectList(queryWrapper);

        }
        //根据userId获取可以操作的菜单
        return null;
    }

    @Override
    public List<String> findUserPermsByUserId(Long userId) {
        return Collections.emptyList();
    }
}
