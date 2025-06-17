package com.hit.articlemgr.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hit.articlemgr.dto.CommentDTO;
import com.hit.articlemgr.dto.CommentVO;

/**
 * 评论服务接口
 */
public interface CommentService {

    /**
     * 创建评论
     *
     * @param commentDTO 评论DTO
     * @param userId 用户ID
     * @return 评论VO
     */
    CommentVO createComment(CommentDTO commentDTO, Long userId);

    /**
     * 删除评论
     *
     * @param commentId 评论ID
     * @param userId 用户ID
     */
    void deleteComment(Long commentId, Long userId);

    /**
     * 获取文章评论列表
     *
     * @param articleId 文章ID
     * @param page 页码
     * @param size 每页大小
     * @return 评论分页数据
     */
    IPage<CommentVO> getArticleComments(Long articleId, Integer page, Integer size);
} 