package com.hit.articlemgr.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 分类视图对象
 *
 * @author HIT
 */
@Data
public class CategoryVO {

    /**
     * 分类ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 父分类ID
     */
    private Long parentId;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 分类描述
     */
    private String description;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 层级
     */
    private Integer level;

    /**
     * 路径
     */
    private String path;

    /**
     * 文章数量
     */
    private Long articleCount;

    /**
     * 子分类
     */
    private List<CategoryVO> children;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}