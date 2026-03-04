package com.foodorder.controller.user;

import com.foodorder.common.Result;
import com.foodorder.entity.User;
import com.foodorder.service.UserService;
import com.foodorder.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户Controller
 */
@Api(tags = "用户管理")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 用户注册
     */
    @ApiOperation(value = "用户注册", notes = "传入username和password进行注册，返回用户信息和Token")
    @PostMapping("/register")
    public Result<Map<String, Object>> register(
            @ApiParam(value = "注册参数，包含username和password", required = true) @RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");

        if (username == null || username.trim().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            return Result.error("密码不能为空");
        }

        Map<String, Object> result = userService.register(username, password);
        return Result.success(result);
    }

    /**
     * 用户登录（含验证码校验）
     */
    @ApiOperation(value = "用户登录", notes = "传入username、password、captchaKey和captchaCode进行登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(
            @ApiParam(value = "登录参数", required = true) @RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        String captchaKey = params.get("captchaKey");
        String captchaCode = params.get("captchaCode");

        if (username == null || username.trim().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            return Result.error("密码不能为空");
        }

        // 验证码校验
        if (captchaKey == null || captchaCode == null || captchaCode.trim().isEmpty()) {
            return Result.error("请输入验证码");
        }
        Object storedCode = redisUtil.get("captcha:" + captchaKey);
        // 验证后立即删除，防止重复使用
        redisUtil.delete("captcha:" + captchaKey);
        if (storedCode == null || !captchaCode.equalsIgnoreCase(storedCode.toString())) {
            return Result.error("验证码错误");
        }

        Map<String, Object> result = userService.login(username, password);
        return Result.success(result);
    }

    /**
     * 获取用户信息
     */
    @ApiOperation(value = "获取用户信息", notes = "根据Token中的用户ID获取用户基本信息，需要在Header中携带Authorization Token")
    @GetMapping("/info")
    public Result<Map<String, Object>> getUserInfo(@RequestAttribute Long userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        Map<String, Object> result = new java.util.HashMap<>();
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("isNewUser", user.getIsNewUser());
        return Result.success(result);
    }
}
