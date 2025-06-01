package com.hit.articlemanager.service;

import com.hit.articlemanager.model.Article;
import com.hit.articlemanager.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ArticleService {
  private final ArticleRepository articleRepository;
  
  @Autowired
  public ArticleService(ArticleRepository articleRepository) {
    this.articleRepository = articleRepository;
  }
  
  public Article createArticle(Article article) {
    article.setCreatedAt(new Date());
    article.setUpdatedAt(new Date());
    return articleRepository.save(article);
  }
  
  public Article updateArticle(String id, Article article) {
    Article existingArticle = articleRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Article not found"));
    
    existingArticle.setTitle(article.getTitle());
    existingArticle.setContent(article.getContent());
    existingArticle.setCategoryId(article.getCategoryId());
    existingArticle.setStatus(article.getStatus());
    existingArticle.setPublic(article.isPublic());
    existingArticle.setUpdatedAt(new Date());
    
    return articleRepository.save(existingArticle);
  }
  
  public void deleteArticle(String id) {
    articleRepository.deleteById(id);
  }
  
  public List<Article> getUserArticles(String userId) {
    return articleRepository.findByUserId(userId);
  }
  
  public List<Article> searchArticles(String query, String categoryId, Date startDate, Date endDate) {
    return articleRepository.searchArticles(query, categoryId, startDate, endDate);
  }
}