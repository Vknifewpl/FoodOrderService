package com.foodorder.service.impl;

import com.foodorder.entity.User;
import com.foodorder.exception.BusinessException;
import com.foodorder.mapper.UserMapper;
import com.foodorder.service.UserService;
import com.foodorder.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户Service实现
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Map<String, Object> register(String username, String password) {
        User existUser = userMapper.selectByUsername(username);
        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(DigestUtils.md5DigestAsHex(password.getBytes()));
        user.setRole(0);
        user.setIsNewUser(1);
        userMapper.insert(user);

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        userMapper.updateToken(user.getId(), token);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("isNewUser", user.getIsNewUser());
        return result;
    }

    @Override
    public Map<String, Object> login(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!md5Password.equals(user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        userMapper.updateToken(user.getId(), token);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("role", user.getRole());
        result.put("isNewUser", user.getIsNewUser());
        return result;
    }

    @Override
    public Map<String, Object> adminLogin(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        if (user.getRole() != 1) {
            throw new BusinessException("该账号不是管理员账号");
        }

        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!md5Password.equals(user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        userMapper.updateToken(user.getId(), token);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        return result;
    }

    @Override
    public User getUserById(Long userId) {
        return userMapper.selectById(userId);
    }

    @Override
    public void updateNewUserStatus(Long userId, Integer isNewUser) {
        userMapper.updateNewUserStatus(userId, isNewUser);
    }

    @Override
    public long getUserCount() {
        return userMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                        .eq(User::getIsDeleted, 0));
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        String oldMd5 = DigestUtils.md5DigestAsHex(oldPassword.getBytes());
        if (!oldMd5.equals(user.getPassword())) {
            throw new BusinessException("旧密码错误");
        }
        String newMd5 = DigestUtils.md5DigestAsHex(newPassword.getBytes());
        user.setPassword(newMd5);
        userMapper.updateById(user);
    }
}
