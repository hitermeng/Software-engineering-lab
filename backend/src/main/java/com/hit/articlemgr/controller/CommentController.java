package com.hit.articlemgr.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hit.articlemgr.dto.CommentDTO;
import com.hit.articlemgr.dto.CommentVO;
import com.hit.articlemgr.service.CommentService;
import com.hit.articlemgr.util.R;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 评论控制器
 */
@RestController
@RequestMapping("/articles")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * 获取文章评论列表
     */
    @GetMapping("/{articleId}/comments")
    public R<IPage<CommentVO>> getArticleComments(
            @PathVariable Long articleId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            IPage<CommentVO> comments = commentService.getArticleComments(articleId, page, size);
            return R.success(comments);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    /**
     * 创建评论
     */
    @PostMapping("/{articleId}/comments")
    public R<CommentVO> createComment(
            @PathVariable Long articleId,
            @Validated @RequestBody CommentDTO commentDTO,
            @AuthenticationPrincipal Long userId) {
        try {
            commentDTO.setArticleId(articleId);
            CommentVO comment = commentService.createComment(commentDTO, userId);
            return R.success("评论成功", comment);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/comments/{commentId}")
    public R<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal Long userId) {
        try {
            commentService.deleteComment(commentId, userId);
            return R.success("删除成功", null);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
} 