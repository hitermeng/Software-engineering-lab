package com.hit.articlemgr.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hit.articlemgr.dto.ArticleSaveDTO;
import com.hit.articlemgr.dto.ArticleVO;
import com.hit.articlemgr.dto.CategoryVO;
import com.hit.articlemgr.dto.FilterDTO;
import com.hit.articlemgr.entity.Article;
import com.hit.articlemgr.entity.ArticleLike;
import com.hit.articlemgr.entity.Category;
import com.hit.articlemgr.entity.User;
import com.hit.articlemgr.exception.BusinessException;
import com.hit.articlemgr.mapper.ArticleLikeMapper;
import com.hit.articlemgr.mapper.ArticleMapper;
import com.hit.articlemgr.mapper.CategoryMapper;
import com.hit.articlemgr.mapper.UserMapper;
import com.hit.articlemgr.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
//import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import lombok.extern.slf4j.Slf4j;

//import java.io.IOException;
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
@Slf4j
public class ArticleServiceImpl implements ArticleService {

    private final ArticleMapper articleMapper;
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;
    private final ArticleLikeMapper articleLikeMapper;

    @Value("${llm.api.url:}")
    private String llmApiUrl;

    @Value("${llm.api.key:}")
    private String llmApiKey;

    //private final RestTemplate restTemplate = new RestTemplate();

    @Override
    @Transactional
    public Article createArticle(ArticleSaveDTO articleSaveDTO, Long userId) {
        // 验证分类是否存在
        if (articleSaveDTO.getCategoryId() != null) {
            Category category = categoryMapper.selectById(articleSaveDTO.getCategoryId());
            if (category == null || !category.getUserId().equals(userId)) {
                throw new BusinessException("分类不存在或无权限");
            }
        }

        // 创建文章
        Article article = new Article();
        BeanUtils.copyProperties(articleSaveDTO, article);
        article.setUserId(userId);
        article.setViewCount(0);
        article.setLikeCount(0);
        article.setCommentCount(0);

        articleMapper.insert(article);
        return article;
    }

    @Override
    @Transactional
    public Article updateArticle(ArticleSaveDTO articleSaveDTO, Long userId) {
        // 验证文章是否存在
        Article existingArticle = articleMapper.selectById(articleSaveDTO.getId());
        if (existingArticle == null) {
            throw new BusinessException("文章不存在");
        }

        // 验证权限
        if (!existingArticle.getUserId().equals(userId)) {
            throw new BusinessException("无权限修改此文章");
        }

        // 验证分类是否存在
        if (articleSaveDTO.getCategoryId() != null) {
            Category category = categoryMapper.selectById(articleSaveDTO.getCategoryId());
            if (category == null || !category.getUserId().equals(userId)) {
                throw new BusinessException("分类不存在或无权限");
            }
        }

        // 更新文章
        Article article = new Article();
        BeanUtils.copyProperties(articleSaveDTO, article);
        articleMapper.updateById(article);

        return article;
    }

    @Override
    @Transactional
    public void deleteArticle(Long id, Long userId) {
        // 验证文章是否存在
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }

        // 验证权限
        if (!article.getUserId().equals(userId)) {
            throw new BusinessException("无权限删除此文章");
        }

        // 删除文章
        articleMapper.deleteById(id);
    }

    @Override
    public ArticleVO getArticleById(Long id, Long userId) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            return null;
        }

        ArticleVO articleVO = new ArticleVO();
        BeanUtils.copyProperties(article, articleVO);
        
        // 设置分类信息
        if (article.getCategoryId() != null) {
            Category category = categoryMapper.selectById(article.getCategoryId());
            if (category != null) {
                articleVO.setCategoryName(category.getName());
                articleVO.setCategoryType(category.getType());
            }
        }

        // 设置作者信息
        if (article.getUserId() != null) {
            User user = userMapper.selectById(article.getUserId());
            if (user != null) {
                articleVO.setUsername(user.getUsername());
                articleVO.setAuthorName(user.getNickname());
                articleVO.setAuthorAvatar(user.getAvatar());
            }
        }

        // 设置标签数组
        if (StringUtils.hasText(article.getTags())) {
            articleVO.setTagArray(Arrays.asList(article.getTags().split(",")));
        }

        return articleVO;
    }

    @Override
    public IPage<ArticleVO> getArticleList(FilterDTO filter, Long userId) {
        Page<ArticleVO> page = new Page<>(filter.getPage(), filter.getSize());
        IPage<ArticleVO> articlePage = articleMapper.findArticleList(page, filter, userId);
        
        // 处理文章列表
        articlePage.getRecords().forEach(article -> {
            // 设置分类信息
            if (article.getCategoryId() != null) {
                Category category = categoryMapper.selectById(article.getCategoryId());
                if (category != null) {
                    article.setCategoryName(category.getName());
                    article.setCategoryType(category.getType());
                }
            }

            // 设置作者信息
            if (article.getUserId() != null) {
                User user = userMapper.selectById(article.getUserId());
                if (user != null) {
                    article.setUsername(user.getUsername());
                    article.setAuthorName(user.getNickname());
                    article.setAuthorAvatar(user.getAvatar());
                }
            }

            // 设置标签数组
            if (StringUtils.hasText(article.getTags())) {
                article.setTagArray(Arrays.asList(article.getTags().split(",")));
            }
        });
        
        return articlePage;
    }

    @Override
    @Transactional
    public void incrementViewCount(Long id) {
        articleMapper.incrementViewCount(id);
    }

    @Override
    @Transactional
    public void incrementLikeCount(Long id) {
        Article article = articleMapper.selectById(id);
        if (article != null) {
            article.setLikeCount(article.getLikeCount() + 1);
            articleMapper.updateById(article);
        }
    }

    @Override
    public List<String> getPopularTags(Long userId) {
        List<String> tags = articleMapper.getPopularTags(userId);
        return tags.stream()
                .filter(StringUtils::hasText)
                .flatMap(tag -> Arrays.stream(tag.split(",")))
                .distinct()
                .collect(Collectors.toList());
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

        // 移除HTML标签以获取纯文本内容
        String plainText = content.replaceAll("<[^>]*>", "");

        // ==================================================================
        // 接入阿里云通义千问API以生成更智能的摘要
        // ==================================================================

        if (llmApiUrl != null && !llmApiUrl.isEmpty() && llmApiKey != null && !llmApiKey.isEmpty()) {
            try {
                log.info("开始调用通义千问API生成摘要...");
                Generation gen = new Generation();

                // System Message: 可以根据需要调整
                Message systemMsg = Message.builder()
                        .role(Role.SYSTEM.getValue())
                        .content("你是一个专业的文章摘要生成助手。请根据用户提供的文章内容生成一个简洁、准确且完整的摘要。要求：1. 摘要长度控制在300字以内；2. 确保摘要的完整性和逻辑性；3. 保留文章的核心观点和关键信息；4. 使用清晰的语言表达。")
                        .build();
                // User Message: 文章内容
                Message userMsg = Message.builder()
                        .role(Role.USER.getValue())
                        .content("请为以下文章生成一个简洁的摘要，确保摘要的完整性和逻辑性：\n\n" + plainText)
                        .build();

                GenerationParam param = GenerationParam.builder()
                        .apiKey(llmApiKey)
                        .model("qwen-plus")
                        .messages(Arrays.asList(systemMsg, userMsg))
                        .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                        .build();

                GenerationResult result = gen.call(param);
                log.info("通义千问API调用完成。结果: {}", result);

                if (result != null && result.getOutput() != null && !result.getOutput().getChoices().isEmpty()) {
                    String generatedSummary = result.getOutput().getChoices().get(0).getMessage().getContent();
                    log.info("成功生成摘要: {}", generatedSummary);
                    
                    // 清理和规范化摘要
                    generatedSummary = generatedSummary.trim()
                            .replaceAll("\\s+", " ")  // 规范化空白字符
                            .replaceAll("。+", "。")  // 删除重复的句号
                            .replaceAll("！+", "！")  // 删除重复的感叹号
                            .replaceAll("？+", "？"); // 删除重复的问号
                    
                    // 如果摘要超过300字，尝试在句子边界处截断
                    if (generatedSummary.length() > 300) {
                        int lastPeriod = generatedSummary.substring(0, 300).lastIndexOf("。");
                        int lastExclamation = generatedSummary.substring(0, 300).lastIndexOf("！");
                        int lastQuestion = generatedSummary.substring(0, 300).lastIndexOf("？");
                        
                        int lastSentenceEnd = Math.max(Math.max(lastPeriod, lastExclamation), lastQuestion);
                        if (lastSentenceEnd > 0) {
                            return generatedSummary.substring(0, lastSentenceEnd + 1);
                        }
                        return generatedSummary.substring(0, 300);
                    }
                    return generatedSummary;
                } else {
                    log.warn("千问API调用成功，但未获取到有效摘要。原始响应: {}", result);
                    return generateSimpleSummary(plainText);
                }
            } catch (ApiException | NoApiKeyException | InputRequiredException e) {
                log.error("调用阿里云通义千问API时发生特定错误: {}", e.getMessage(), e);
                return generateSimpleSummary(plainText);
            } catch (Exception e) {
                log.error("摘要生成发生未知错误: {}", e.getMessage(), e);
                return generateSimpleSummary(plainText);
            }
        } else {
            log.warn("LLM API URL 或 API Key 未配置，使用默认摘要生成逻辑。");
            return generateSimpleSummary(plainText);
        }
    }

    /**
     * 生成简单摘要的辅助方法
     */
    private String generateSimpleSummary(String plainText) {
        if (plainText.length() <= 200) {
            return plainText;
        }
        // 尝试在句子边界处截断
        int lastPeriod = plainText.substring(0, 200).lastIndexOf("。");
        int lastExclamation = plainText.substring(0, 200).lastIndexOf("！");
        int lastQuestion = plainText.substring(0, 200).lastIndexOf("？");
        
        int lastSentenceEnd = Math.max(Math.max(lastPeriod, lastExclamation), lastQuestion);
        if (lastSentenceEnd > 0) {
            return plainText.substring(0, lastSentenceEnd + 1);
        }
        return plainText.substring(0, 200) + "...";
    }

    @Override
    public List<ArticleVO> getRecentArticles(Long userId) {
        FilterDTO filter = new FilterDTO();
        filter.setPage(1);
        filter.setSize(10);
        filter.setSortField("create_time");
        filter.setSortOrder("desc");
        
        IPage<ArticleVO> page = getArticleList(filter, userId);
        return page.getRecords();
    }

    @Override
    public List<ArticleVO> getHotArticles() {
        FilterDTO filter = new FilterDTO();
        filter.setPage(1);
        filter.setSize(10);
        filter.setSortField("view_count");
        filter.setSortOrder("desc");
        filter.setIsShared(1);
        filter.setStatus(1);
        
        IPage<ArticleVO> page = getArticleList(filter, null);
        return page.getRecords();
    }

    @Override
    public List<CategoryVO> getHotCategories() {
        return categoryMapper.selectHotCategories().stream()
                .map(category -> {
                    CategoryVO vo = new CategoryVO();
                    BeanUtils.copyProperties(category, vo);
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void decrementLikeCount(Long id) {
        Article article = articleMapper.selectById(id);
        if (article != null && article.getLikeCount() > 0) {
            article.setLikeCount(article.getLikeCount() - 1);
            articleMapper.updateById(article);
        }
    }

    @Override
    public ArticleVO getArticleDetail(Long id, Long userId) {
        ArticleVO article = articleMapper.findArticleById(id, userId);
        if (article != null) {
            processArticleVO(article, userId);
        }
        return article;
    }

    /**
     * 处理文章VO对象
     */
    private void processArticleVO(ArticleVO article, Long userId) {
        // 处理标签
        if (StringUtils.hasText(article.getTags())) {
            article.setTagArray(Arrays.asList(article.getTags().split(",")));
        } else {
            article.setTagArray(new ArrayList<>());
        }

        // 获取用户信息
        User user = userMapper.selectById(article.getUserId());
        if (user != null) {
            article.setUsername(user.getUsername());
            article.setUserNickname(user.getNickname());
            article.setAuthorName(user.getNickname());
            article.setAuthorAvatar(user.getAvatar());
        }

        // 获取分类信息
        if (article.getCategoryId() != null) {
            Category category = categoryMapper.selectById(article.getCategoryId());
            if (category != null) {
                article.setCategoryName(category.getName());
            }
        }
    }
}