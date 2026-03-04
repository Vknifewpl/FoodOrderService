package com.foodorder.controller.admin;

import com.foodorder.common.Result;
import com.foodorder.service.UserService;
import com.foodorder.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理员Controller
 */
@Api(tags = "管理员登录")
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 管理员登录（含验证码校验）
     */
    @ApiOperation(value = "管理员登录", notes = "管理员通过用户名、密码和验证码登录，返回Token和管理员信息")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(
            @ApiParam(value = "登录参数", required = true)
            @RequestBody Map<String, String> params) {
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
        redisUtil.delete("captcha:" + captchaKey);
        if (storedCode == null || !captchaCode.equalsIgnoreCase(storedCode.toString())) {
            return Result.error("验证码错误");
        }

        Map<String, Object> result = userService.adminLogin(username, password);
        return Result.success(result);
    }
}
