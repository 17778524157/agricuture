package com.agriculture.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.agriculture.common.constant.Constants;
import com.agriculture.common.exception.ServiceException;
import com.agriculture.common.result.Result;
import com.agriculture.common.utils.RegexPatterns;
import com.agriculture.common.utils.RegexUtils;
import com.agriculture.user.dto.LoginRequest;
import com.agriculture.user.dto.LoginResponse;
import com.agriculture.user.dto.RegisterRequest;
import com.agriculture.user.dto.RegisterResponse;
import com.agriculture.user.dto.PersonalInfoRequest;
import com.agriculture.user.dto.PersonalInfoResponse;
import com.agriculture.user.dto.PasswordChangeRequest;
import com.agriculture.user.entity.Menu;
import com.agriculture.user.entity.User;
import com.agriculture.user.mapper.MenuMapper;
import com.agriculture.user.mapper.RoleMapper;
import com.agriculture.user.mapper.RoleMenuMapper;
import com.agriculture.user.mapper.UserMapper;
import com.agriculture.user.service.IMenuService;
import com.agriculture.user.service.UserService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import org.springframework.beans.factory.annotation.Autowired;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.agriculture.common.utils.RedisConstants.LOGIN_CODE_KEY;
import static com.agriculture.common.utils.RedisConstants.LOGIN_CODE_TTL;
import com.agriculture.common.service.RedisLoginService;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private static final String SECRET_KEY = "agriculture-secret-key";
    private static final long EXPIRE_TIME = 24 * 60 * 60 * 1000; // 24小时

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private MenuMapper menuMapper;
    @Autowired
    private RoleMenuMapper roleMenuMapper;
    @Autowired
    private IMenuService menuService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    
    @Autowired
    private RedisLoginService redisLoginService;

    @Override
    public LoginResponse login(LoginRequest request) {
        String loginKey = request.getUsername() != null ? request.getUsername() : request.getPhone();
        
        // 1. 检查登录权限（账户锁定、失败次数等）
        String permissionCheck = redisLoginService.checkLoginPermission(loginKey);
        if (permissionCheck != null) {
            throw new ServiceException(Constants.CODE_600, permissionCheck);
        }
        
        // 2. 查询用户
        User user = findByUser(request);
        if (user == null) {
            // 处理登录失败
            redisLoginService.handleLoginFailure(loginKey);
            throw new ServiceException("用户不存在");
        }
        
        // 3. 验证角色
        if (request.getRole() == null || request.getRole().trim().isEmpty()) {
            throw new ServiceException(Constants.CODE_600, "请选择用户角色");
        }
        
        // 验证用户角色是否匹配
        if (!request.getRole().equals(user.getRole())) {
            // 处理登录失败
            redisLoginService.handleLoginFailure(loginKey);
            throw new ServiceException(Constants.CODE_600, "用户角色不匹配，请选择正确的角色");
        }
        
        // 4. 验证密码 - 仅在用户名登录时验证，手机号登录已通过验证码验证
        if (request.getUsername() != null && !request.getUsername().isEmpty() && 
            request.getPassword() != null && !request.getPassword().isEmpty()) {
            String encryptedPassword = DigestUtils.md5DigestAsHex(request.getPassword().getBytes());
            if (!encryptedPassword.equals(user.getPassword())) {
                // 处理登录失败
                redisLoginService.handleLoginFailure(loginKey);
                throw new ServiceException(Constants.CODE_600, "用户名或密码错误");
            }
        }
        
        // 5. 生成Token
        String token = generateToken(user);
        
        // 6. 构建响应
        LoginResponse response = new LoginResponse();
        BeanUtils.copyProperties(user, response);
        response.setToken(token);
        
        // 调试日志：检查头像URL
        log.info("登录响应构建完成，用户ID: {}, 头像URL: {}", user.getId(), user.getAvatarUrl());

        // 7. 设置默认菜单（根据角色）
        List<Menu> menus = getRoleMenus(user.getRole());
        response.setMenus(menus);

        // 8. 处理登录成功（保存会话、用户上线等）
        redisLoginService.handleLoginSuccess(loginKey, token, response);

        return response;
    }

    @Override
    public User findByUser(LoginRequest request) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        
        // Support both username and phone login
        if (request.getUsername() != null && !request.getUsername().isEmpty()) {
            queryWrapper.eq("username", request.getUsername());
        } else if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            queryWrapper.eq("phone", request.getPhone());
        }
        
        return userMapper.selectOne(queryWrapper);
    }

    @Override
/**
 * 生成JWT令牌的方法
 * @param user 用户对象，包含用户ID、用户名和角色信息
 * @return String 返回生成的JWT令牌字符串
 */
    public String generateToken(User user) {
        // 添加空指针检查
        if (user == null) {
            throw new ServiceException(Constants.CODE_500, "生成Token失败：用户信息为空");
        }
        // 设置过期时间，当前时间加上过期时长
        Date expireDate = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        // 使用JWT创建令牌
        return JWT.create()
                // 添加用户ID声明
                .withClaim("userId", user.getId())
                // 添加用户名声明
                .withClaim("username", user.getUsername())
                // 添加角色声明
                .withClaim("role", user.getRole())
                // 设置过期时间
                .withExpiresAt(expireDate)
                // 使用HMAC256算法和密钥进行签名
                .sign(Algorithm.HMAC256(SECRET_KEY));
    }

    /**
     * 发送验证码
     *
     * @param phone
     * @param session
     */
    @Override
    public Result sendCode(String phone, HttpSession session) {
        log.info("收到发送验证码请求，手机号: {}", phone);
        
        //验证手机号是否正确
        if (RegexUtils.isPhoneInvalid(phone)) {
            log.warn("手机号格式不正确: {}", phone);
            throw new ServiceException(Constants.CODE_600, "手机号不正确");
        }
        
        //生成6位随机数字验证码
        String code = RandomUtil.randomNumbers(6);
        log.info("生成验证码: {}", code);
        
        //存储验证码到Redis，设置2分钟过期时间
        String redisKey = LOGIN_CODE_KEY + phone;
        stringRedisTemplate.opsForValue().set(redisKey, code, LOGIN_CODE_TTL, TimeUnit.MINUTES);
        log.info("验证码已存储到Redis，key: {}, 过期时间: {}分钟", redisKey, LOGIN_CODE_TTL);
        
        //在实际环境中，这里应该集成真实的短信发送服务
        log.info("发送短信验证码成功，手机号: {}, 验证码: {}", phone, code);
        return Result.success();
    }

    @Override
    public Result<LoginResponse> phoneLogin(LoginRequest request, HttpSession session) {
        // 1. 验证手机号格式
        if(RegexUtils.isPhoneInvalid(request.getPhone())){
            throw new ServiceException(Constants.CODE_600, "手机号格式错误");
        }
        
        // 2. 检查登录权限
        String permissionCheck = redisLoginService.checkLoginPermission(request.getPhone());
        if (permissionCheck != null) {
            return Result.fail(permissionCheck);
        }
        
        // 3. 从redis获取验证码并校验
        String cacheCode = stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY + request.getPhone());
        String code = request.getCode();
        // 判断验证码是否正确
        if(cacheCode == null || !cacheCode.equals(code)){
            // 处理登录失败
            redisLoginService.handleLoginFailure(request.getPhone());
            return Result.fail("验证码错误");
        }
        
        // 4. 验证码正确，根据手机号查询用户信息
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("phone", request.getPhone()));
        
        // 检查用户是否存在
        if (user == null) {
            // 处理登录失败
            redisLoginService.handleLoginFailure(request.getPhone());
            return Result.fail("用户不存在，请先注册");
        }
        
        // 5. 验证角色
        if (request.getRole() == null || request.getRole().trim().isEmpty()) {
            return Result.fail("请选择用户角色");
        }
        
        // 验证用户角色是否匹配
        if (!request.getRole().equals(user.getRole())) {
            // 处理登录失败
            redisLoginService.handleLoginFailure(request.getPhone());
            return Result.fail("用户角色不匹配，请选择正确的角色");
        }
        
        // 6. 生成Token
        String token = generateToken(user);
        
        // 7. 构建响应
        LoginResponse response = new LoginResponse();
        BeanUtils.copyProperties(user, response);
        response.setToken(token);

        // 8. 设置默认菜单（根据角色）
        List<Menu> menus = getRoleMenus(user.getRole());
        response.setMenus(menus);

        // 9. 处理登录成功
        redisLoginService.handleLoginSuccess(request.getPhone(), token, response);

        return Result.success(response);
    }

    @Override
    public Result logout(String token, String userId) {
        try {
            // 处理用户登出
            redisLoginService.handleLogout(token, userId);
            return Result.success("登出成功");
        } catch (Exception e) {
            log.error("用户登出失败", e);
            return Result.fail("登出失败");
        }
    }

    /**
     * 根据角色获取菜单列表
     */
    private List<Menu> getRoleMenus(String roleFlag) {
        Integer roleId = roleMapper.selectByFlag(roleFlag);
        // 当前角色的所有菜单id集合
        List<Integer> menuIds = roleMenuMapper.selectByRoleId(roleId);

        // 查出系统所有的菜单(树形)
        List<Menu> menus = menuService.findMenus("");
        // new一个最后筛选完成之后的list
        List<Menu> roleMenus = new ArrayList<>();
        // 筛选当前用户角色的菜单
        for (Menu menu : menus) {
            if (menuIds.contains(menu.getId())) {
                // 如果当前菜单的ID在角色可以访问的菜单ID集合中，则将其添加到roleMenus列表中。
                roleMenus.add(menu);
            }
            List<Menu> children = menu .getChildren();
            // removeIf()  移除 children 里面不在 menuIds集合中的 元素
            children.removeIf(child -> !menuIds.contains(child.getId()));
        }
        return roleMenus;
    }

    /**
     * 用户注册
     */
    @Override
    public Result<RegisterResponse> register(RegisterRequest request, HttpSession session) {
        log.info("收到用户注册请求，用户名: {}, 手机号: {}", request.getUsername(), request.getPhone());
        
        try {
            // 1. 验证请求参数
            validateRegisterRequest(request);
            
            // 2. 检查用户名是否已存在
            if (isUsernameExists(request.getUsername())) {
                log.warn("用户名已存在: {}", request.getUsername());
                return Result.fail("用户名已存在，请选择其他用户名");
            }
            
            // 3. 检查手机号是否已存在
            if (isPhoneExists(request.getPhone())) {
                log.warn("手机号已存在: {}", request.getPhone());
                return Result.fail("手机号已被注册，请使用其他手机号");
            }
            
            // 4. 检查邮箱是否已存在（如果提供了邮箱）
            if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
                if (isEmailExists(request.getEmail())) {
                    log.warn("邮箱已存在: {}", request.getEmail());
                    return Result.fail("邮箱已被注册，请使用其他邮箱");
                }
            }
            
            // 5. 验证验证码
            String cacheCode = stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY + request.getPhone());
            if (cacheCode == null || !cacheCode.equals(request.getCode())) {
                log.warn("验证码错误，手机号: {}, 输入验证码: {}", request.getPhone(), request.getCode());
                return Result.fail("验证码错误或已过期，请重新获取");
            }
            
            // 6. 创建用户对象
            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(DigestUtils.md5DigestAsHex(request.getPassword().getBytes())); // MD5加密密码
            user.setNickname(request.getNickname());
            user.setEmail(request.getEmail());
            user.setPhone(request.getPhone());
            user.setAddress(request.getAddress());
            user.setRole(request.getRole() != null ? request.getRole() : "ROLE_USER");
            user.setCreateTime(LocalDateTime.now());
            
            // 7. 保存用户到数据库
            int insertResult = userMapper.insert(user);
            if (insertResult <= 0) {
                log.error("用户注册失败，保存到数据库失败，insertResult: {}", insertResult);
                return Result.fail("注册失败，请稍后重试");
            }
            
            // 检查用户ID是否生成成功
            if (user.getId() == null) {
                log.error("用户注册失败，用户ID未生成，insertResult: {}", insertResult);
                return Result.fail("注册失败，请稍后重试");
            }
            
            log.info("用户数据已保存到数据库，用户ID: {}, insertResult: {}", user.getId(), insertResult);
            
            // 8. 删除已使用的验证码
            stringRedisTemplate.delete(LOGIN_CODE_KEY + request.getPhone());
            
            // 9. 构建响应
            RegisterResponse response = new RegisterResponse();
            response.setUserId(user.getId());
            response.setUsername(user.getUsername());
            response.setNickname(user.getNickname());
            response.setEmail(user.getEmail());
            response.setPhone(user.getPhone());
            response.setRole(user.getRole());
            response.setCreateTime(user.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            response.setMessage("注册成功，欢迎加入酸枣种植风险预警智能决策系统！");
            
            log.info("用户注册成功，用户ID: {}, 用户名: {}", user.getId(), user.getUsername());
            return Result.success(response);
            
        } catch (ServiceException e) {
            log.error("用户注册失败: {}", e.getMessage());
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("用户注册异常", e);
            return Result.fail("注册失败，请稍后重试");
        }
    }

    /**
     * 验证注册请求参数
     */
    private void validateRegisterRequest(RegisterRequest request) {
        // 验证密码和确认密码是否一致
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new ServiceException(Constants.CODE_600, "两次输入的密码不一致");
        }
        
        // 验证手机号格式
        if (RegexUtils.isPhoneInvalid(request.getPhone())) {
            throw new ServiceException(Constants.CODE_600, "手机号格式不正确");
        }
        
        // 验证邮箱格式（如果提供了邮箱）
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            if (RegexUtils.isEmailInvalid(request.getEmail())) {
                throw new ServiceException(Constants.CODE_600, "邮箱格式不正确");
            }
        }
    }

    /**
     * 检查用户名是否已存在
     */
    @Override
    public boolean isUsernameExists(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return userMapper.selectCount(queryWrapper) > 0;
    }

    /**
     * 检查手机号是否已存在
     */
    @Override
    public boolean isPhoneExists(String phone) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);
        return userMapper.selectCount(queryWrapper) > 0;
    }

    /**
     * 检查邮箱是否已存在
     */
    @Override
    public boolean isEmailExists(String email) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        return userMapper.selectCount(queryWrapper) > 0;
    }
    
    /**
     * 获取个人信息
     */
    @Override
    public Result<PersonalInfoResponse> getPersonalInfo(Integer userId) {
        log.info("获取个人信息，用户ID: {}", userId);
        
        try {
            User user = userMapper.selectById(userId);
            if (user == null) {
                return Result.fail("用户不存在");
            }
            
            PersonalInfoResponse response = new PersonalInfoResponse();
            BeanUtils.copyProperties(user, response);
            
            log.info("获取个人信息成功，用户ID: {}", userId);
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("获取个人信息异常，用户ID: {}", userId, e);
            return Result.fail("获取个人信息失败");
        }
    }
    
    /**
     * 更新个人信息
     */
    @Override
    public Result<PersonalInfoResponse> updatePersonalInfo(Integer userId, PersonalInfoRequest request) {
        log.info("更新个人信息，用户ID: {}, 请求: {}", userId, request);
        
        try {
            // 1. 验证用户是否存在
            User user = userMapper.selectById(userId);
            if (user == null) {
                return Result.fail("用户不存在");
            }
            
            // 2. 验证邮箱是否被其他用户使用
            if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
                if (!request.getEmail().equals(user.getEmail()) && isEmailExists(request.getEmail())) {
                    return Result.fail("邮箱已被其他用户使用");
                }
            }
            
            // 3. 验证手机号是否被其他用户使用
            if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
                if (!request.getPhone().equals(user.getPhone()) && isPhoneExists(request.getPhone())) {
                    return Result.fail("手机号已被其他用户使用");
                }
            }
            
            // 4. 更新用户信息
            if (request.getNickname() != null) {
                user.setNickname(request.getNickname());
            }
            if (request.getEmail() != null) {
                user.setEmail(request.getEmail());
            }
            if (request.getPhone() != null) {
                user.setPhone(request.getPhone());
            }
            if (request.getAddress() != null) {
                user.setAddress(request.getAddress());
            }
            if (request.getBio() != null) {
                user.setBio(request.getBio());
            }
            if (request.getCompany() != null) {
                user.setCompany(request.getCompany());
            }
            if (request.getPosition() != null) {
                user.setPosition(request.getPosition());
            }
            if (request.getExpertise() != null) {
                user.setExpertise(request.getExpertise());
            }
            if (request.getAvatarUrl() != null) {
                log.info("更新头像URL，用户ID: {}, 新头像URL: {}", userId, request.getAvatarUrl());
                user.setAvatarUrl(request.getAvatarUrl());
            }
            
            // 5. 保存到数据库
            int updateResult = userMapper.updateById(user);
            if (updateResult <= 0) {
                return Result.fail("更新个人信息失败");
            }
            
            // 6. 构建响应
            PersonalInfoResponse response = new PersonalInfoResponse();
            BeanUtils.copyProperties(user, response);
            
            log.info("更新个人信息成功，用户ID: {}", userId);
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("更新个人信息异常，用户ID: {}", userId, e);
            return Result.fail("更新个人信息失败");
        }
    }
    
    /**
     * 修改密码
     */
    @Override
    public Result changePassword(Integer userId, PasswordChangeRequest request) {
        log.info("修改密码，用户ID: {}", userId);
        
        try {
            // 1. 验证用户是否存在
            User user = userMapper.selectById(userId);
            if (user == null) {
                return Result.fail("用户不存在");
            }
            
            // 2. 验证原密码
            String oldPasswordHash = DigestUtils.md5DigestAsHex(request.getOldPassword().getBytes());
            if (!oldPasswordHash.equals(user.getPassword())) {
                return Result.fail("原密码错误");
            }
            
            // 3. 验证新密码和确认密码是否一致
            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                return Result.fail("新密码和确认密码不一致");
            }
            
            // 4. 验证新密码不能与原密码相同
            if (request.getOldPassword().equals(request.getNewPassword())) {
                return Result.fail("新密码不能与原密码相同");
            }
            
            // 5. 更新密码
            String newPasswordHash = DigestUtils.md5DigestAsHex(request.getNewPassword().getBytes());
            user.setPassword(newPasswordHash);
            
            int updateResult = userMapper.updateById(user);
            if (updateResult <= 0) {
                return Result.fail("修改密码失败");
            }
            
            log.info("修改密码成功，用户ID: {}", userId);
            return Result.success("密码修改成功");
            
        } catch (Exception e) {
            log.error("修改密码异常，用户ID: {}", userId, e);
            return Result.fail("修改密码失败");
        }
    }
    
    /**
     * 上传头像
     */
    @Override
    public Result<String> uploadAvatar(Integer userId, String avatarUrl) {
        log.info("上传头像，用户ID: {}, 头像URL: {}", userId, avatarUrl);
        
        try {
            // 1. 验证用户是否存在
            User user = userMapper.selectById(userId);
            if (user == null) {
                return Result.fail("用户不存在");
            }
            
            // 2. 更新头像URL
            user.setAvatarUrl(avatarUrl);
            
            int updateResult = userMapper.updateById(user);
            if (updateResult <= 0) {
                return Result.fail("上传头像失败");
            }
            
            log.info("上传头像成功，用户ID: {}, 头像URL: {}", userId, avatarUrl);
            return Result.success(avatarUrl);
            
        } catch (Exception e) {
            log.error("上传头像异常，用户ID: {}", userId, e);
            return Result.fail("上传头像失败");
        }
    }
    
    /**
     * 根据用户名获取用户信息
     */
    @Override
    public User getUserByUsername(String username) {
        log.info("根据用户名获取用户信息，用户名: {}", username);
        
        try {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("username", username);
            User user = userMapper.selectOne(queryWrapper);
            
            if (user != null) {
                log.info("找到用户，用户ID: {}", user.getId());
            } else {
                log.warn("未找到用户，用户名: {}", username);
            }
            
            return user;
        } catch (Exception e) {
            log.error("根据用户名获取用户信息异常，用户名: {}", username, e);
            return null;
        }
    }
}
