package com.hit.articlemgr.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hit.articlemgr.dto.CommentDTO;
import com.hit.articlemgr.dto.CommentVO;
import com.hit.articlemgr.service.CommentService;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {

    @Override
    public CommentVO createComment(CommentDTO commentDTO, Long userId) {
        // 实现创建评论的逻辑
        return null; // 临时返回
    }

    @Override
    public void deleteComment(Long commentId, Long userId) {
        // 实现删除评论的逻辑
    }

    @Override
    public IPage<CommentVO> getArticleComments(Long articleId, Integer page, Integer size) {
        // 实现获取文章评论列表的逻辑
        return null; // 临时返回
    }
} 