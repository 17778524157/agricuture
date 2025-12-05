package com.agriculture.user.controller;

import com.agriculture.common.result.Result;
import com.agriculture.common.service.RedisLoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * 登录管理控制器
 * 提供登录状态查询和管理功能
 */
@Slf4j
@RestController
@RequestMapping("/api/login/manage")
@Api(tags = "登录管理")
public class LoginManageController {

    @Autowired
    private RedisLoginService redisLoginService;

    /**
     * 获取在线用户数量
     */
    @GetMapping("/online/count")
    @ApiOperation("获取在线用户数量")
    public Result<Integer> getOnlineUserCount() {
        int count = redisLoginService.getOnlineUserCount();
        return Result.success(count);
    }

    /**
     * 获取所有在线用户
     */
    @GetMapping("/online/users")
    @ApiOperation("获取所有在线用户")
    public Result<Set<String>> getOnlineUsers() {
        Set<String> onlineUsers = redisLoginService.getOnlineUsers();
        return Result.success(onlineUsers);
    }

    /**
     * 强制用户下线
     */
    @PostMapping("/force-offline/{userId}")
    @ApiOperation("强制用户下线")
    public Result forceUserOffline(@PathVariable String userId) {
        try {
            redisLoginService.userOffline(userId);
            return Result.success("用户已强制下线");
        } catch (Exception e) {
            log.error("强制用户下线失败", e);
            return Result.fail("强制下线失败");
        }
    }

    /**
     * 解锁用户账户
     */
    @PostMapping("/unlock/{username}")
    @ApiOperation("解锁用户账户")
    public Result unlockUserAccount(@PathVariable String username) {
        try {
            redisLoginService.unlockUserAccount(username);
            return Result.success("用户账户已解锁");
        } catch (Exception e) {
            log.error("解锁用户账户失败", e);
            return Result.fail("解锁失败");
        }
    }

    /**
     * 检查用户账户是否被锁定
     */
    @GetMapping("/lock-status/{username}")
    @ApiOperation("检查用户账户锁定状态")
    public Result<Boolean> checkUserLockStatus(@PathVariable String username) {
        boolean isLocked = redisLoginService.isUserAccountLocked(username);
        return Result.success(isLocked);
    }

    /**
     * 获取用户登录失败次数
     */
    @GetMapping("/fail-count/{username}")
    @ApiOperation("获取用户登录失败次数")
    public Result<Integer> getLoginFailCount(@PathVariable String username) {
        int failCount = redisLoginService.getLoginFailCount(username);
        return Result.success(failCount);
    }

    /**
     * 清除用户登录失败次数
     */
    @PostMapping("/clear-fail-count/{username}")
    @ApiOperation("清除用户登录失败次数")
    public Result clearLoginFailCount(@PathVariable String username) {
        try {
            redisLoginService.clearLoginFailCount(username);
            return Result.success("登录失败次数已清除");
        } catch (Exception e) {
            log.error("清除登录失败次数失败", e);
            return Result.fail("清除失败");
        }
    }

    /**
     * 检查Token是否在黑名单中
     */
    @GetMapping("/token-blacklist/{token}")
    @ApiOperation("检查Token黑名单状态")
    public Result<Boolean> checkTokenBlacklist(@PathVariable String token) {
        boolean inBlacklist = redisLoginService.isTokenInBlacklist(token);
        return Result.success(inBlacklist);
    }

    /**
     * 将Token加入黑名单
     */
    @PostMapping("/blacklist-token/{token}")
    @ApiOperation("将Token加入黑名单")
    public Result addTokenToBlacklist(@PathVariable String token) {
        try {
            redisLoginService.addTokenToBlacklist(token);
            return Result.success("Token已加入黑名单");
        } catch (Exception e) {
            log.error("Token加入黑名单失败", e);
            return Result.fail("操作失败");
        }
    }
}
