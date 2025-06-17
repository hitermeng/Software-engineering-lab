package com.hit.articlemgr.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 评论DTO
 */
@Data
public class CommentDTO {
    
    /**
     * 文章ID
     */
    @NotNull(message = "文章ID不能为空")
    private Long articleId;

    /**
     * 评论内容
     */
    @NotBlank(message = "评论内容不能为空")
    private String content;

    /**
     * 父评论ID（回复评论时使用）
     */
    private Long parentId;

    /**
     * 回复的用户名（回复评论时使用）
     */
    private String replyTo;
} 