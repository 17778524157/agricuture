package com.agriculture.user.controller;

import com.agriculture.common.constant.Constants;
import com.agriculture.common.result.Result;
import com.agriculture.user.entity.Role;
import com.agriculture.user.entity.RoleMenu;
import com.agriculture.user.mapper.RoleMapper;
import com.agriculture.user.mapper.RoleMenuMapper;
// 【改造】复用已有 JWT，从令牌解析角色，限制只有管理员能改动固定角色
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
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

    // 【改造】JWT 密钥，与 UserServiceImpl/网关/UserController 保持一致
    private static final String SECRET_KEY = "agriculture-secret-key";

    /**
     * 【改造】角色在新系统中固定为 3 个，仅系统管理员可新增/修改/删除角色、分配角色菜单。
     * 读接口不做限制（仅返回 3 个固定角色，且用户表单的角色下拉需要）。
     */
    private boolean isAdmin(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            JWT.require(algorithm).build().verify(token);
            DecodedJWT jwt = JWT.decode(token);
            return "ROLE_ADMIN".equals(jwt.getClaim("role").asString());
        } catch (Exception e) {
            return false;
        }
    }

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
    // 【改造】仅系统管理员可创建角色（角色固定为 3 个，正常无需新建）
    public Result<Boolean> createRole(@RequestBody Role role,
                                      @RequestHeader("token") String token) {
        if (!isAdmin(token)) {
            return Result.error(Constants.CODE_403, "权限不足，仅系统管理员可维护角色");
        }
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
    // 【改造】仅系统管理员可更新角色
    public Result<Boolean> updateRole(@RequestBody Role role,
                                      @RequestHeader("token") String token) {
        if (!isAdmin(token)) {
            return Result.error(Constants.CODE_403, "权限不足，仅系统管理员可维护角色");
        }
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
    // 【改造】仅系统管理员可删除角色
    public Result<Boolean> deleteRole(@PathVariable Integer id,
                                      @RequestHeader("token") String token) {
        if (!isAdmin(token)) {
            return Result.error(Constants.CODE_403, "权限不足，仅系统管理员可维护角色");
        }
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
    // 【改造】仅系统管理员可分配角色菜单
    public Result<Boolean> assignRoleMenus(@PathVariable Integer roleId,
                                          @RequestBody List<Integer> menuIds,
                                          @RequestHeader("token") String token) {
        if (!isAdmin(token)) {
            return Result.error(Constants.CODE_403, "权限不足，仅系统管理员可维护角色");
        }
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
