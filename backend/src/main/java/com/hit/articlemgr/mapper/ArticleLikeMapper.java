package com.hit.articlemgr.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hit.articlemgr.entity.ArticleLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 文章点赞记录Mapper接口
 */
@Mapper
public interface ArticleLikeMapper extends BaseMapper<ArticleLike> {
    
    /**
     * 检查用户是否点赞过文章
     *
     * @param articleId 文章ID
     * @param userId 用户ID
     * @return 点赞记录数
     */
    @Select("SELECT COUNT(*) FROM article_likes WHERE article_id = #{articleId} AND user_id = #{userId}")
    int checkUserLiked(Long articleId, Long userId);
} 