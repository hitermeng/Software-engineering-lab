package com.hit.articlemgr.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hit.articlemgr.dto.ArticleVO;

/**
 * 搜索服务接口
 *
 * @author HIT
 */
public interface SearchService {

    /**
     * 全文搜索文章
     */
    IPage<ArticleVO> searchArticles(String keyword, Integer page, Integer size, Long userId);

    /**
     * 高级搜索
     */
    IPage<ArticleVO> advancedSearch(String keyword, Long categoryId, String tag,
                                    String sort, Integer page, Integer size, Long userId);
}