package com.hit.articlemgr.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hit.articlemgr.dto.ArticleSaveDTO;
import com.hit.articlemgr.dto.ArticleVO;
import com.hit.articlemgr.dto.CategoryVO;
import com.hit.articlemgr.dto.FilterDTO;
import com.hit.articlemgr.entity.Article;
import com.hit.articlemgr.mapper.ArticleMapper;

import java.util.List;

/**
 * 文章服务接口
 *
 * @author HIT
 */
public interface ArticleService {

    /**
     * 创建文章
     */
    Article createArticle(ArticleSaveDTO articleSaveDTO, Long userId);

    /**
     * 更新文章
     */
    Article updateArticle(ArticleSaveDTO articleSaveDTO, Long userId);

    /**
     * 删除文章
     */
    void deleteArticle(Long id, Long userId);

    /**
     * 根据ID获取文章
     */
    ArticleVO getArticleById(Long id, Long userId);

    /**
     * 分页查询文章列表
     */
    IPage<ArticleVO> getArticleList(FilterDTO filter, Long userId);

    /**
     * 增加阅读次数
     */
    void incrementViewCount(Long id);

    /**
     * 增加点赞次数
     */
    void incrementLikeCount(Long id);

    /**
     * 获取热门标签
     */
    List<String> getPopularTags(Long userId);

    /**
     * 获取文章统计数据
     */
    ArticleMapper.ArticleStatistics getArticleStatistics(Long userId);

    /**
     * 生成文章摘要（基于大模型）
     */
    String generateSummary(String content);

    /**
     * 获取最近文章列表
     */
    List<ArticleVO> getRecentArticles(Long userId);

    /**
     * 获取热门文章
     *
     * @return 热门文章列表
     */
    List<ArticleVO> getHotArticles();

    /**
     * 获取热门分类
     *
     * @return 热门分类列表
     */
    List<CategoryVO> getHotCategories();

    /**
     * 减少点赞数
     *
     * @param id 文章ID
     */
    void decrementLikeCount(Long id);

    /**
     * 获取文章详情（包含点赞状态）
     *
     * @param id 文章ID
     * @param userId 用户ID
     * @return 文章详情
     */
    ArticleVO getArticleDetail(Long id, Long userId);
}