package com.hit.articlemanager.service;

import com.hit.articlemanager.model.Category;
import com.hit.articlemanager.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
  private final CategoryRepository categoryRepository;
  
  @Autowired
  public CategoryService(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }
  
  public Category createCategory(Category category) {
    return categoryRepository.save(category);
  }
  
  public List<Category> getUserCategories(String userId) {
    return categoryRepository.findByUserId(userId);
  }
  
  public List<Category> getChildCategories(String parentId) {
    return categoryRepository.findByParentId(parentId);
  }
  
  public void deleteCategory(String id) {
    categoryRepository.deleteById(id);
  }
}