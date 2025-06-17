package com.hit.articlemgr.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hit.articlemgr.dto.ArticleVO;
import com.hit.articlemgr.dto.FilterDTO;
import com.hit.articlemgr.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import lombok.Data;

import java.util.List;

/**
 * 文章数据访问接口
 *
 * @author HIT
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    /**
     * 分页查询文章列表
     */
    IPage<ArticleVO> findArticleList(Page<ArticleVO> page, @Param("filter") FilterDTO filter, @Param("userId") Long userId);

    /**
     * 根据ID查询文章详情
     */
    ArticleVO findArticleById(@Param("id") Long id, @Param("userId") Long userId);

    /**
     * 全文搜索文章
     */
    IPage<ArticleVO> searchArticles(Page<ArticleVO> page, @Param("keyword") String keyword, @Param("userId") Long userId);

    /**
     * 增加阅读次数
     */
    @Update("UPDATE article SET view_count = view_count + 1 WHERE id = #{id}")
    void incrementViewCount(@Param("id") Long id);

    /**
     * 增加点赞次数
     */
    @Update("UPDATE article SET like_count = like_count + 1 WHERE id = #{id}")
    void incrementLikeCount(@Param("id") Long id);

    /**
     * 获取热门标签
     */
    @Select("SELECT tags FROM article WHERE tags IS NOT NULL AND tags != '' AND deleted = 0 " +
            "AND (is_shared = 1 OR user_id = #{userId}) LIMIT 100")
    List<String> getPopularTags(@Param("userId") Long userId);

    /**
     * 文章统计信息
     */
    @Data
    public static class ArticleStatistics {
        private Long totalArticles;
        private Long publishedArticles;
        private Long totalViews;
    }

    /**
     * 获取文章统计信息 (用户特定)
     */
    ArticleStatistics getArticleStatistics(@Param("userId") Long userId);

    /**
     * 获取全局文章统计信息
     */
    ArticleStatistics getGlobalArticleStatistics();
}