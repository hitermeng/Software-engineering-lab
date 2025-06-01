package com.hit.articlemanager.controller;

import com.hit.articlemanager.model.Article;
import com.hit.articlemanager.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {
  private final ArticleService articleService;
  
  @Autowired
  public ArticleController(ArticleService articleService) {
    this.articleService = articleService;
  }
  
  @PostMapping
  public ResponseEntity<Article> createArticle(@RequestBody Article article) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserId = authentication.getName();
    
    article.setUserId(currentUserId);
    Article savedArticle = articleService.createArticle(article);
    return ResponseEntity.ok(savedArticle);
  }
  
  @PutMapping("/{id}")
  public ResponseEntity<Article> updateArticle(@PathVariable String id, @RequestBody Article article) {
    Article updatedArticle = articleService.updateArticle(id, article);
    return ResponseEntity.ok(updatedArticle);
  }
  
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteArticle(@PathVariable String id) {
    articleService.deleteArticle(id);
    return ResponseEntity.noContent().build();
  }
  
  @GetMapping
  public ResponseEntity<List<Article>> getUserArticles() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserId = authentication.getName();
    
    List<Article> articles = articleService.getUserArticles(currentUserId);
    return ResponseEntity.ok(articles);
  }
  
  @GetMapping("/search")
  public ResponseEntity<List<Article>> searchArticles(
      @RequestParam(required = false) String query,
      @RequestParam(required = false) String category,
      @RequestParam(required = false) Date startDate,
      @RequestParam(required = false) Date endDate) {
    
    List<Article> articles = articleService.searchArticles(query, category, startDate, endDate);
    return ResponseEntity.ok(articles);
  }
  
  @PatchMapping("/{id}/share")
  public ResponseEntity<Article> toggleArticleShare(@PathVariable String id, @RequestBody ShareRequest shareRequest) {
    Article article = articleService.updateArticle(id, new Article() {{
      setPublic(shareRequest.isPublic());
    }});
    return ResponseEntity.ok(article);
  }
  
  // Inner class for share request
  static class ShareRequest {
    private boolean isPublic;
    
    public boolean isPublic() {
      return isPublic;
    }
    
    public void setPublic(boolean aPublic) {
      isPublic = aPublic;
    }
  }
}