package com.hit.articlemanager.repository;

import com.hit.articlemanager.model.Article;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ArticleRepository extends MongoRepository<Article, String> {
  List<Article> findByUserId(String userId);
  
  @Query("{" +
      "$and: [" +
      "  { $or: [ " +
      "    { ?0 : null }, " +
      "    { $text: { $search: ?0 } } " +
      "  ] }," +
      "  { $or: [ " +
      "    { ?1 : null }, " +
      "    { categoryId: ?1 } " +
      "  ] }," +
      "  { $or: [ " +
      "    { ?2 : null }, " +
      "    { createdAt: { $gte: ?2 } } " +
      "  ] }," +
      "  { $or: [ " +
      "    { ?3 : null }, " +
      "    { createdAt: { $lte: ?3 } } " +
      "  ] }" +
      "]" +
      "}")
  List<Article> searchArticles(String query, String categoryId, Date startDate, Date endDate);
}