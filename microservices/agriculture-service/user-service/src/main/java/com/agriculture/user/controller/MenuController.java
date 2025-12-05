package com.agriculture.user.controller;

import com.agriculture.common.result.Result;
import com.agriculture.user.entity.Menu;
import com.agriculture.user.mapper.MenuMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/menu")
@Api(tags = "菜单管理")
public class MenuController {

    @Autowired
    private MenuMapper menuMapper;

    @GetMapping
    @ApiOperation("获取菜单列表")
    public Result<List<Menu>> getMenus() {
        try {
            log.info("获取菜单列表请求");
            List<Menu> menus = menuMapper.selectList(null);
            log.info("查询到菜单数量: {}", menus != null ? menus.size() : 0);
            return Result.success(menus);
        } catch (Exception e) {
            log.error("获取菜单列表失败: {}", e.getMessage(), e);
            return Result.fail("获取菜单数据失败：" + e.getMessage());
        }
    }

    @GetMapping("/ids")
    @ApiOperation("获取所有菜单ID")
    public Result<List<Integer>> getMenuIds() {
        try {
            log.info("获取菜单ID列表请求");
            List<Integer> ids = menuMapper.selectAllIds();
            log.info("查询到菜单ID数量: {}", ids != null ? ids.size() : 0);
            return Result.success(ids);
        } catch (Exception e) {
            log.error("获取菜单ID列表失败: {}", e.getMessage(), e);
            return Result.fail("获取菜单ID失败：" + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @ApiOperation("根据ID获取菜单信息")
    public Result<Menu> getMenuById(@PathVariable Integer id) {
        try {
            log.info("根据ID获取菜单信息，id: {}", id);
            Menu menu = menuMapper.selectById(id);
            if (menu != null) {
                return Result.success(menu);
            } else {
                return Result.fail("菜单不存在");
            }
        } catch (Exception e) {
            log.error("获取菜单信息失败: {}", e.getMessage(), e);
            return Result.fail("获取菜单信息失败：" + e.getMessage());
        }
    }

    @PostMapping
    @ApiOperation("创建菜单")
    public Result<Boolean> createMenu(@RequestBody Menu menu) {
        try {
            log.info("创建菜单请求: {}", menu);
            int result = menuMapper.insert(menu);
            if (result > 0) {
                log.info("菜单创建成功，ID: {}", menu.getId());
                return Result.success(true);
            } else {
                return Result.fail("菜单创建失败");
            }
        } catch (Exception e) {
            log.error("创建菜单失败: {}", e.getMessage(), e);
            return Result.fail("创建菜单失败：" + e.getMessage());
        }
    }

    @PutMapping
    @ApiOperation("更新菜单")
    public Result<Boolean> updateMenu(@RequestBody Menu menu) {
        try {
            log.info("更新菜单请求: {}", menu);
            int result = menuMapper.updateById(menu);
            if (result > 0) {
                log.info("菜单更新成功，ID: {}", menu.getId());
                return Result.success(true);
            } else {
                return Result.fail("菜单更新失败");
            }
        } catch (Exception e) {
            log.error("更新菜单失败: {}", e.getMessage(), e);
            return Result.fail("更新菜单失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除菜单")
    public Result<Boolean> deleteMenu(@PathVariable Integer id) {
        try {
            log.info("删除菜单请求，id: {}", id);
            int result = menuMapper.deleteById(id);
            if (result > 0) {
                log.info("菜单删除成功，ID: {}", id);
                return Result.success(true);
            } else {
                return Result.fail("菜单删除失败");
            }
        } catch (Exception e) {
            log.error("删除菜单失败: {}", e.getMessage(), e);
            return Result.fail("删除菜单失败：" + e.getMessage());
        }
    }

    @GetMapping("/test")
    @ApiOperation("测试接口")
    public Result<String> test() {
        try {
            log.info("菜单服务测试接口被调用");
            return Result.success("菜单服务测试成功");
        } catch (Exception e) {
            log.error("菜单服务测试接口异常: {}", e.getMessage(), e);
            return Result.fail("测试失败：" + e.getMessage());
        }
    }
}