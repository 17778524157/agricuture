package com.agriculture.user.controller;

import com.agriculture.common.result.Result;
import com.agriculture.user.entity.Role;
import com.agriculture.user.entity.RoleMenu;
import com.agriculture.user.mapper.RoleMapper;
import com.agriculture.user.mapper.RoleMenuMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/role")
@Api(tags = "角色管理")
public class RoleController {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @GetMapping
    @ApiOperation("获取角色列表")
    public Result<List<Role>> getRoles() {
        try {
            log.info("获取角色列表请求");
            List<Role> roles = roleMapper.selectList(null);
            log.info("查询到角色数量: {}", roles != null ? roles.size() : 0);
            return Result.success(roles);
        } catch (Exception e) {
            log.error("获取角色列表失败: {}", e.getMessage(), e);
            return Result.fail("获取角色数据失败：" + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @ApiOperation("根据ID获取角色信息")
    public Result<Role> getRoleById(@PathVariable Integer id) {
        try {
            log.info("根据ID获取角色信息，id: {}", id);
            Role role = roleMapper.selectById(id);
            if (role != null) {
                return Result.success(role);
            } else {
                return Result.fail("角色不存在");
            }
        } catch (Exception e) {
            log.error("获取角色信息失败: {}", e.getMessage(), e);
            return Result.fail("获取角色信息失败：" + e.getMessage());
        }
    }

    @PostMapping
    @ApiOperation("创建角色")
    public Result<Boolean> createRole(@RequestBody Role role) {
        try {
            log.info("创建角色请求: {}", role);
            int result = roleMapper.insert(role);
            if (result > 0) {
                log.info("角色创建成功，ID: {}", role.getId());
                return Result.success(true);
            } else {
                return Result.fail("角色创建失败");
            }
        } catch (Exception e) {
            log.error("创建角色失败: {}", e.getMessage(), e);
            return Result.fail("创建角色失败：" + e.getMessage());
        }
    }

    @PutMapping
    @ApiOperation("更新角色")
    public Result<Boolean> updateRole(@RequestBody Role role) {
        try {
            log.info("更新角色请求: {}", role);
            int result = roleMapper.updateById(role);
            if (result > 0) {
                log.info("角色更新成功，ID: {}", role.getId());
                return Result.success(true);
            } else {
                return Result.fail("角色更新失败");
            }
        } catch (Exception e) {
            log.error("更新角色失败: {}", e.getMessage(), e);
            return Result.fail("更新角色失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除角色")
    public Result<Boolean> deleteRole(@PathVariable Integer id) {
        try {
            log.info("删除角色请求，id: {}", id);
            int result = roleMapper.deleteById(id);
            if (result > 0) {
                log.info("角色删除成功，ID: {}", id);
                return Result.success(true);
            } else {
                return Result.fail("角色删除失败");
            }
        } catch (Exception e) {
            log.error("删除角色失败: {}", e.getMessage(), e);
            return Result.fail("删除角色失败：" + e.getMessage());
        }
    }

    @GetMapping("/roleMenu/{roleId}")
    @ApiOperation("获取角色的菜单权限")
    public Result<List<Integer>> getRoleMenus(@PathVariable Integer roleId) {
        try {
            log.info("获取角色菜单权限请求，roleId: {}", roleId);
            List<Integer> menuIds = roleMenuMapper.selectByRoleId(roleId);
            log.info("角色{}的菜单权限数量: {}", roleId, menuIds != null ? menuIds.size() : 0);
            return Result.success(menuIds);
        } catch (Exception e) {
            log.error("获取角色菜单权限失败: {}", e.getMessage(), e);
            return Result.fail("获取角色菜单权限失败：" + e.getMessage());
        }
    }

    @PostMapping("/roleMenu/{roleId}")
    @ApiOperation("分配角色菜单权限")
    public Result<Boolean> assignRoleMenus(@PathVariable Integer roleId, @RequestBody List<Integer> menuIds) {
        try {
            log.info("分配角色菜单权限请求，roleId: {}, menuIds: {}", roleId, menuIds);
            
            // 先删除原有的权限
            roleMenuMapper.deleteByRoleId(roleId);
            
            // 添加新的权限
            if (menuIds != null && !menuIds.isEmpty()) {
                for (Integer menuId : menuIds) {
                    RoleMenu roleMenu = new RoleMenu();
                    roleMenu.setRoleId(roleId);
                    roleMenu.setMenuId(menuId);
                    roleMenuMapper.insert(roleMenu);
                }
            }
            
            log.info("角色{}菜单权限分配成功，分配了{}个菜单", roleId, menuIds != null ? menuIds.size() : 0);
            return Result.success(true);
        } catch (Exception e) {
            log.error("分配角色菜单权限失败: {}", e.getMessage(), e);
            return Result.fail("分配角色菜单权限失败：" + e.getMessage());
        }
    }
}
