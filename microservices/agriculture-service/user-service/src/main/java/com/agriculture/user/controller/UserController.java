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
// 【改造】复用项目已有 JWT 能力（与 UserServiceImpl / 网关同一套），从令牌解析角色，不引入新依赖
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
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

    // 【改造】JWT 密钥，与 UserServiceImpl、网关保持一致（沿用原实现，不新增配置）
    private static final String SECRET_KEY = "agriculture-secret-key";

    /**
     * 【改造】页面菜单级权限控制：判断当前令牌是否属于系统管理员。
     * 角色直接从已通过网关校验的 JWT 中解析，前端无法通过改 localStorage 越权。
     * 仅"用户账号管理"类接口调用此方法；个人改密 / 个人信息等自助接口不调用。
     */
    private boolean isAdmin(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            JWT.require(algorithm).build().verify(token);          // 校验签名与有效期
            DecodedJWT jwt = JWT.decode(token);
            return "ROLE_ADMIN".equals(jwt.getClaim("role").asString());
        } catch (Exception e) {
            log.warn("用户管理接口鉴权失败：{}", e.getMessage());
            return false;
        }
    }

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
    // 【改造】禁止普通角色访问用户管理：从 token 头解析角色，仅管理员可查询用户列表
    @GetMapping("/list")
    @ApiOperation("获取用户列表")
    public Result<List<User>> getUserList(@RequestHeader("token") String token) {
        if (!isAdmin(token)) {
            return Result.error(Constants.CODE_403, "权限不足，仅系统管理员可操作用户账号");
        }
        return Result.success(userService.list());
    }

    /**
     * 根据ID获取用户信息接口
     * @param id 用户ID
     * @return 返回用户信息
     * @throws ServiceException 当用户不存在时抛出异常
     */
    // 【改造】同样限制仅系统管理员可按 ID 查询用户
    @GetMapping("/{id}")
    @ApiOperation("根据ID获取用户信息")
    public Result<User> getUserById(@PathVariable Integer id,
                                   @RequestHeader("token") String token) {
        if (!isAdmin(token)) {
            return Result.error(Constants.CODE_403, "权限不足，仅系统管理员可操作用户账号");
        }
        User user = userService.getById(id);
        if (user == null) {
            throw new ServiceException("用户不存在");
        }
        return Result.success(user);
    }

    /**
     * 创建用户接口
     * @param user 用户信息
     * @return 返回创建结果
     */
    @PostMapping
    @ApiOperation("创建用户")
    // 【改造】仅系统管理员可新增账号
    public Result<Boolean> createUser(@Valid @RequestBody User user,
                                      @RequestHeader("token") String token) {
        if (!isAdmin(token)) {
            return Result.error(Constants.CODE_403, "权限不足，仅系统管理员可操作用户账号");
        }
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
    // 【改造】仅系统管理员可编辑用户（编辑弹窗中的"角色"下拉即"分配角色"功能）
    public Result<Boolean> updateUser(@Valid @RequestBody User user,
                                      @RequestHeader("token") String token) {
        if (!isAdmin(token)) {
            return Result.error(Constants.CODE_403, "权限不足，仅系统管理员可操作用户账号");
        }
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
    // 【改造】仅系统管理员可删除用户
    public Result<Boolean> deleteUser(@PathVariable Integer id,
                                      @RequestHeader("token") String token) {
        if (!isAdmin(token)) {
            return Result.error(Constants.CODE_403, "权限不足，仅系统管理员可操作用户账号");
        }
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
    public Result<Boolean> checkUsername(@RequestParam(value = "username", required = false) String username) {
        log.info("检查用户名是否存在: {}", username);
        try {
            if (username == null || username.trim().isEmpty()) {
                return Result.fail("用户名不能为空");
            }
            boolean exists = userService.isUsernameExists(username);
            return Result.success(!exists);
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
    public Result<Boolean> checkPhone(@RequestParam(value = "phone", required = false) String phone) {
        log.info("检查手机号是否存在: {}", phone);
        try {
            if (phone == null || phone.trim().isEmpty()) {
                return Result.fail("手机号不能为空");
            }
            boolean exists = userService.isPhoneExists(phone);
            return Result.success(!exists);
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
    public Result<Boolean> checkEmail(@RequestParam(value = "email", required = false) String email) {
        log.info("检查邮箱是否存在: {}", email);
        try {
            if (email == null || email.trim().isEmpty()) {
                return Result.fail("邮箱不能为空");
            }
            boolean exists = userService.isEmailExists(email);
            return Result.success(!exists);
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
     * 【改造-新增】系统管理员重置指定用户密码为默认密码 123456
     */
    @PutMapping("/resetPwd/{id}")
    @ApiOperation("重置用户密码（管理员）")
    public Result resetPassword(@PathVariable Integer id,
                                @RequestHeader("token") String token) {
        if (!isAdmin(token)) {
            return Result.error(Constants.CODE_403, "权限不足，仅系统管理员可操作用户账号");
        }
        return userService.resetPassword(id);
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
