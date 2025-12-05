package com.agriculture.user.service;

import com.agriculture.common.result.Result;
import com.agriculture.user.dto.LoginRequest;
import com.agriculture.user.dto.LoginResponse;
import com.agriculture.user.dto.RegisterRequest;
import com.agriculture.user.dto.RegisterResponse;
import com.agriculture.user.dto.PersonalInfoRequest;
import com.agriculture.user.dto.PersonalInfoResponse;
import com.agriculture.user.dto.PasswordChangeRequest;
import com.agriculture.user.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionIdListener;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {
    
    /**
     * 用户登录
     */
    LoginResponse login(LoginRequest request);
    
    /**
     * 根据用户名查询用户
     */


    User findByUser(LoginRequest request);

    /**
     * 生成JWT Token
     */
    String generateToken(User user);

    /**
     *发送验证码
     */
    Result sendCode(String phone, HttpSession session);

    Result<LoginResponse> phoneLogin(LoginRequest request, HttpSession session);

    /**
     * 用户登出
     */
    Result logout(String token, String userId);

    /**
     * 用户注册
     */
    Result<RegisterResponse> register(RegisterRequest request, HttpSession session);

    /**
     * 检查用户名是否已存在
     */
    boolean isUsernameExists(String username);

    /**
     * 检查手机号是否已存在
     */
    boolean isPhoneExists(String phone);

    /**
     * 检查邮箱是否已存在
     */
    boolean isEmailExists(String email);
    
    /**
     * 获取个人信息
     */
    Result<PersonalInfoResponse> getPersonalInfo(Integer userId);
    
    /**
     * 更新个人信息
     */
    Result<PersonalInfoResponse> updatePersonalInfo(Integer userId, PersonalInfoRequest request);
    
    /**
     * 修改密码
     */
    Result changePassword(Integer userId, PasswordChangeRequest request);
    
    /**
     * 上传头像
     */
    Result<String> uploadAvatar(Integer userId, String avatarUrl);
    
    /**
     * 根据用户名获取用户信息
     */
    User getUserByUsername(String username);
}
