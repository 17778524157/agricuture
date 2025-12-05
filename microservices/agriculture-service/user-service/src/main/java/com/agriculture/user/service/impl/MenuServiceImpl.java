package com.agriculture.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.agriculture.user.entity.Menu;
import com.agriculture.user.mapper.MenuMapper;
import com.agriculture.user.service.IMenuService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单服务实现类
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements IMenuService {
    
    @Override
    public List<Menu> findMenus(String name) {
        // 创建查询条件
        QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort_num");
        if (StrUtil.isNotBlank(name)) {
            queryWrapper.like("name", name);
        }
        
        // 查询所有菜单
        List<Menu> list = list(queryWrapper);
        
        // 构建树形结构：先找出所有一级菜单（pid为null的菜单）
        List<Menu> parentNodes = list.stream()
                .filter(menu -> menu.getPid() == null)
                .collect(Collectors.toList());
        
        // 为每个一级菜单设置子菜单
        for (Menu menu : parentNodes) {
            menu.setChildren(list.stream()
                    .filter(m -> menu.getId().equals(m.getPid()))
                    .collect(Collectors.toList()));
        }
        
        return parentNodes;
    }
    
}