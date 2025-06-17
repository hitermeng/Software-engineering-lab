package com.hit.articlemgr.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文章点赞实体类
 */
@Data
@TableName("article_likes")
public class ArticleLike {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long articleId;
    
    private Long userId;
    
    private LocalDateTime createTime;
} 