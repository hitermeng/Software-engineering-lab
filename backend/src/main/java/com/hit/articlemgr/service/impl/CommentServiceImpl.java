package com.hit.articlemgr.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hit.articlemgr.dto.CommentDTO;
import com.hit.articlemgr.dto.CommentVO;
import com.hit.articlemgr.entity.Article;
import com.hit.articlemgr.entity.Comment;
import com.hit.articlemgr.entity.User;
import com.hit.articlemgr.exception.BusinessException;
import com.hit.articlemgr.mapper.ArticleMapper;
import com.hit.articlemgr.mapper.CommentMapper;
import com.hit.articlemgr.mapper.UserMapper;
import com.hit.articlemgr.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 评论服务实现类
 */
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final ArticleMapper articleMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public CommentVO createComment(CommentDTO commentDTO, Long userId) {
        // 验证文章是否存在
        Article article = articleMapper.selectById(commentDTO.getArticleId());
        if (article == null) {
            throw new BusinessException("文章不存在");
        }

        // 如果是回复评论，验证父评论是否存在
        if (commentDTO.getParentId() != null) {
            Comment parentComment = commentMapper.selectById(commentDTO.getParentId());
            if (parentComment == null) {
                throw new BusinessException("父评论不存在");
            }
        }

        // 创建评论
        Comment comment = new Comment();
        comment.setArticleId(commentDTO.getArticleId());
        comment.setContent(commentDTO.getContent());
        comment.setUserId(userId);
        comment.setParentId(commentDTO.getParentId());
        comment.setReplyToUserId(commentDTO.getReplyToUserId());

        commentMapper.insert(comment);

        // 更新文章评论数
        article.setCommentCount(article.getCommentCount() + 1);
        articleMapper.updateById(article);

        // 转换为VO并返回
        return convertToVO(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }

        // 验证是否是评论作者或管理员
        if (!comment.getUserId().equals(userId)) {
            User user = userMapper.selectById(userId);
            if (user == null || !"ADMIN".equals(user.getRole())) {
                throw new BusinessException("无权删除此评论");
            }
        }

        // 删除评论及其所有回复
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getId, commentId)
                .or()
                .eq(Comment::getParentId, commentId);
        int deleteCount = commentMapper.delete(wrapper);

        // 更新文章评论数
        Article article = articleMapper.selectById(comment.getArticleId());
        if (article != null) {
            article.setCommentCount(article.getCommentCount() - deleteCount);
            articleMapper.updateById(article);
        }
    }

    @Override
    public IPage<CommentVO> getArticleComments(Long articleId, Integer page, Integer size) {
        // 分页查询顶级评论
        Page<CommentVO> commentPage = new Page<>(page, size);
        IPage<CommentVO> topComments = commentMapper.selectArticleComments(commentPage, articleId);

        // 获取所有评论ID
        List<Long> commentIds = topComments.getRecords().stream()
                .map(CommentVO::getId)
                .collect(Collectors.toList());

        // 查询所有回复
        List<CommentVO> replies = new ArrayList<>();
        if (!commentIds.isEmpty()) {
            replies = commentMapper.selectCommentReplies(commentIds);
        }

        // 按父评论ID分组
        Map<Long, List<CommentVO>> replyMap = replies.stream()
                .collect(Collectors.groupingBy(CommentVO::getParentId));

        // 设置回复列表
        topComments.getRecords().forEach(comment -> {
            List<CommentVO> commentReplies = replyMap.getOrDefault(comment.getId(), new ArrayList<>());
            comment.setReplies(commentReplies);
        });

        return topComments;
    }

    /**
     * 将Comment实体转换为CommentVO
     */
    private CommentVO convertToVO(Comment comment) {
        CommentVO vo = new CommentVO();
        BeanUtils.copyProperties(comment, vo);

        // 获取用户信息
        User user = userMapper.selectById(comment.getUserId());
        if (user != null) {
            vo.setUserName(user.getUsername());
            vo.setUserAvatar(user.getAvatar());
        }

        // 如果是回复评论，获取被回复用户信息
        if (comment.getReplyToUserId() != null) {
            User replyToUser = userMapper.selectById(comment.getReplyToUserId());
            if (replyToUser != null) {
                vo.setReplyTo(replyToUser.getUsername());
            }
        }

        return vo;
    }
} 