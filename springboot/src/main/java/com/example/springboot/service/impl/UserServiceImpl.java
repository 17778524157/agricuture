package com.example.springboot.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.springboot.controller.dto.UserDTO;
import com.example.springboot.controller.dto.UserPasswordDTO;
import com.example.springboot.exception.ServiceException;
import com.example.springboot.common.Constants;
import com.example.springboot.common.RoleEnum;
import com.example.springboot.entity.Menu;
import com.example.springboot.entity.User;
import com.example.springboot.mapper.RoleMapper;
import com.example.springboot.mapper.RoleMenuMapper;
import com.example.springboot.mapper.UserMapper;
import com.example.springboot.service.IMenuService;
import com.example.springboot.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot.utils.TokenUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    // 定义一个静态的日志记录器，用于记录日志信息
    private static final Log LOG = Log.get();

    // 使用@Resource注解自动注入。。。。实例
    @Resource
    private UserMapper userMapper;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private RoleMenuMapper roleMenuMapper;

    @Resource
    private IMenuService menuService;

    @Override
    public UserDTO login(UserDTO userDTO) {
        // 调用自定义的getUserInfo方法，根据用户输入的DTO信息获取User实体
        User one = getUserInfo(userDTO);
        if (one != null) {
            //使用BeanUtil.copyProperties方法将User实体的属性复制到UserDTO中，true参数表示进行深拷贝（如果属性是对象类型）
            BeanUtil.copyProperties(one, userDTO, true);
            // 生成一个token，通常用于后续的API请求认证。
            String token = TokenUtils.genToken(one.getId().toString(), one.getPassword());
            // 将生成的token设置到UserDTO中，以便返回给前端
            userDTO.setToken(token);

            String role = one.getRole(); // ROLE_ADMIN
            // 设置用户的菜单列表
            List<Menu> roleMenus = getRoleMenus(role);
            userDTO.setMenus(roleMenus);
            return userDTO;
        } else {
            throw new ServiceException(Constants.CODE_600, "用户名或密码错误");
        }
    }

    /**
     * 注册新用户的方法。
     *
     * @param userDTO 包含新用户信息的DTO对象。
     * @return 如果注册成功，返回保存在数据库中的User对象；如果用户名已存在，则抛出异常。
     */
    @Override
    public User register(UserDTO userDTO) {
        // 使用lambda表达式构建查询条件，查询数据库中是否存在具有相同用户名的用户。
        User one = getOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, userDTO.getUsername()));
        if (one == null) {
            one = new User();
            BeanUtil.copyProperties(userDTO, one, true);
            // 默认一个普通用户的角色
            one.setRole(RoleEnum.ROLE_USER.toString());
            save(one);  // 把 copy完之后的用户对象存储到数据库
        } else {
            throw new ServiceException(Constants.CODE_600, "用户已存在");
        }
        return one;
    }

    /**
     * 更新用户密码的方法。
     *
     * @param userPasswordDTO 包含用户ID和新密码的DTO对象。
     * @throws ServiceException 如果更新失败（例如，用户ID不存在或密码验证失败），则抛出异常。
     */
    @Override
    public void updatePassword(UserPasswordDTO userPasswordDTO) {
        int update = userMapper.updatePassword(userPasswordDTO);
        if (update < 1) {
            throw new ServiceException(Constants.CODE_600, "密码错误");
        }
    }

    /**
     * 根据用户DTO中的用户名和密码查询用户信息的方法。
     *
     * @param userDTO 包含用户名和密码的DTO对象。
     * @return 如果查询成功，返回查询到的User对象；如果查询失败（例如，数据库错误），则抛出异常。
     */
    private User getUserInfo(UserDTO userDTO) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", userDTO.getUsername());
        queryWrapper.eq("password", userDTO.getPassword());
        User one;
        try {
            one = getOne(queryWrapper); // 从数据库查询用户信息
        } catch (Exception e) {
            LOG.error(e);
            throw new ServiceException(Constants.CODE_500, "系统错误");
        }
        return one;
    }

    /**
     * 根据角色标识获取该角色对应的菜单列表（包括子菜单）。
     *
     * @param roleFlag 角色标识，用于查询对应的角色ID。
     * @return 返回该角色对应的菜单列表（包括具有该角色访问权限的菜单及其子菜单）。
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
            List<Menu> children = menu.getChildren();
            // removeIf()  移除 children 里面不在 menuIds集合中的 元素
            children.removeIf(child -> !menuIds.contains(child.getId()));
        }
        return roleMenus;
    }

}