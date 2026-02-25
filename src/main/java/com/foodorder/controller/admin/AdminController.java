package com.foodorder.controller.admin;

import com.foodorder.common.Result;
import com.foodorder.service.UserService;
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

    /**
     * 管理员登录
     */
    @ApiOperation(value = "管理员登录", notes = "管理员通过用户名和密码登录，返回Token和管理员信息")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(
            @ApiParam(value = "登录参数，包含username和password", required = true)
            @RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");

        if (username == null || username.trim().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            return Result.error("密码不能为空");
        }

        Map<String, Object> result = userService.adminLogin(username, password);
        return Result.success(result);
    }
}
