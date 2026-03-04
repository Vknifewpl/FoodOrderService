package com.foodorder.service;

import com.foodorder.entity.User;

import java.util.Map;

/**
 * 用户Service接口
 */
public interface UserService {

    /**
     * 用户注册
     */
    Map<String, Object> register(String username, String password);

    /**
     * 用户登录
     */
    Map<String, Object> login(String username, String password);

    /**
     * 管理员登录
     */
    Map<String, Object> adminLogin(String username, String password);

    /**
     * 根据ID获取用户信息
     */
    User getUserById(Long userId);

    /**
     * 更新用户新用户状态
     */
    void updateNewUserStatus(Long userId, Integer isNewUser);

    /**
     * 获取用户总数
     */
    long getUserCount();

    /**
     * 修改密码
     */
    void changePassword(String username, String oldPassword, String newPassword);
}
