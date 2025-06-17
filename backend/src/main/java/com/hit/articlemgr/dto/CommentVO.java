package com.hit.articlemgr.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论VO类
 */
@Data
public class CommentVO {
    
    /**
     * 评论ID
     */
    private Long id;

    /**
     * 文章ID
     */
    private Long articleId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 父评论ID
     */
    private Long parentId;

    /**
     * 回复的用户ID
     */
    private Long replyToUserId;

    /**
     * 回复的用户名
     */
    private String replyTo;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 子评论列表
     */
    private List<CommentVO> replies;
} 