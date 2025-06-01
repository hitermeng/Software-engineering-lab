package com.hit.articlemanager.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "articles")
@Data
public class Article {
  @Id
  private String id;
  private String title;
  private String content;
  private String categoryId;
  private String userId;
  private String status; // "draft" or "published"
  private boolean isPublic;
  private Date createdAt = new Date();
  private Date updatedAt = new Date();
}