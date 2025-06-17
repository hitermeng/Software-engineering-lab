<template>
  <div class="community-container">
    <!-- 页面头部 -->
    <div class="community-header">
      <div class="header-left">
        <el-button :icon="ArrowLeft" @click="goBackToDashboard">返回</el-button>
        <h2>社区文章</h2>
      </div>
    </div>
    
    <div class="community-content">
      <!-- 筛选区域 -->
      <div class="filter-section">
        <el-card class="filter-card">
          <el-form :inline="true" :model="filterForm" class="filter-form">
            <el-form-item label="关键词">
              <el-input
                v-model="searchKeyword"
                placeholder="搜索文章..."
                clearable
                @keyup.enter="fetchArticles"
              />
            </el-form-item>
            <el-form-item label="分类">
              <el-select
                v-model="selectedCategory"
                placeholder="选择分类"
                clearable
                @change="fetchArticles"
              >
                <el-option
                  v-for="category in categories"
                  :key="category.id"
                  :label="category.name"
                  :value="category.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="排序">
              <el-select
                v-model="sortBy"
                placeholder="排序方式"
                @change="fetchArticles"
              >
                <el-option label="最新发布" value="createTime,desc" />
                <el-option label="最多浏览" value="viewCount,desc" />
                <el-option label="最多评论" value="commentCount,desc" />
                <el-option label="最多点赞" value="likeCount,desc" />
              </el-select>
            </el-form-item>
            <el-form-item label="时间范围">
              <el-date-picker
                v-model="dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                @change="fetchArticles"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="fetchArticles">搜索</el-button>
              <el-button @click="resetFilter">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </div>

      <!-- 文章列表区域 -->
      <div class="articles-section">
        <el-row :gutter="20">
          <el-col :span="16">
            <!-- 文章列表 -->
            <div class="article-list">
              <el-card v-for="article in articles" :key="article.id" class="article-card">
                <div class="article-header">
                  <h3 class="article-title" @click="viewArticle(article.id)">
                    {{ article.title }}
                  </h3>
                  <el-tag
                    v-if="article.categoryType"
                    :type="article.categoryType === 'tech' ? 'success' : 'info'"
                    size="small"
                  >
                    {{ article.categoryName }}
                  </el-tag>
                </div>
                
                <div class="article-content">
                  <p class="article-summary">{{ article.summary }}</p>
                </div>

                <div class="article-footer">
                  <div class="article-meta">
                    <el-avatar :size="24" :src="article.authorAvatar">
                      {{ article.authorName?.charAt(0) }}
                    </el-avatar>
                    <span class="author-name">{{ article.authorName }}</span>
                    <span class="publish-time">{{ formatDate(article.createTime) }}</span>
                  </div>
                  <div class="article-stats">
                    <span class="stat-item">
                      <el-icon><View /></el-icon>
                      {{ article.viewCount }}
                    </span>
                    <span class="stat-item">
                      <el-icon><ChatDotRound /></el-icon>
                      {{ article.commentCount }}
                    </span>
                    <span class="stat-item">
                      <el-icon><Star /></el-icon>
                      {{ article.likeCount }}
                    </span>
                  </div>
                </div>
              </el-card>
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
          </el-col>

          <!-- 侧边栏 -->
          <el-col :span="8">
            <el-card class="sidebar-card">
              <template #header>
                <div class="card-header">
                  <span>热门文章</span>
                </div>
              </template>
              <div class="hot-articles">
                <div v-for="article in hotArticles" :key="article.id" class="hot-article-item">
                  <span class="hot-article-title" @click="viewArticle(article.id)">
                    {{ article.title }}
                  </span>
                  <span class="hot-article-views">{{ article.viewCount }} 阅读</span>
                </div>
              </div>
            </el-card>

            <el-card class="sidebar-card">
              <template #header>
                <div class="card-header">
                  <span>热门分类</span>
                </div>
              </template>
              <div class="hot-categories">
                <el-tag
                  v-for="category in hotCategories"
                  :key="category.id"
                  :type="category.type"
                  class="category-tag"
                  @click="selectCategory(category.id)"
                >
                  {{ category.name }}
                </el-tag>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, View, ChatDotRound, Star, ArrowLeft } from '@element-plus/icons-vue'
import { articleAPI } from '@/api'
import type { ArticleVO, Category, FilterDTO, IPage } from '@/api'

const router = useRouter()

// 返回仪表盘
const goBackToDashboard = () => {
  router.push('/dashboard')
}

// 搜索和筛选相关
const searchKeyword = ref('')
const selectedCategory = ref<number | null>(null)
const sortBy = ref('latest')
const dateRange = ref<[Date, Date] | null>(null)
const categories = ref<Category[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 解决 filterForm 未定义错误
const filterForm = reactive({})

// 文章列表数据
const articles = ref<ArticleVO[]>([])
const hotArticles = ref<ArticleVO[]>([])
const hotCategories = ref<Category[]>([])

// 日期快捷选项
const dateShortcuts = [
  {
    text: '最近一周',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
      return [start, end]
    },
  },
  {
    text: '最近一个月',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
      return [start, end]
    },
  },
  {
    text: '最近三个月',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 90)
      return [start, end]
    },
  },
]

// 处理搜索
const handleSearch = () => {
  fetchArticles()
}

// 获取文章列表
const fetchArticles = async () => {
  try {
    const params: FilterDTO = {
      page: currentPage.value,
      size: pageSize.value,
      keyword: searchKeyword.value,
      categoryId: selectedCategory.value || undefined,
      sortBy: sortBy.value,
      startDate: dateRange.value?.[0]?.toISOString(),
      endDate: dateRange.value?.[1]?.toISOString(),
    }
    
    const response = await articleAPI.getPublicArticles(params)
    articles.value = response.data.records
    total.value = response.data.total
  } catch (error) {
    console.error('获取文章列表失败:', error)
    ElMessage.error('获取文章列表失败')
  }
}

// 获取热门文章
const fetchHotArticles = async () => {
  try {
    const response = await articleAPI.getHotArticles()
    hotArticles.value = response.data
  } catch (error) {
    console.error('获取热门文章失败:', error)
  }
}

// 获取分类列表
const fetchCategories = async () => {
  try {
    const response = await articleAPI.getHotCategories()
    categories.value = response.data
  } catch (error) {
    console.error('获取分类列表失败:', error)
  }
}

// 获取热门分类
const fetchHotCategories = async () => {
  try {
    const response = await articleAPI.getHotCategories()
    hotCategories.value = response.data
  } catch (error) {
    console.error('获取热门分类失败:', error)
  }
}

// 查看文章详情
const viewArticle = (id: number) => {
  router.push(`/article/${id}`)
}

// 选择分类
const selectCategory = (categoryId: number) => {
  selectedCategory.value = categoryId
  fetchArticles()
}

// 处理分页
const handleSizeChange = (val: number) => {
  pageSize.value = val
  fetchArticles()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  fetchArticles()
}

// 格式化日期
const formatDate = (date: string) => {
  return new Date(date).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  })
}

// 重置筛选条件
const resetFilter = () => {
  searchKeyword.value = ''
  selectedCategory.value = null
  sortBy.value = 'latest'
  dateRange.value = null
  currentPage.value = 1
  fetchArticles()
}

onMounted(() => {
  fetchArticles()
  fetchHotArticles()
  fetchCategories()
  fetchHotCategories()
})
</script>

<style scoped>
.community-container {
  height: 100vh;
  padding: 20px;
  background-color: #f5f7fa;
  overflow-y: auto;
}

.community-content {
  max-width: 1200px;
  margin: 0 auto;
  padding-bottom: 40px;
}

.filter-section {
  margin-bottom: 20px;
}

.filter-card {
  padding: 20px;
}

.filter-options {
  display: flex;
  gap: 16px;
  margin-top: 16px;
}

.article-card {
  margin-bottom: 16px;
  transition: transform 0.2s;
}

.article-card:hover {
  transform: translateY(-2px);
}

.article-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.article-title {
  margin: 0;
  font-size: 18px;
  color: #303133;
  cursor: pointer;
}

.article-title:hover {
  color: #409eff;
}

.article-content {
  margin-bottom: 16px;
}

.article-summary {
  color: #606266;
  margin: 0;
  line-height: 1.6;
}

.article-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16px;
}

.article-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}

.author-name {
  color: #606266;
  font-size: 14px;
}

.publish-time {
  color: #909399;
  font-size: 12px;
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
  font-size: 14px;
}

.sidebar-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.hot-article-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #ebeef5;
}

.hot-article-title {
  cursor: pointer;
  color: #606266;
  flex: 1;
  margin-right: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.hot-article-title:hover {
  color: #409eff;
}

.hot-article-views {
  color: #909399;
  font-size: 12px;
}

.hot-categories {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.category-tag {
  cursor: pointer;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .filter-options {
    flex-direction: column;
  }
  
  .article-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}
</style> 