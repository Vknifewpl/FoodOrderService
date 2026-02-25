package com.foodorder.controller.user;

import com.foodorder.common.Result;
import com.foodorder.entity.Comment;
import com.foodorder.service.CommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 评论Controller（用户端）
 */
@Api(tags = "用户评论")
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 发布评论
     */
    @ApiOperation(value = "发布评论", notes = "对已购买的菜品发布评论，传入userId、foodId、orderId、content、rating（1-5分）")
    @PostMapping("/add")
    public Result<Void> add(
            @ApiParam(value = "评论参数，包含userId、foodId、orderId、content、rating", required = true)
            @RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        Long foodId = Long.valueOf(params.get("foodId").toString());
        Long orderId = Long.valueOf(params.get("orderId").toString());
        String content = (String) params.get("content");
        Integer rating = Integer.valueOf(params.get("rating").toString());

        commentService.addComment(userId, foodId, orderId, content, rating);
        return Result.success();
    }

    /**
     * 根据菜品ID获取评论
     */
    @ApiOperation(value = "获取菜品评论", notes = "根据菜品ID获取该菜品的所有评论列表，按时间倒序排列")
    @GetMapping("/food")
    public Result<List<Comment>> listByFood(
            @ApiParam(value = "菜品ID", required = true) @RequestParam Long foodId) {
        List<Comment> comments = commentService.listCommentsByFood(foodId);
        return Result.success(comments);
    }

    /**
     * 根据用户ID获取评论
     */
    @ApiOperation(value = "获取用户评论", notes = "根据用户ID获取该用户发表的所有评论列表")
    @GetMapping("/user")
    public Result<List<Comment>> listByUser(
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId) {
        List<Comment> comments = commentService.listCommentsByUser(userId);
        return Result.success(comments);
    }
}
