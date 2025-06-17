<template>
  <div class="article-detail-container">
    <div class="article-content">
      <el-card class="article-card">
        <template #header>
          <div class="article-header">
            <h1 class="article-title">{{ article.title }}</h1>
            <div class="article-meta">
              <el-avatar :size="32" :src="article.authorAvatar">
                {{ article.authorName?.charAt(0) }}
              </el-avatar>
              <span class="author-name">{{ article.authorName }}</span>
              <span class="publish-time">{{ formatDate(article.createTime) }}</span>
              <el-tag size="small" :type="article.categoryType">
                {{ article.categoryName }}
              </el-tag>
            </div>
          </div>
        </template>

        <div class="article-body" v-html="article.content"></div>

        <div class="article-footer">
          <div class="article-stats">
            <span class="stat-item">
              <el-icon><View /></el-icon>
              {{ article.viewCount }} 阅读
            </span>
            <span class="stat-item">
              <el-icon><ChatDotRound /></el-icon>
              {{ article.commentCount }} 评论
            </span>
            <span class="stat-item">
              <el-icon><Star /></el-icon>
              {{ article.likeCount }} 点赞
            </span>
          </div>
          <div class="article-actions">
            <el-button type="primary" :icon="Star" @click="handleLike">
              {{ isLiked ? '取消点赞' : '点赞' }}
            </el-button>
            <el-button type="success" :icon="Share" @click="handleShare">
              分享
            </el-button>
          </div>
        </div>
      </el-card>

      <!-- 评论区 -->
      <el-card class="comments-card">
        <template #header>
          <div class="comments-header">
            <h3>评论 ({{ article.commentCount }})</h3>
          </div>
        </template>

        <!-- 评论输入框 -->
        <div class="comment-input">
          <el-input
            v-model="commentContent"
            type="textarea"
            :rows="3"
            placeholder="写下你的评论..."
            :maxlength="500"
            show-word-limit
          />
          <div class="comment-actions">
            <el-button type="primary" @click="submitComment" :disabled="!commentContent.trim()">
              发表评论
            </el-button>
          </div>
        </div>

        <!-- 评论列表 -->
        <div class="comments-list">
          <div v-for="comment in comments" :key="comment.id" class="comment-item">
            <div class="comment-user">
              <el-avatar :size="32" :src="comment.userAvatar">
                {{ comment.userName?.charAt(0) }}
              </el-avatar>
              <div class="comment-user-info">
                <span class="user-name">{{ comment.userName }}</span>
                <span class="comment-time">{{ formatDate(comment.createTime) }}</span>
              </div>
            </div>
            <div class="comment-content">{{ comment.content }}</div>
            <div class="comment-actions">
              <el-button type="text" @click="replyToComment(comment)">
                回复
              </el-button>
              <el-button 
                v-if="comment.userId === currentUserId" 
                type="text" 
                @click="deleteComment(comment.id)"
              >
                删除
              </el-button>
            </div>

            <!-- 回复列表 -->
            <div v-if="comment.replies && comment.replies.length > 0" class="replies-list">
              <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
                <div class="reply-user">
                  <el-avatar :size="24" :src="reply.userAvatar">
                    {{ reply.userName?.charAt(0) }}
                  </el-avatar>
                  <div class="reply-user-info">
                    <span class="user-name">{{ reply.userName }}</span>
                    <span class="reply-time">{{ formatDate(reply.createTime) }}</span>
                  </div>
                </div>
                <div class="reply-content">
                  <span class="reply-to" v-if="reply.replyToUserName">回复 @{{ reply.replyToUserName }}：</span>
                  {{ reply.content }}
                </div>
                <div class="reply-actions">
                  <el-button type="text" @click="replyToComment(comment, reply)">
                    回复
                  </el-button>
                  <el-button 
                    v-if="reply.userId === currentUserId" 
                    type="text" 
                    @click="deleteComment(reply.id)"
                  >
                    删除
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="total"
            :page-sizes="[10, 20, 30, 50]"
            layout="total, sizes, prev, pager, next"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { View, ChatDotRound, Star, Share } from '@element-plus/icons-vue'
import { articleAPI } from '@/api'
import { useAuthStore } from '@/store/auth'
import type { ArticleVO, Comment, CommentDTO } from '@/api'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

// 文章数据
const article = ref<ArticleVO>({
  id: 0,
  title: '',
  content: '',
  summary: '',
  categoryId: undefined,
  categoryName: '',
  categoryType: '',
  tags: '',
  tagArray: [],
  status: 0,
  isShared: 0,
  viewCount: 0,
  likeCount: 0,
  commentCount: 0,
  userId: 0,
  username: '',
  authorName: '',
  authorAvatar: '',
  isLiked: false,
  createTime: '',
  updateTime: ''
})

// 评论相关
const comments = ref<Comment[]>([])
const commentContent = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const isLiked = ref(false)
const currentUserId = ref(authStore.user?.id)

// 获取文章详情
const fetchArticleDetail = async () => {
  try {
    const articleId = Number(route.params.id)
    const response = await articleAPI.getArticleDetail(articleId)
    article.value = response.data
    isLiked.value = response.data.isLiked
  } catch (error) {
    console.error('获取文章详情失败:', error)
    ElMessage.error('获取文章详情失败')
  }
}

// 获取评论列表
const fetchComments = async () => {
  try {
    const articleId = Number(route.params.id)
    const response = await articleAPI.getComments({
      articleId: articleId,
      page: currentPage.value,
      size: pageSize.value
    })
    comments.value = response.data.records
    total.value = response.data.total
  } catch (error) {
    console.error('获取评论列表失败:', error)
    ElMessage.error('获取评论列表失败')
  }
}

// 提交评论
const submitComment = async () => {
  if (!authStore.isAuthenticated) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  try {
    const articleId = Number(route.params.id)
    await articleAPI.createComment({
      articleId: articleId,
      content: commentContent.value
    } as CommentDTO) // Cast to CommentDTO to ensure correct type for API
    ElMessage.success('评论成功')
    commentContent.value = ''
    fetchComments()
  } catch (error) {
    console.error('提交评论失败:', error)
    ElMessage.error('提交评论失败')
  }
}

// 回复评论
const replyToComment = (comment: Comment, replyTo?: Comment) => {
  if (!authStore.isAuthenticated) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  commentContent.value = replyTo 
    ? `回复 @${replyTo.userName}：`
    : `回复 @${comment.userName}：`
}

// 删除评论
const deleteComment = async (commentId: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这条评论吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await articleAPI.deleteComment(commentId)
    ElMessage.success('删除成功')
    fetchComments()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除评论失败:', error)
      ElMessage.error('删除评论失败')
    }
  }
}

// 点赞文章
const handleLike = async () => {
  if (!authStore.isAuthenticated) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  try {
    const articleId = Number(route.params.id)
    if (isLiked.value) {
      await articleAPI.unlikeArticle(articleId)
      article.value.likeCount--
    } else {
      await articleAPI.likeArticle(articleId)
      article.value.likeCount++
    }
    isLiked.value = !isLiked.value
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败')
  }
}

// 分享文章
const handleShare = () => {
  const url = window.location.href
  navigator.clipboard.writeText(url).then(() => {
    ElMessage.success('链接已复制到剪贴板')
  }).catch(() => {
    ElMessage.error('复制失败，请手动复制链接')
  })
}

// 处理分页
const handleSizeChange = (val: number) => {
  pageSize.value = val
  fetchComments()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  fetchComments()
}

// 格式化日期
const formatDate = (date: string) => {
  return new Date(date).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(() => {
  fetchArticleDetail()
  fetchComments()
})
</script>

<style scoped>
.article-detail-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.article-content {
  max-width: 800px;
  margin: 0 auto;
}

.article-card {
  margin-bottom: 20px;
}

.article-header {
  text-align: center;
}

.article-title {
  margin: 0 0 16px;
  font-size: 24px;
  color: #303133;
}

.article-meta {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  color: #909399;
  font-size: 14px;
}

.article-body {
  margin: 24px 0;
  line-height: 1.8;
  color: #303133;
}

.article-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}

.article-stats {
  display: flex;
  gap: 16px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #909399;
}

.article-actions {
  display: flex;
  gap: 12px;
}

.comments-card {
  margin-top: 20px;
}

.comments-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.comments-header h3 {
  margin: 0;
  font-size: 18px;
  color: #303133;
}

.comment-input {
  margin-bottom: 24px;
}

.comment-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}

.comment-item {
  padding: 16px 0;
  border-bottom: 1px solid #ebeef5;
}

.comment-user {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.comment-user-info {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-weight: 500;
  color: #303133;
}

.comment-time {
  font-size: 12px;
  color: #909399;
}

.comment-content {
  margin: 8px 0;
  color: #606266;
  line-height: 1.6;
}

.comment-actions {
  display: flex;
  gap: 12px;
}

.replies-list {
  margin-left: 40px;
  margin-top: 12px;
  padding-left: 12px;
  border-left: 2px solid #ebeef5;
}

.reply-item {
  padding: 12px 0;
}

.reply-user {
  display: flex;
  align-items: center;
  gap: 8px;
}

.reply-user-info {
  display: flex;
  flex-direction: column;
}

.reply-content {
  margin: 8px 0;
  color: #606266;
  line-height: 1.6;
}

.reply-to {
  color: #409eff;
  margin-right: 4px;
}

.reply-actions {
  display: flex;
  gap: 12px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .article-detail-container {
    padding: 12px;
  }

  .article-title {
    font-size: 20px;
  }

  .article-meta {
    flex-wrap: wrap;
  }

  .article-footer {
    flex-direction: column;
    gap: 12px;
  }

  .replies-list {
    margin-left: 20px;
  }
}
</style> 