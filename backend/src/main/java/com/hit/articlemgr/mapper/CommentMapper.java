package com.hit.articlemgr.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hit.articlemgr.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 评论Mapper接口
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    /**
     * 获取文章评论列表（包含用户信息）
     */
    @Select("SELECT c.*, u.username as user_name, u.avatar as user_avatar, " +
            "ru.username as reply_to_username " +
            "FROM comments c " +
            "LEFT JOIN users u ON c.user_id = u.id " +
            "LEFT JOIN users ru ON c.reply_to_user_id = ru.id " +
            "WHERE c.article_id = #{articleId} AND c.parent_id IS NULL " +
            "ORDER BY c.create_time DESC")
    IPage<Comment> selectArticleComments(Page<Comment> page, @Param("articleId") Long articleId);

    /**
     * 获取评论的回复列表
     */
    @Select("SELECT c.*, u.username as user_name, u.avatar as user_avatar, " +
            "ru.username as reply_to_username " +
            "FROM comments c " +
            "LEFT JOIN users u ON c.user_id = u.id " +
            "LEFT JOIN users ru ON c.reply_to_user_id = ru.id " +
            "WHERE c.parent_id = #{parentId} " +
            "ORDER BY c.create_time ASC")
    List<Comment> selectCommentReplies(@Param("parentId") Long parentId);

    /**
     * 获取评论数量
     */
    @Select("SELECT COUNT(*) FROM comments WHERE article_id = #{articleId}")
    int countByArticleId(@Param("articleId") Long articleId);
} 