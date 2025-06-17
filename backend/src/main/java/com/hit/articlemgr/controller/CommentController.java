package com.hit.articlemgr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.hit.articlemgr.service.CommentService;
import com.hit.articlemgr.dto.CommentVO;
import com.baomidou.mybatisplus.core.metadata.IPage;

@RestController
@RequestMapping("/api/articles")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/{articleId}/comments")
    public IPage<CommentVO> getArticleComments(@PathVariable Long articleId, @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size) {
        return commentService.getArticleComments(articleId, page, size);
    }
} 