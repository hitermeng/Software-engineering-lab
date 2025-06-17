package com.hit.articlemgr.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hit.articlemgr.dto.ArticleVO;
import com.hit.articlemgr.service.SearchService;
import com.hit.articlemgr.util.R;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 搜索控制器
 *
 * @author HIT
 */
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    /**
     * 全文搜索文章
     */
    @GetMapping
    public R<IPage<ArticleVO>> searchArticles(@AuthenticationPrincipal Long userId,
                                              @RequestParam String keyword,
                                              @RequestParam(defaultValue = "1") Integer page,
                                              @RequestParam(defaultValue = "10") Integer size) {
        try {
            IPage<ArticleVO> result = searchService.searchArticles(keyword, page, size, userId);
            return R.success(result);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    /**
     * 高级搜索
     */
    @GetMapping("/advanced")
    public R<IPage<ArticleVO>> advancedSearch(@AuthenticationPrincipal Long userId,
                                              @RequestParam(required = false) String keyword,
                                              @RequestParam(required = false) Long categoryId,
                                              @RequestParam(required = false) String tag,
                                              @RequestParam(defaultValue = "create_time") String sortField,
                                              @RequestParam(defaultValue = "desc") String sortOrder,
                                              @RequestParam(defaultValue = "1") Integer page,
                                              @RequestParam(defaultValue = "10") Integer size) {
        try {
            String sort = sortField + "," + sortOrder;
            
            IPage<ArticleVO> result = searchService.advancedSearch(
                    keyword, categoryId, tag, sort, page, size, userId);
            return R.success(result);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
}