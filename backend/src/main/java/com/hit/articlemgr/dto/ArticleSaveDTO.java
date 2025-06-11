package com.hit.articlemgr.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 文章保存DTO
 *
 * @author HIT
 */
@Data
public class ArticleSaveDTO {

    /**
     * 文章ID（更新时需要）
     */
    private Long id;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 文章标题
     */
    @NotBlank(message = "文章标题不能为空")
    @Size(max = 200, message = "文章标题长度不能超过200个字符")
    private String title;

    /**
     * 文章内容
     */
    @NotBlank(message = "文章内容不能为空")
    private String content;

    /**
     * 文章摘要
     */
    @Size(max = 500, message = "文章摘要长度不能超过500个字符")
    private String summary;

    /**
     * 标签，逗号分隔
     */
    @Size(max = 500, message = "标签长度不能超过500个字符")
    private String tags;

    /**
     * 是否共享：0-私有，1-共享
     */
    private Integer isShared;

    /**
     * 状态：1-正常，0-草稿
     */
    private Integer status;
}