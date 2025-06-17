<template>
  <div class="search-container">
    <!-- 顶部导航栏（复用仪表盘样式） -->
    <header class="dashboard-header">
      <!-- 保持与仪表盘相同的顶部导航 -->
    </header>

    <div class="search-content">
      <!-- 搜索框区域 -->
      <div class="search-box">
        <el-input
            v-model="keyword"
            placeholder="输入关键词搜索文章..."
            clearable
            size="large"
            @keyup.enter="searchArticles"
        >
          <template #append>
            <el-button type="primary" @click="searchArticles">
              搜索
            </el-button>
          </template>
        </el-input>

        <el-link type="primary" @click="showAdvanced = !showAdvanced">
          {{ showAdvanced ? '收起高级搜索' : '展开高级搜索' }}
        </el-link>
      </div>

      <!-- 高级搜索表单 -->
      <div v-if="showAdvanced" class="advanced-search-form">
        <el-form :model="advancedFilters" label-width="80px">
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="关键词">
                <el-input v-model="advancedFilters.keyword" placeholder="标题/内容" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="分类">
                <el-select
                    v-model="advancedFilters.categoryId"
                    placeholder="选择分类"
                    clearable
                >
                  <!-- 分类选项需从API获取 -->
                  <el-option
                      v-for="category in categories"
                      :key="category.id"
                      :label="category.name"
                      :value="category.id"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="标签">
                <el-input v-model="advancedFilters.tag" placeholder="输入标签" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="排序字段">
                <el-select v-model="advancedFilters.sortField">
                  <el-option label="创建时间" value="create_time" />
                  <el-option label="更新时间" value="update_time" />
                  <el-option label="阅读量" value="view_count" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="排序方式">
                <el-select v-model="advancedFilters.sortOrder">
                  <el-option label="升序" value="asc" />
                  <el-option label="降序" value="desc" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item>
                <el-button type="primary" @click="advancedSearch">
                  高级搜索
                </el-button>
                <el-button @click="resetAdvanced">重置</el-button>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>

      <!-- 搜索结果区域 -->
      <div class="search-results">
        <div v-if="loading" class="loading-indicator">
          <el-icon class="is-loading"><Loading /></el-icon>
          搜索中...
        </div>

        <div v-if="!loading && results.length === 0" class="empty-results">
          <el-empty description="未找到相关文章" />
        </div>

        <div v-else class="result-list">
          <div v-for="article in results" :key="article.id" class="article-card">
            <h3 class="title">{{ article.title }}</h3>
            <div class="meta">
              <span class="category">{{ article.categoryName }}</span>
              <span class="time">{{ formatDate(article.createTime) }}</span>
              <el-tag
                  v-for="(tag, index) in article.tagArray"
                  :key="index"
                  size="small"
                  class="tag"
              >
                {{ tag }}
              </el-tag>
            </div>
            <div class="content-preview">
              {{ truncateContent(article.content) }}
            </div>
          </div>
        </div>

        <!-- 分页控件 -->
        <div class="pagination">
          <el-pagination
              v-model:current-page="pagination.current"
              v-model:page-size="pagination.size"
              :total="pagination.total"
              layout="prev, pager, next, jumper"
              @current-change="handlePageChange"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { searchAPI, categoryAPI, type Category, type ArticleVO } from '@/api'
import { Search, Loading } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

// 搜索关键词
const keyword = ref('')
const showAdvanced = ref(false)
const loading = ref(false)
const categories = ref<Category[]>([]) // 使用 Category 类型

// 高级搜索参数
const advancedFilters = reactive({
  keyword: '',
  categoryId: null as number | null,
  tag: '',
  sortField: 'create_time',
  sortOrder: 'desc'
})

// 分页设置
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 搜索结果
const results = ref<ArticleVO[]>([])

// 执行搜索
const searchArticles = async () => {
  if (!keyword.value.trim()) {
    ElMessage.warning('请输入搜索关键词')
    return
  }

  loading.value = true
  try {
    const response = await searchAPI.basicSearch({
      keyword: keyword.value,
      page: pagination.current,
      size: pagination.size
    })

    // 处理标签 - 将逗号分隔的字符串转为数组
    results.value = response.data.records.map(article => ({
      ...article,
      tagArray: article.tags ? article.tags.split(',') : []
    }))

    pagination.total = response.data.total
  } catch (error) {
    ElMessage.error('搜索失败: ' + (error as Error).message)
  } finally {
    loading.value = false
  }
}

// 执行高级搜索
const advancedSearch = async () => {
  loading.value = true
  try {
    const params = {
      ...advancedFilters,
      sort: `${advancedFilters.sortField},${advancedFilters.sortOrder}`,
      categoryId: advancedFilters.categoryId ?? undefined,
      page: pagination.current,
      size: pagination.size
    }

    const response = await searchAPI.advancedSearch(params)

    // 处理标签 - 将逗号分隔的字符串转为数组
    results.value = response.data.records.map(article => ({
      ...article,
      tagArray: article.tags ? article.tags.split(',') : []
    }))

    pagination.total = response.data.total
  } catch (error) {
    ElMessage.error('高级搜索失败: ' + (error as Error).message)
  } finally {
    loading.value = false
  }
}

// 重置高级搜索
const resetAdvanced = () => {
  advancedFilters.keyword = ''
  advancedFilters.categoryId = null
  advancedFilters.tag = ''
  advancedFilters.sortField = 'create_time'
  advancedFilters.sortOrder = 'desc'
}

// 分页变化处理
const handlePageChange = (page: number) => {
  pagination.current = page
  if (showAdvanced.value) {
    advancedSearch()
  } else {
    searchArticles()
  }
}

// 工具函数
const truncateContent = (content: string, length = 150) => {
  if (!content) return ''
  return content.length > length
      ? content.substring(0, length) + '...'
      : content
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

// 获取分类列表 - 扁平化处理
const fetchCategories = async () => {
  try {
    const response = await categoryAPI.getTree()
    // 递归扁平化分类
    const flatten = (categories: Category[], level = 0): Category[] => {
      return categories.flatMap(category => {
        const flattened = {
          ...category,
          name: '—'.repeat(level) + category.name
        }
        return [
          flattened,
          ...(category.children ? flatten(category.children, level + 1) : [])
        ]
      })
    }

    categories.value = flatten(response.data)
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

onMounted(() => {
  const routeAdvanced = route.query.advanced === 'true'
  const routeKeyword = route.query.q as string

  if (routeAdvanced) {
    showAdvanced.value = true
  }

  if (routeKeyword) {
    keyword.value = routeKeyword
    searchArticles()
  }

  fetchCategories()
})

// 监听路由变化
watch(
    () => route.query,
    (newQuery) => {
      if (newQuery.q) {
        keyword.value = newQuery.q as string
        searchArticles()
      }

      if (newQuery.advanced === 'true') {
        showAdvanced.value = true
      } else if (newQuery.advanced === 'false') {
        showAdvanced.value = false
      }
    },
    { immediate: true }
)
</script>

<style scoped>
.search-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.search-content {
  flex: 1;
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}

.search-box {
  margin-bottom: 20px;
  position: relative;
}

.advanced-search-form {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.search-results {
  margin-top: 20px;
}

.article-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  transition: transform 0.2s;
}

.article-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.article-card .title {
  margin-top: 0;
  color: #1a1a1a;
  font-size: 18px;
}

.article-card .meta {
  display: flex;
  align-items: center;
  gap: 15px;
  margin: 10px 0;
  color: #666;
  font-size: 14px;
}

.article-card .meta .category {
  background: #e6f7ff;
  padding: 2px 8px;
  border-radius: 4px;
  color: #1890ff;
}

.article-card .content-preview {
  color: #4d4d4d;
  line-height: 1.6;
}

.pagination {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}

.loading-indicator {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px;
  color: #999;
}

.empty-results {
  padding: 40px 0;
  text-align: center;
}

.tag {
  margin-left: 6px;
}
</style>