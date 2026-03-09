package com.foodorder.controller.super_admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.foodorder.common.Result;
import com.foodorder.entity.User;
import com.foodorder.exception.BusinessException;
import com.foodorder.mapper.UserMapper;
import com.foodorder.utils.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 超级管理端 - 用户管理 Controller
 */
@Api(tags = "超级管理-用户管理")
@RestController
@RequestMapping("/super/users")
public class SuperUserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 分页查询用户列表（支持关键字搜索、角色筛选）
     */
    @ApiOperation(value = "查询用户列表")
    @GetMapping
    public Result<IPage<User>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer role) {

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .eq(User::getIsDeleted, 0)
                .like(StringUtils.hasText(keyword), User::getUsername, keyword)
                .eq(role != null, User::getRole, role)
                .orderByDesc(User::getCreateTime);

        IPage<User> pageResult = userMapper.selectPage(new Page<>(page, size), wrapper);
        // 清除密码和token字段，不对外暴露
        pageResult.getRecords().forEach(u -> {
            u.setPassword(null);
            u.setToken(null);
        });
        return Result.success(pageResult);
    }

    /**
     * 新增用户
     */
    @ApiOperation(value = "新增用户")
    @PostMapping
    public Result<Void> add(@RequestBody Map<String, Object> params) {
        String username = (String) params.get("username");
        String password = (String) params.get("password");
        Integer role = params.get("role") != null ? Integer.valueOf(params.get("role").toString()) : 0;

        if (!StringUtils.hasText(username)) {
            return Result.error("用户名不能为空");
        }
        if (!StringUtils.hasText(password) || password.length() < 6) {
            return Result.error("密码不能少于6位");
        }

        // 检查用户名唯一性
        if (userMapper.selectByUsername(username) != null) {
            throw new BusinessException("用户名已存在");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(DigestUtils.md5DigestAsHex(password.getBytes()));
        user.setRole(role);
        user.setIsNewUser(role == 0 ? 1 : 0);
        userMapper.insert(user);
        return Result.success();
    }

    /**
     * 修改用户信息（用户名、角色）
     */
    @ApiOperation(value = "修改用户信息")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        User user = userMapper.selectById(id);
        if (user == null || user.getIsDeleted() == 1) {
            throw new BusinessException("用户不存在");
        }

        String newUsername = (String) params.get("username");
        if (StringUtils.hasText(newUsername) && !newUsername.equals(user.getUsername())) {
            // 检查新用户名是否已被占用
            User existing = userMapper.selectByUsername(newUsername);
            if (existing != null && !existing.getId().equals(id)) {
                throw new BusinessException("用户名已存在");
            }
            user.setUsername(newUsername);
        }

        if (params.get("role") != null) {
            user.setRole(Integer.valueOf(params.get("role").toString()));
        }

        userMapper.updateById(user);
        return Result.success();
    }

    /**
     * 重置密码
     */
    @ApiOperation(value = "重置用户密码")
    @PutMapping("/{id}/password")
    public Result<Void> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> params) {
        String newPassword = params.get("password");
        if (!StringUtils.hasText(newPassword) || newPassword.length() < 6) {
            return Result.error("密码不能少于6位");
        }

        User user = userMapper.selectById(id);
        if (user == null || user.getIsDeleted() == 1) {
            throw new BusinessException("用户不存在");
        }

        user.setPassword(DigestUtils.md5DigestAsHex(newPassword.getBytes()));
        // 重置密码后清除旧 token，强制重新登录
        user.setToken(null);
        userMapper.updateById(user);
        return Result.success();
    }

    /**
     * 删除用户（逻辑删除）
     */
    @ApiOperation(value = "删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        User user = userMapper.selectById(id);
        if (user == null || user.getIsDeleted() == 1) {
            throw new BusinessException("用户不存在");
        }
        userMapper.deleteById(id);
        return Result.success();
    }
}
