package com.hit.articlemgr.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hit.articlemgr.dto.ArticleSaveDTO;
import com.hit.articlemgr.dto.ArticleVO;
import com.hit.articlemgr.dto.FilterDTO;
import com.hit.articlemgr.entity.Article;
import com.hit.articlemgr.mapper.ArticleMapper;
import com.hit.articlemgr.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 文章服务实现类
 *
 * @author HIT
 */
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleMapper articleMapper;

    @Override
    @Transactional
    public Article createArticle(ArticleSaveDTO articleSaveDTO, Long userId) {
        Article article = new Article();
        article.setUserId(userId);
        article.setCategoryId(articleSaveDTO.getCategoryId());
        article.setTitle(articleSaveDTO.getTitle());
        article.setContent(articleSaveDTO.getContent());

        // 如果没有提供摘要，自动生成
        if (!StringUtils.hasText(articleSaveDTO.getSummary())) {
            article.setSummary(generateSummary(articleSaveDTO.getContent()));
        } else {
            article.setSummary(articleSaveDTO.getSummary());
        }

        article.setTags(articleSaveDTO.getTags());
        article.setIsShared(articleSaveDTO.getIsShared() != null ? articleSaveDTO.getIsShared() : 0);
        article.setStatus(articleSaveDTO.getStatus() != null ? articleSaveDTO.getStatus() : 1);
        article.setViewCount(0);
        article.setLikeCount(0);
        article.setCommentCount(0);

        articleMapper.insert(article);
        return article;
    }

    @Override
    @Transactional
    public Article updateArticle(ArticleSaveDTO articleSaveDTO, Long userId) {
        Article article = getArticleEntityById(articleSaveDTO.getId(), userId);
        if (article == null) {
            throw new RuntimeException("文章不存在");
        }

        article.setCategoryId(articleSaveDTO.getCategoryId());
        article.setTitle(articleSaveDTO.getTitle());
        article.setContent(articleSaveDTO.getContent());

        // 如果没有提供摘要，自动生成
        if (!StringUtils.hasText(articleSaveDTO.getSummary())) {
            article.setSummary(generateSummary(articleSaveDTO.getContent()));
        } else {
            article.setSummary(articleSaveDTO.getSummary());
        }

        article.setTags(articleSaveDTO.getTags());
        article.setIsShared(articleSaveDTO.getIsShared() != null ? articleSaveDTO.getIsShared() : 0);
        article.setStatus(articleSaveDTO.getStatus() != null ? articleSaveDTO.getStatus() : 1);

        articleMapper.updateById(article);
        return article;
    }

    @Override
    @Transactional
    public void deleteArticle(Long id, Long userId) {
        Article article = getArticleEntityById(id, userId);
        if (article == null) {
            throw new RuntimeException("文章不存在");
        }

        // 软删除
        articleMapper.deleteById(id);
    }

    @Override
    public ArticleVO getArticleById(Long id, Long userId) {
        ArticleVO articleVO = articleMapper.findArticleById(id, userId);
        if (articleVO != null && StringUtils.hasText(articleVO.getTags())) {
            // 解析标签
            articleVO.setTagArray(articleVO.getTags().split(","));
        }
        return articleVO;
    }

    @Override
    public IPage<ArticleVO> getArticleList(FilterDTO filter, Long userId) {
        Page<ArticleVO> page = new Page<>(filter.getPage(), filter.getSize());
        IPage<ArticleVO> result = articleMapper.findArticleList(page, filter, userId);

        // 处理标签
        result.getRecords().forEach(article -> {
            if (StringUtils.hasText(article.getTags())) {
                article.setTagArray(article.getTags().split(","));
            }
        });

        return result;
    }

    @Override
    @Transactional
    public void incrementViewCount(Long id) {
        articleMapper.incrementViewCount(id);
    }

    @Override
    @Transactional
    public void incrementLikeCount(Long id) {
        articleMapper.incrementLikeCount(id);
    }

    @Override
    public List<String> getPopularTags(Long userId) {
        List<String> tagStrings = articleMapper.getPopularTags(userId);
        Set<String> allTags = tagStrings.stream()
                .filter(StringUtils::hasText)
                .flatMap(tags -> Arrays.stream(tags.split(",")))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());

        return new ArrayList<>(allTags);
    }

    @Override
    public ArticleMapper.ArticleStatistics getArticleStatistics(Long userId) {
        return articleMapper.getArticleStatistics(userId);
    }

    @Override
    public String generateSummary(String content) {
        if (!StringUtils.hasText(content)) {
            return "";
        }

        // 简单的摘要生成逻辑：取前200个字符
        // 在实际项目中，这里可以接入大模型API来生成更智能的摘要
        String plainText = content.replaceAll("<[^>]*>", ""); // 移除HTML标签
        if (plainText.length() <= 200) {
            return plainText;
        }

        return plainText.substring(0, 200) + "...";
    }

    @Override
    public List<ArticleVO> getRecentArticles(Long userId) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getDeleted, 0) // 未删除
                .eq(Article::getStatus, 1) // 已发布
                .and(w -> w.eq(Article::getUserId, userId).or().eq(Article::getIsShared, 1)) // 自己的或共享的
                .orderByDesc(Article::getCreateTime)
                .last("LIMIT 5"); // 获取最近5篇

        List<Article> articles = articleMapper.selectList(wrapper);

        // 转换为 ArticleVO
        List<ArticleVO> articleVOs = articles.stream().map(article -> {
            ArticleVO vo = new ArticleVO();
            vo.setId(article.getId());
            vo.setTitle(article.getTitle());
            vo.setCategoryId(article.getCategoryId());
            vo.setTags(article.getTags());
            vo.setStatus(article.getStatus());
            vo.setCreateTime(article.getCreateTime());
            // 可以根据需要复制更多字段
            return vo;
        }).collect(Collectors.toList());

        return articleVOs;
    }

    /**
     * 根据ID获取文章实体
     */
    private Article getArticleEntityById(Long id, Long userId) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getId, id)
                .eq(Article::getUserId, userId);
        return articleMapper.selectOne(wrapper);
    }
}