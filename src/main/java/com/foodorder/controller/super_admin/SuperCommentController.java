package com.foodorder.controller.super_admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.foodorder.common.Result;
import com.foodorder.entity.Comment;
import com.foodorder.entity.Food;
import com.foodorder.entity.User;
import com.foodorder.exception.BusinessException;
import com.foodorder.mapper.CommentMapper;
import com.foodorder.mapper.FoodMapper;
import com.foodorder.mapper.UserMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 超级管理端 - 评论管理 Controller
 */
@Api(tags = "超级管理-评论管理")
@RestController
@RequestMapping("/super/comments")
public class SuperCommentController {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private FoodMapper foodMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 分页查询评论列表（支持按菜品名搜索）
     */
    @ApiOperation(value = "查询评论列表")
    @GetMapping
    public Result<IPage<Comment>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long foodId) {

        List<Long> foodIdsByName = Collections.emptyList();
        if (StringUtils.hasText(keyword)) {
            List<Food> foods = foodMapper.searchByName(keyword.trim());
            foodIdsByName = foods.stream().map(Food::getId).collect(Collectors.toList());
            if (foodIdsByName.isEmpty()) {
                return Result.success(new Page<>(page, size, 0));
            }
        }

        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<Comment>()
                .eq(Comment::getIsDeleted, 0)
                .in(StringUtils.hasText(keyword), Comment::getFoodId, foodIdsByName)
                .eq(foodId != null, Comment::getFoodId, foodId)
                .orderByDesc(Comment::getCreateTime);

        IPage<Comment> pageData = commentMapper.selectPage(new Page<>(page, size), wrapper);
        List<Comment> records = pageData.getRecords();
        if (records != null && !records.isEmpty()) {
            Set<Long> userIds = records.stream().map(Comment::getUserId).filter(Objects::nonNull).collect(Collectors.toSet());
            Set<Long> foodIds = records.stream().map(Comment::getFoodId).filter(Objects::nonNull).collect(Collectors.toSet());

            Map<Long, String> userNameMap = userIds.isEmpty() ? Collections.emptyMap() :
                    userMapper.selectBatchIds(userIds).stream().collect(Collectors.toMap(User::getId, User::getUsername, (a, b) -> a));
            Map<Long, String> foodNameMap = foodIds.isEmpty() ? Collections.emptyMap() :
                    foodMapper.selectBatchIds(foodIds).stream().collect(Collectors.toMap(Food::getId, Food::getName, (a, b) -> a));

            records.forEach(c -> {
                c.setUsername(userNameMap.getOrDefault(c.getUserId(), "-"));
                c.setFoodName(foodNameMap.getOrDefault(c.getFoodId(), "-"));
            });
        }

        return Result.success(pageData);
    }

    /**
     * 删除评论（逻辑删除）
     */
    @ApiOperation(value = "删除评论")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        Comment comment = commentMapper.selectById(id);
        if (comment == null || comment.getIsDeleted() == 1) {
            throw new BusinessException("评论不存在");
        }
        commentMapper.deleteById(id);
        return Result.success();
    }
}