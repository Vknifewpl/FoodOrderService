package com.foodorder.controller.super_admin;

import com.foodorder.common.Result;
import com.foodorder.entity.User;
import com.foodorder.exception.BusinessException;
import com.foodorder.mapper.UserMapper;
import com.foodorder.utils.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 超级管理员登录 Controller
 * role=2 专用
 */
@Api(tags = "超级管理员")
@RestController
@RequestMapping("/super")
public class SuperController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 超级管理员登录
     */
    @ApiOperation(value = "超级管理员登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");

        if (username == null || username.trim().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            return Result.error("密码不能为空");
        }

        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 仅允许 role=2 的超级管理员登录
        if (user.getRole() != 2) {
            throw new BusinessException("该账号不是超级管理员账号");
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
        return Result.success(result);
    }
}
