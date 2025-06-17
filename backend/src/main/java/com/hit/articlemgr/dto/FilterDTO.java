package com.hit.articlemgr.dto;

import lombok.Data;

//import java.time.LocalDateTime;

/**
 * 查询过滤DTO
 *
 * @author HIT
 */
@Data
public class FilterDTO {
    
    /**
     * 关键字（标题、内容）
     */
    private String keyword;
    
    /**
     * 分类ID
     */
    private Long categoryId;
    
    /**
     * 标签
     */
    private String tag;
    
    /**
     * 是否共享：0-私有，1-共享
     */
    private Integer isShared;
    
    /**
     * 状态：1-正常，0-草稿
     */
    private Integer status;
    
    /**
     * 开始时间
     */
    private String startDate;
    
    /**
     * 结束时间
     */
    private String endDate;
    
    /**
     * 排序字段
     */
    private String sort;
    
    private String sortField;

    /**
     * 排序方向：asc-升序，desc-降序
     */
    private String sortOrder;
    
    /**
     * 页码
     */
    private Integer page = 1;
    
    /**
     * 每页数量
     */
    private Integer size = 10;
    
    /**
     * 是否只查询自己的文章
     */
    private Boolean onlyMine = false;
}