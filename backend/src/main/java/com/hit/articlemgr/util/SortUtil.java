package com.hit.articlemgr.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SortUtil {
  private static final Map<String, String> FIELD_MAPPING = new HashMap<>();
  
  static {
    // 映射 Java 属性名到数据库列名
    FIELD_MAPPING.put("createTime", "create_time");
    FIELD_MAPPING.put("updateTime", "update_time");
    FIELD_MAPPING.put("viewCount", "view_count");
    FIELD_MAPPING.put("likeCount", "like_count");
    FIELD_MAPPING.put("commentCount", "comment_count");
  }
  
  public static String parseSort(String sort) {
    if (sort == null || sort.isEmpty()) {
      return "a.create_time DESC";
    }
    
    String[] parts = sort.split(",");
    if (parts.length != 2) {
      return "a.create_time DESC";
    }
    
    String field = parts[0];
    String direction = parts[1].toUpperCase();
    
    // 字段映射到数据库列名
    String dbField = FIELD_MAPPING.getOrDefault(field, field);
    
    // 防止SQL注入，只允许特定字段
    if (!isValidField(dbField)) {
      return "a.create_time DESC";
    }
    
    // 确保返回完整的 SQL 排序表达式
    return "a." + dbField + " " + direction;
  }
  
  private static boolean isValidField(String field) {
    // 允许的字段列表（不带表前缀）
    return Arrays.asList(
        "create_time", "update_time", "view_count",
        "like_count", "comment_count"
    ).contains(field);
  }
}