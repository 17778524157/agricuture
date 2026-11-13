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
    /**
     * 根据菜单名称查询菜单列表，并构建树形结构
     * @param name 菜单名称，可以为空，如果为空则查询所有菜单
     * @return 返回构建好的树形菜单列表
     */
    public List<Menu> findMenus(String name) {
        // 创建查询条件
        QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
        // 设置查询结果按sort_num字段升序排列
        queryWrapper.orderByAsc("sort_num");
        // 如果name不为空，则添加模糊查询条件
        if (StrUtil.isNotBlank(name)) {
            queryWrapper.like("name", name);
        }
        
        // 查询所有菜单
        List<Menu> list = list(queryWrapper);
        
        // 构建树形结构：先找出所有一级菜单（pid为null的菜单）
        List<Menu> parentNodes = list.stream()
                .filter(menu -> menu.getPid() == null)
                .collect(Collectors.toList());
        
        // 为每个一级菜单设置子菜单0
        for (Menu menu : parentNodes) {
            menu.setChildren(list.stream()
                    .filter(m -> menu.getId().equals(m.getPid()))
                    .collect(Collectors.toList()));
        }
        
        return parentNodes;
    }
    
}