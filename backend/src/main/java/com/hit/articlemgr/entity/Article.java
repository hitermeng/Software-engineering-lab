package com.hit.articlemgr.entity;

import com.baomidou.mybatisplus.annotation.*;
        import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 文章实体类
 *
 * @author HIT
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("article")
public class Article {

    /**
     * 文章ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 分类ID
     */
    @TableField("category_id")
    private Long categoryId;

    /**
     * 文章标题
     */
    @TableField("title")
    private String title;

    /**
     * 文章内容
     */
    @TableField("content")
    private String content;

    /**
     * 文章摘要
     */
    @TableField("summary")
    private String summary;

    /**
     * 标签，逗号分隔
     */
    @TableField("tags")
    private String tags;

    /**
     * 是否共享：0-私有，1-共享
     */
    @TableField("is_shared")
    private Integer isShared;

    /**
     * 阅读次数
     */
    @TableField("view_count")
    private Integer viewCount;

    /**
     * 点赞次数
     */
    @TableField("like_count")
    private Integer likeCount;

    /**
     * 评论次数
     */
    @TableField("comment_count")
    private Integer commentCount;

    /**
     * 状态：1-正常，0-草稿
     */
    @TableField("status")
    private Integer status;

    /**
     * 删除标记：0-未删除，1-已删除
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}