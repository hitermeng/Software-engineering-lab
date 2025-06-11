package com.hit.articlemgr.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 分类DTO
 *
 * @author HIT
 */
@Data
public class CategoryDTO {

    /**
     * 分类ID（更新时需要）
     */
    private Long id;

    /**
     * 父分类ID，0表示根分类
     */
    @NotNull(message = "父分类ID不能为空")
    private Long parentId;

    /**
     * 分类名称
     */
    @NotBlank(message = "分类名称不能为空")
    @Size(max = 100, message = "分类名称长度不能超过100个字符")
    private String name;

    /**
     * 分类描述
     */
    @Size(max = 500, message = "分类描述长度不能超过500个字符")
    private String description;

    /**
     * 排序
     */
    private Integer sortOrder;

    private String color;
    private String icon;
    private Integer sort;
    private Boolean isActive;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<CategoryDTO> children;
}