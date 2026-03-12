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

    @ApiOperation(value = "发布评论", notes = "对已购买的菜品发布评论，传入foodId、orderId、content、rating（1-5分）")
    @PostMapping("/add")
    public Result<Void> add(
            @RequestAttribute Long userId,
            @ApiParam(value = "评论参数，包含foodId、orderId、content、rating", required = true) @RequestBody Map<String, Object> params) {
        Long foodId = Long.valueOf(params.get("foodId").toString());
        Long orderId = Long.valueOf(params.get("orderId").toString());
        String content = (String) params.get("content");
        Integer rating = Integer.valueOf(params.get("rating").toString());

        commentService.addComment(userId, foodId, orderId, content, rating);
        return Result.success();
    }

    @ApiOperation(value = "批量发布评论", notes = "对订单中的多个菜品批量发布评论")
    @PostMapping("/addBatch")
    public Result<Void> addBatch(
            @RequestAttribute Long userId,
            @ApiParam(value = "批量评论参数，包含orderId和comments数组", required = true) @RequestBody Map<String, Object> params) {
        Long orderId = Long.valueOf(params.get("orderId").toString());
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> comments = (List<Map<String, Object>>) params.get("comments");

        commentService.addCommentBatch(userId, orderId, comments);
        return Result.success();
    }

    @ApiOperation(value = "获取订单评论", notes = "根据订单ID获取该订单的所有评论列表")
    @GetMapping("/order")
    public Result<List<Comment>> listByOrder(
            @RequestAttribute Long userId,
            @ApiParam(value = "订单ID", required = true) @RequestParam Long orderId) {
        List<Comment> comments = commentService.listCommentsByOrder(userId, orderId);
        return Result.success(comments);
    }

    @ApiOperation(value = "获取菜品评论", notes = "根据菜品ID获取该菜品的所有评论列表，按时间倒序排列")
    @GetMapping("/food")
    public Result<List<Comment>> listByFood(
            @ApiParam(value = "菜品ID", required = true) @RequestParam Long foodId) {
        List<Comment> comments = commentService.listCommentsByFood(foodId);
        return Result.success(comments);
    }

    @ApiOperation(value = "获取用户评论", notes = "根据用户ID获取该用户发表的所有评论列表")
    @GetMapping("/user")
    public Result<List<Comment>> listByUser(
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId) {
        List<Comment> comments = commentService.listCommentsByUser(userId);
        return Result.success(comments);
    }
}