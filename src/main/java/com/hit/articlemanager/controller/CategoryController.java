package com.hit.articlemanager.controller;

import com.hit.articlemanager.model.Category;
import com.hit.articlemanager.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
  private final CategoryService categoryService;
  
  @Autowired
  public CategoryController(CategoryService categoryService) {
    this.categoryService = categoryService;
  }
  
  @PostMapping
  public ResponseEntity<Category> createCategory(@RequestBody Category category) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserId = authentication.getName();
    
    category.setUserId(currentUserId);
    Category savedCategory = categoryService.createCategory(category);
    return ResponseEntity.ok(savedCategory);
  }
  
  @GetMapping
  public ResponseEntity<List<Category>> getUserCategories() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserId = authentication.getName();
    
    List<Category> categories = categoryService.getUserCategories(currentUserId);
    return ResponseEntity.ok(categories);
  }
  
  @GetMapping("/{parentId}/children")
  public ResponseEntity<List<Category>> getChildCategories(@PathVariable String parentId) {
    List<Category> childCategories = categoryService.getChildCategories(parentId);
    return ResponseEntity.ok(childCategories);
  }
  
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCategory(@PathVariable String id) {
    categoryService.deleteCategory(id);
    return ResponseEntity.noContent().build();
  }
}