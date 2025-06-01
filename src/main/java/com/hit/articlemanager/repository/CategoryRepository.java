package com.hit.articlemanager.repository;

import com.hit.articlemanager.model.Category;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CategoryRepository extends MongoRepository<Category, String> {
  List<Category> findByUserId(String userId);
  List<Category> findByParentId(String parentId);
}