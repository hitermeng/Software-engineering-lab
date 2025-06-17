package com.hit.articlemgr.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hit.articlemgr.dto.ArticleSaveDTO;
import com.hit.articlemgr.dto.ArticleVO;
import com.hit.articlemgr.dto.CategoryVO;
import com.hit.articlemgr.dto.FilterDTO;
import com.hit.articlemgr.entity.Article;
import com.hit.articlemgr.mapper.ArticleMapper;
import com.hit.articlemgr.service.ArticleService;
import com.hit.articlemgr.util.R;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文章控制器
 *
 * @author HIT
 */
@RestController
@RequestMapping("/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    /**
     * 创建文章
     */
    @PostMapping
    public R<Article> createArticle(@AuthenticationPrincipal Long userId,
                                    @Validated @RequestBody ArticleSaveDTO articleSaveDTO) {
        try {
            Article article = articleService.createArticle(articleSaveDTO, userId);
            return R.success("创建成功", article);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    /**
     * 更新文章
     */
    @PutMapping("/{id}")
    public R<Article> updateArticle(@AuthenticationPrincipal Long userId,
                                    @PathVariable Long id,
                                    @Validated @RequestBody ArticleSaveDTO articleSaveDTO) {
        try {
            articleSaveDTO.setId(id);
            Article article = articleService.updateArticle(articleSaveDTO, userId);
            return R.success("更新成功", article);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    /**
     * 删除文章
     */
    @DeleteMapping("/{id}")
    public R<Void> deleteArticle(@AuthenticationPrincipal Long userId,
                                 @PathVariable Long id) {
        try {
            articleService.deleteArticle(id, userId);
//            return R.success("删除成功");
            return R.success("删除成功", (Void)null);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    /**
     * 获取文章详情
     */
    @GetMapping("/{id}")
    public R<ArticleVO> getArticleById(@AuthenticationPrincipal Long userId,
                                       @PathVariable Long id) {
        try {
            ArticleVO article = articleService.getArticleById(id, userId);
            if (article == null) {
                return R.notFound("文章不存在");
            }

            // 增加阅读次数
            articleService.incrementViewCount(id);

            return R.success(article);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    /**
     * 分页查询文章列表
     */
    @GetMapping
    public R<IPage<ArticleVO>> getArticleList(@AuthenticationPrincipal Long userId,
                                              FilterDTO filter) {
        try {
            IPage<ArticleVO> articlePage = articleService.getArticleList(filter, userId);
            return R.success(articlePage);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    /**
     * 获取共享文章列表
     */
    @GetMapping("/shared")
    public R<IPage<ArticleVO>> getSharedArticles(@AuthenticationPrincipal Long userId,
                                                 FilterDTO filter) {
        try {
            filter.setIsShared(1);
            filter.setOnlyMine(false);
            IPage<ArticleVO> articlePage = articleService.getArticleList(filter, userId);
            return R.success(articlePage);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    /**
     * 点赞文章
     */
    @PostMapping("/{id}/like")
    public R<Void> likeArticle(@PathVariable Long id) {
        try {
            articleService.incrementLikeCount(id);
            return R.success("点赞成功", (Void)null);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    /**
     * 获取热门标签
     */
    @GetMapping("/tags/popular")
    public R<List<String>> getPopularTags(@AuthenticationPrincipal Long userId) {
        try {
            List<String> tags = articleService.getPopularTags(userId);
            return R.success(tags);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    /**
     * 获取文章统计数据
     */
    @GetMapping("/statistics")
    public R<ArticleMapper.ArticleStatistics> getStatistics(@AuthenticationPrincipal Long userId) {
        try {
            ArticleMapper.ArticleStatistics statistics = articleService.getArticleStatistics(userId);
            return R.success(statistics);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    /**
     * 生成文章摘要
     */
    @PostMapping("/generate-summary")
    public R<String> generateSummary(@RequestBody String content) {
        try {
            String summary = articleService.generateSummary(content);
            return R.success(summary);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    /**
     * 获取最近文章列表
     */
    @GetMapping("/recent")
    public R<List<ArticleVO>> getRecentArticles(@AuthenticationPrincipal Long userId) {
        try {
            // 默认获取最近5篇已发布的文章
            List<ArticleVO> articles = articleService.getRecentArticles(userId);
            return R.success(articles);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    /**
     * 获取公开文章列表（不需要登录）
     */
    @GetMapping("/public")
    public R<IPage<ArticleVO>> getPublicArticles(FilterDTO filter) {
        try {
            filter.setIsShared(1);
            filter.setStatus(1); // 只查询已发布的文章
            IPage<ArticleVO> articlePage = articleService.getArticleList(filter, null);
            return R.success(articlePage);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    /**
     * 获取热门文章
     */
    @GetMapping("/hot")
    public R<List<ArticleVO>> getHotArticles() {
        try {
            List<ArticleVO> articles = articleService.getHotArticles();
            return R.success(articles);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    /**
     * 获取热门分类
     */
    @GetMapping("/categories/hot")
    public R<List<CategoryVO>> getHotCategories() {
        try {
            List<CategoryVO> categories = articleService.getHotCategories();
            return R.success(categories);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    /**
     * 取消点赞
     */
    @DeleteMapping("/{id}/like")
    public R<Void> unlikeArticle(@PathVariable Long id) {
        try {
            articleService.decrementLikeCount(id);
            return R.success("取消点赞成功", null);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    /**
     * 获取文章详情（包含点赞状态）
     */
    @GetMapping("/{id}/detail")
    public R<ArticleVO> getArticleDetail(@PathVariable Long id,
                                        @AuthenticationPrincipal Long userId) {
        try {
            ArticleVO article = articleService.getArticleDetail(id, userId);
            if (article == null) {
                return R.notFound("文章不存在");
            }

            // 增加阅读次数
            articleService.incrementViewCount(id);

            return R.success(article);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
}