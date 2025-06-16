package com.hit.articlemgr.util;

import java.util.Arrays;

public class SortUtil {
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
    
    // 防止SQL注入，只允许特定字段
    if (!isValidField(field)) {
      return "a.create_time DESC";
    }
    
    return "a." + field + " " + direction;
  }
  
  private static boolean isValidField(String field) {
    return Arrays.asList("create_time", "update_time", "view_count", "like_count").contains(field);
  }
}