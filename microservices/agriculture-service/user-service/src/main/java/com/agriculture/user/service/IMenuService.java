package com.agriculture.user.service;

import com.agriculture.user.entity.Menu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 菜单服务接口
 */
public interface IMenuService extends IService<Menu> {
    
    /**
     * 根据名称查询菜单列表（树形结构）
     */
    List<Menu> findMenus(String name);
    
}