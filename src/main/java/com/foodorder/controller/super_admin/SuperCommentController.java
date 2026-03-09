package com.foodorder.controller.super_admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.foodorder.common.Result;
import com.foodorder.entity.Comment;
import com.foodorder.exception.BusinessException;
import com.foodorder.mapper.CommentMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 超级管理端 - 评论管理 Controller
 */
@Api(tags = "超级管理-评论管理")
@RestController
@RequestMapping("/super/comments")
public class SuperCommentController {

    @Autowired
    private CommentMapper commentMapper;

    /**
     * 分页查询评论列表（支持内容关键字搜索）
     */
    @ApiOperation(value = "查询评论列表")
    @GetMapping
    public Result<IPage<Comment>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long foodId) {

        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<Comment>()
                .eq(Comment::getIsDeleted, 0)
                .like(StringUtils.hasText(keyword), Comment::getContent, keyword)
                .eq(foodId != null, Comment::getFoodId, foodId)
                .orderByDesc(Comment::getCreateTime);

        return Result.success(commentMapper.selectPage(new Page<>(page, size), wrapper));
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
