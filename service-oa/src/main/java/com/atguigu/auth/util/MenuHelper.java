package com.atguigu.auth.util;

import com.atguigu.model.system.SysMenu;

import java.util.ArrayList;
import java.util.List;

public class MenuHelper {
    public static List<SysMenu> buildTree(List<SysMenu> list){
        List<SysMenu> trees = new ArrayList<>();
        for (SysMenu sysMenu:list){
            if (sysMenu.getParentId().longValue()==0){//递归入口
                trees.add(getChildren(sysMenu,list));
            }
        }
        return trees;
    }
    //递归调用
    public static SysMenu getChildren(SysMenu sysMenu,List<SysMenu> sysMenuList){
        sysMenu.setChildren(new ArrayList<SysMenu>());
        for(SysMenu it:sysMenuList){
            if (sysMenu.getId().longValue() == it.getParentId().longValue()){
                if (sysMenu.getChildren()==null){
                    sysMenu.setChildren(new ArrayList<SysMenu>());
                }
                sysMenu.getChildren().add(getChildren(it,sysMenuList));
            }
        }
        return sysMenu;
    }
}
