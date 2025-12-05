package com.agriculture.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 个人信息更新请求DTO
 */
@Data
public class PersonalInfoRequest {
    
    @NotBlank(message = "昵称不能为空")
    @Size(min = 2, max = 20, message = "昵称长度必须在2-20个字符之间")
    private String nickname;
    
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    @Size(max = 200, message = "地址长度不能超过200个字符")
    private String address;
    
    @Size(max = 500, message = "个人简介长度不能超过500个字符")
    private String bio;
    
    @Size(max = 100, message = "公司名称长度不能超过100个字符")
    private String company;
    
    @Size(max = 50, message = "职位长度不能超过50个字符")
    private String position;
    
    @Size(max = 100, message = "专业领域长度不能超过100个字符")
    private String expertise;
    
    private String avatarUrl;
}
