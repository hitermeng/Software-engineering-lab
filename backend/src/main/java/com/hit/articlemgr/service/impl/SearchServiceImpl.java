package com.hit.articlemgr.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hit.articlemgr.dto.ArticleVO;
import com.hit.articlemgr.dto.FilterDTO;
//import com.hit.articlemgr.entity.Article;
import com.hit.articlemgr.mapper.ArticleMapper;
import com.hit.articlemgr.service.ArticleService;
import com.hit.articlemgr.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Arrays;

/**
 * 搜索服务实现类
 *
 * @author HIT
 */
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final ArticleMapper articleMapper;
    private final ArticleService articleService;

    @Override
    public IPage<ArticleVO> searchArticles(String keyword, Integer page, Integer size, Long userId) {
        Page<ArticleVO> pageParam = new Page<>(page, size);
        IPage<ArticleVO> result = articleMapper.searchArticles(pageParam, keyword, userId);

        // 处理标签
        processTags(result.getRecords());

        return result;
    }

    @Override
    public IPage<ArticleVO> advancedSearch(String keyword, Long categoryId, String tag,
                                           String sortField, String sortOrder,
                                           Integer page, Integer size, Long userId) {
        FilterDTO filter = new FilterDTO();
        filter.setKeyword(keyword);
        filter.setCategoryId(categoryId);
        filter.setTag(tag);
        filter.setSortField(sortField);
        filter.setSortOrder(sortOrder);
        filter.setPage(page);
        filter.setSize(size);
        
        IPage<ArticleVO> result = articleService.getArticleList(filter, userId);
        
        // 处理标签
        processTags(result.getRecords());
        
        return result;
    }
    
    private void processTags(List<ArticleVO> articles) {
        articles.forEach(article -> {
            if (StringUtils.hasText(article.getTags())) {
                article.setTagArray(Arrays.asList(article.getTags().split(",")));
            } else {
                article.setTagArray(List.of());
            }
        });
    }
}