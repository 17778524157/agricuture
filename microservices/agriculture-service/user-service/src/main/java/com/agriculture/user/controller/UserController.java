package com.agriculture.user.controller;

import cn.hutool.core.util.StrUtil;
import com.agriculture.common.annotation.LogOperation;
import com.agriculture.common.constant.Constants;
import com.agriculture.common.exception.ServiceException;
import com.agriculture.common.result.Result;
import com.agriculture.common.utils.RegexUtils;
import com.agriculture.user.dto.LoginRequest;
import com.agriculture.user.dto.LoginResponse;
import com.agriculture.user.dto.RegisterRequest;
import com.agriculture.user.dto.RegisterResponse;
import com.agriculture.user.dto.PersonalInfoRequest;
import com.agriculture.user.dto.PersonalInfoResponse;
import com.agriculture.user.dto.PasswordChangeRequest;
import com.agriculture.user.entity.User;
import com.agriculture.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

/**
 * 用户控制器
 * 提供用户相关的HTTP接口，包括登录、注册、用户信息管理等功能
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
@Api(tags = "用户管理")
public class UserController {

    @Autowired
    private UserService userService; // 用户服务接口，用于处理用户相关的业务逻辑

    /**
     * 用户登录接口
     * @param request 登录请求参数，包含用户名和密码
     * @return 返回登录结果和用户信息
     */
    @PostMapping("/login")                    // HTTP POST请求映射到/login路径
    @ApiOperation("用户登录")                // Swagger API文档注解，描述接口功能
    @LogOperation(value = "用户登录", type = "LOGIN")  // 自定义日志注解，记录登录操作日志
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {  // 接收登录请求体
        log.info("用户登录请求: {}", request.getUsername());  // 记录登录请求日志，输出用户名
        String username = request.getUsername();  // 从请求中获取用户名
        String password = request.getPassword();  // 从请求中获取密码
    // 校验用户名和密码是否为空
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
            return Result.error(Constants.CODE_400, "参数错误");  // 参数为空时返回错误信息
        }
        try {
        // 调用用户服务处理登录逻辑
            LoginResponse response = userService.login(request);
            return Result.success(Constants.CODE_200, response);  // 登录成功返回用户信息
        } catch (ServiceException e) {

        // 处理业务异常
            return Result.error(e.getCode(), e.getMessage());  // 返回业务异常信息
        } catch (Exception e) {

        // 处理其他异常
            log.error("登录异常", e);  // 记录异常日志
            return Result.error("登录失败，请稍后重试");  // 返回通用错误信息
        }
    }

    /**
     * 获取用户列表接口
     * @return 返回所有用户列表
     */
    @GetMapping("/list")
    @ApiOperation("获取用户列表")
    public List<User> getUserList() {
        return userService.list();
    }

    /**
     * 根据ID获取用户信息接口
     * @param id 用户ID
     * @return 返回用户信息
     * @throws ServiceException 当用户不存在时抛出异常
     */
    @GetMapping("/{id}")
    @ApiOperation("根据ID获取用户信息")
    public User getUserById(@PathVariable Integer id) {
        User user = userService.getById(id);
        if (user == null) {
            throw new ServiceException("用户不存在");
        }
        return user;
    }

    /**
     * 创建用户接口
     * @param user 用户信息
     * @return 返回创建结果
     */
    @PostMapping
    @ApiOperation("创建用户")
    public Result<Boolean> createUser(@Valid @RequestBody User user) {
        try {
            log.info("创建用户请求: {}", user);
            boolean result = userService.save(user);
            if (result) {
                log.info("用户创建成功，ID: {}", user.getId());
                return Result.success(true);
            } else {
                return Result.fail("用户创建失败");
            }
        } catch (Exception e) {
            log.error("创建用户失败: {}", e.getMessage(), e);
            return Result.fail("创建用户失败：" + e.getMessage());
        }
    }

    /**
     * 更新用户接口
     * @param user 用户信息
     * @return 返回更新结果
     */
    @PutMapping
    @ApiOperation("更新用户")
    public Result<Boolean> updateUser(@Valid @RequestBody User user) {
        try {
            log.info("更新用户请求: {}", user);
            boolean result = userService.updateById(user);
            if (result) {
                log.info("用户更新成功，ID: {}", user.getId());
                return Result.success(true);
            } else {
                return Result.fail("用户更新失败");
            }
        } catch (Exception e) {
            log.error("更新用户失败: {}", e.getMessage(), e);
            return Result.fail("更新用户失败：" + e.getMessage());
        }
    }

    /**
     * 删除用户接口
     * @param id 用户ID
     * @return 返回删除结果
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除用户")
    public Result<Boolean> deleteUser(@PathVariable Integer id) {
        try {
            log.info("删除用户请求，id: {}", id);
            boolean result = userService.removeById(id);
            if (result) {
                log.info("用户删除成功，ID: {}", id);
                return Result.success(true);
            } else {
                return Result.fail("用户删除失败");
            }
        } catch (Exception e) {
            log.error("删除用户失败: {}", e.getMessage(), e);
            return Result.fail("删除用户失败：" + e.getMessage());
        }
    }
    /**
     * 发送手机验证码接口
     * @param phone 手机号码
     * @param session HTTP会话
     * @return 返回发送结果
     */
    @PostMapping("/sendCode")
    public Result sendCode(@RequestParam("phone") String phone, HttpSession session) {
        log.info("收到发送验证码请求，手机号: {}", phone);
        try {
            Result result = userService.sendCode(phone, session);
            log.info("发送验证码结果: {}", result);
            return result;
        } catch (Exception e) {
            log.error("发送验证码失败", e);
            return Result.fail("发送验证码失败: " + e.getMessage());
        }
    }
    
    /**
     * 手机号登录接口
     * @param request 登录请求参数
     * @param session HTTP会话
     * @return 返回登录结果和用户信息
     */
    @PostMapping("/phoneLogin")
    @ApiOperation("手机号登录")
    @LogOperation(value = "手机号登录", type = "LOGIN")
    public Result<LoginResponse> phoneLogin(@RequestBody LoginRequest request, HttpSession session) {
        log.info("手机号登录请求: {}", request.getPhone());
        try {
            return userService.phoneLogin(request, session);
        } catch (ServiceException e) {
            log.error("手机号登录失败: {}", e.getMessage());
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("手机号登录异常", e);
            return Result.error("登录失败，请稍后重试");
        }
    }

    /**
     * 用户登出接口
     * @param token 登录令牌
     * @param userId 用户ID
     * @return 返回登出结果
     */
    @PostMapping("/logout")
    @ApiOperation("用户登出")
    @LogOperation(value = "用户登出", type = "LOGOUT")
    public Result logout(@RequestHeader("token") String token, @RequestParam("userId") String userId) {
        log.info("用户登出请求: {}", userId);
        return userService.logout(token, userId);
    }

    /**
     * 用户注册接口
     * @param request 注册请求参数
     * @param session HTTP会话
     * @return 返回注册结果和用户信息
     */
    @PostMapping("/register")
    @ApiOperation("用户注册")
    @LogOperation(value = "用户注册", type = "REGISTER")
    public Result<RegisterResponse> register(@Valid @RequestBody RegisterRequest request, HttpSession session) {
        log.info("用户注册请求: {}", request.getUsername());
        try {
            Result<RegisterResponse> result = userService.register(request, session);
            log.info("用户注册结果: {}", result);
            return result;
        } catch (Exception e) {
            log.error("用户注册失败", e);
            return Result.fail("注册失败: " + e.getMessage());
        }
    }

    /**
     * 检查用户名是否已存在接口
     * @param username 用户名
     * @return 返回检查结果，true表示可用，false表示已存在
     */
    @GetMapping("/checkUsername")
    @ApiOperation("检查用户名是否已存在")
    public Result<Boolean> checkUsername(@RequestParam("username") String username) {
        log.info("检查用户名是否存在: {}", username);
        try {
            boolean exists = userService.isUsernameExists(username);
            return Result.success(!exists); // 返回true表示可用，false表示已存在
        } catch (Exception e) {
            log.error("检查用户名失败", e);
            return Result.fail("检查失败");
        }
    }

    /**
     * 检查手机号是否已存在接口
     * @param phone 手机号码
     * @return 返回检查结果，true表示可用，false表示已存在
     */
    @GetMapping("/checkPhone")
    @ApiOperation("检查手机号是否已存在")
    public Result<Boolean> checkPhone(@RequestParam("phone") String phone) {
        log.info("检查手机号是否存在: {}", phone);
        try {
            boolean exists = userService.isPhoneExists(phone);
            return Result.success(!exists); // 返回true表示可用，false表示已存在
        } catch (Exception e) {
            log.error("检查手机号失败", e);
            return Result.fail("检查失败");
        }
    }

    /**
     * 检查邮箱是否已存在接口
     * @param email 邮箱地址
     * @return 返回检查结果，true表示可用，false表示已存在
     */
    @GetMapping("/checkEmail")
    @ApiOperation("检查邮箱是否已存在")
    public Result<Boolean> checkEmail(@RequestParam("email") String email) {
        log.info("检查邮箱是否存在: {}", email);
        try {
            boolean exists = userService.isEmailExists(email);
            return Result.success(!exists); // 返回true表示可用，false表示已存在
        } catch (Exception e) {
            log.error("检查邮箱失败", e);
            return Result.fail("检查失败");
        }
    }
    
    /**
     * 获取个人信息接口
     * @param userId 用户ID
     * @return 返回个人信息
     */
    @GetMapping("/personalInfo")
    @ApiOperation("获取个人信息")
    public Result<PersonalInfoResponse> getPersonalInfo(@RequestHeader("userId") Integer userId) {
        log.info("获取个人信息请求，用户ID: {}", userId);
        try {
            return userService.getPersonalInfo(userId);
        } catch (Exception e) {
            log.error("获取个人信息失败", e);
            return Result.fail("获取个人信息失败");
        }
    }
    
    /**
     * 更新个人信息接口
     * @param userId 用户ID
     * @param request 个人信息更新请求
     * @return 返回更新后的个人信息
     */
    @PutMapping("/personalInfo")
    @ApiOperation("更新个人信息")
    public Result<PersonalInfoResponse> updatePersonalInfo(
            @RequestHeader("userId") Integer userId,
            @RequestBody @Valid PersonalInfoRequest request) {
        log.info("更新个人信息请求，用户ID: {}, 请求: {}", userId, request);
        try {
            return userService.updatePersonalInfo(userId, request);
        } catch (Exception e) {
            log.error("更新个人信息失败", e);
            return Result.fail("更新个人信息失败");
        }
    }
    
    /**
     * 修改密码接口
     * @param userId 用户ID
     * @param request 密码修改请求
     * @return 返回修改结果
     */
    @PutMapping("/changePassword")
    @ApiOperation("修改密码")
    public Result changePassword(
            @RequestHeader("userId") Integer userId,
            @RequestBody @Valid PasswordChangeRequest request) {
        log.info("修改密码请求，用户ID: {}", userId);
        try {
            return userService.changePassword(userId, request);
        } catch (Exception e) {
            log.error("修改密码失败", e);
            return Result.fail("修改密码失败");
        }
    }
    
    /**
     * 上传头像接口
     * @param userId 用户ID
     * @param avatarUrl 头像URL
     * @return 返回上传结果
     */
    @PostMapping("/uploadAvatar")
    @ApiOperation("上传头像")
    public Result<String> uploadAvatar(
            @RequestHeader("userId") Integer userId,
            @RequestParam("avatarUrl") String avatarUrl) {
        log.info("上传头像请求，用户ID: {}, 头像URL: {}", userId, avatarUrl);
        try {
            return userService.uploadAvatar(userId, avatarUrl);
        } catch (Exception e) {
            log.error("上传头像失败", e);
            return Result.fail("上传头像失败");
        }
    }
    
    /**
     * 根据用户名获取用户信息接口
     * @param username 用户名
     * @return 返回用户信息
     */
    @GetMapping("/username/{username}")
    @ApiOperation("根据用户名获取用户信息")
    public Result<User> getUserByUsername(@PathVariable("username") String username) {
        log.info("根据用户名获取用户信息请求: {}", username);
        try {
            User user = userService.getUserByUsername(username);
            if (user == null) {
                return Result.fail("用户不存在");
            }
            return Result.success(user);
        } catch (Exception e) {
            log.error("根据用户名获取用户信息失败", e);
            return Result.fail("获取用户信息失败");
        }
    }
}
