package com.hit.articlemgr.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章视图对象
 *
 * @author HIT
 */
@Data
public class ArticleVO {

    /**
     * 文章ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户昵称
     */
    private String userNickname;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 分类类型
     */
    private String categoryType;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章内容
     */
    private String content;

    /**
     * 文章摘要
     */
    private String summary;

    /**
     * 标签，逗号分隔
     */
    private String tags;

    /**
     * 标签数组
     */
    private List<String> tagArray;

    /**
     * 是否共享：0-私有，1-共享
     */
    private Integer isShared;

    /**
     * 阅读次数
     */
    private Integer viewCount;

    /**
     * 点赞次数
     */
    private Integer likeCount;

    /**
     * 评论次数
     */
    private Integer commentCount;

    /**
     * 状态：1-正常，0-草稿
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 作者名称
     */
    private String authorName;

    /**
     * 作者头像
     */
    private String authorAvatar;
}