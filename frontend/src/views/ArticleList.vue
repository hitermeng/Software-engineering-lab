<template>
  <div class="article-list-container">
    <!-- 头部操作栏 -->
    <div class="article-header">
      <div class="header-left">
        <h2>{{ isShared ? '共享文章' : '我的文章' }}</h2>
        <el-tag v-if="totalCount > 0" type="info">
          共 {{ totalCount }} 篇文章
        </el-tag>
      </div>

      <div class="header-right">
        <el-button
            v-if="!isShared"
            type="primary"
            @click="$router.push('/dashboard/editor')"
        >
          <el-icon><EditPen /></el-icon>
          写文章
        </el-button>
      </div>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <el-form :model="filterForm" inline>
        <el-form-item label="分类">
          <el-select
              v-model="filterForm.categoryId"
              placeholder="选择分类"
              clearable
              style="width: 150px"
          >
            <el-option
                v-for="category in categoryStore.flatCategories"
                :key="category.id"
                :label="'　'.repeat(category.level) + category.name"
                :value="category.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="状态" v-if="!isShared">
          <el-select
              v-model="filterForm.status"
              placeholder="选择状态"
              clearable
              style="width: 120px"
          >
<!--            <el-option label="已发布" value="PUBLISHED" />-->
<!--            <el-option label="草稿" value="DRAFT" />-->
            <el-option label="已发布" value="1" />
            <el-option label="草稿" value="0" />
          </el-select>
        </el-form-item>

        <el-form-item label="时间">
          <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              style="width: 220px"
          />
        </el-form-item>

        <el-form-item>
          <el-button @click="resetFilter">重置</el-button>
          <el-button type="primary" @click="handleFilter">筛选</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 文章列表 -->
    <div class="article-content">
      <el-card v-loading="loading">
        <div v-if="articles.length > 0" class="article-grid">
          <div
              v-for="article in articles"
              :key="article.id"
              class="article-card"
              @click="viewArticle(article.id)"
          >
            <div class="card-header">
              <h3 class="article-title">{{ article.title }}</h3>
              <el-dropdown v-if="!isShared || article.userId === authStore.currentUser?.id" @command="(command: string) => handleArticleCommand(command, article)">
                <el-button type="text" :icon="MoreFilled" @click.stop />
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="edit" v-if="!isShared || article.userId === authStore.currentUser?.id">
                      <el-icon><Edit /></el-icon>
                      编辑
                    </el-dropdown-item>
                    <el-dropdown-item command="delete" v-if="!isShared || article.userId === authStore.currentUser?.id" divided>
                      <el-icon><Delete /></el-icon>
                      删除
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>

            <div class="card-content">
              <p class="article-summary">
                {{ article.summary || article.content.substring(0, 150) + '...' }}
              </p>
            </div>

            <div class="card-footer">
              <div class="article-meta">
                <el-tag
                    v-if="article.categoryName"
                    size="small"
                    type="info"
                >
                  {{ article.categoryName }}
                </el-tag>

                <el-tag
                    v-if="!isShared"
                    size="small"
                    :type="article.status === 1 ? 'success' : 'warning'"
                >
                  {{ article.status === 1 ? '已发布' : '草稿' }}
                </el-tag>

                <el-tag
                    v-if="article.isShared"
                    size="small"
                    type="primary"
                >
                  共享
                </el-tag>
              </div>

              <div class="article-stats">
                <span class="stat-item">
                  <el-icon><View /></el-icon>
                  {{ article.viewCount }}
                </span>
                <span class="stat-item">
                  <el-icon><Star /></el-icon>
                  {{ article.likeCount }}
                </span>
              </div>

              <div class="article-info">
                <span class="author" v-if="isShared">{{ article.username }}</span>
                <span class="date">{{ formatDate(article.updatedAt) }}</span>
              </div>
            </div>

            <!-- 标签 -->
            <div v-if="article.tags && article.tags.length > 0" class="article-tags">
              <el-tag
                  v-for="tag in article.tags"
                  :key="tag"
                  size="small"
                  effect="plain"
              >
                {{ tag }}
              </el-tag>
            </div>
          </div>
        </div>

        <el-empty v-else description="暂无文章" />

        <!-- 分页 -->
        <div v-if="totalCount > 0" class="pagination">
          <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :total="totalCount"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handlePageSizeChange"
              @current-change="handlePageChange"
          />
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch, computed } from 'vue'
import { useRouter } from 'vue-router'
import {
  EditPen,
  MoreFilled,
  Edit,
  Delete,
  View,
  Star
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/store/auth'
import { useCategoryStore } from '@/store/category'
import { articleAPI, type Article, type FilterDTO } from '@/api'
import { ElMessageBox, ElMessage } from 'element-plus'
import dayjs from 'dayjs'

// Props
interface Props {
  isShared?: boolean
  searchKeyword?: string
}

const props = withDefaults(defineProps<Props>(), {
  isShared: false,
  searchKeyword: ''
})

const router = useRouter()
const authStore = useAuthStore()
const categoryStore = useCategoryStore()

// 响应式状态
const loading = ref(false)
const articles = ref<Article[]>([])
const totalCount = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)
const dateRange = ref<[string, string] | null>(null)

// 筛选表单
const filterForm = reactive({
  categoryId: undefined as number | undefined,
  status: undefined as string | undefined,
  keyword: ''
})

// 获取文章列表
const fetchArticles = async () => {
  try {
    loading.value = true

    const params: FilterDTO = {
      page: currentPage.value,
      size: pageSize.value,
      categoryId: filterForm.categoryId,
      status: filterForm.status,
      keyword: filterForm.keyword || undefined,
      startDate: dateRange.value?.[0],
      endDate: dateRange.value?.[1],
      sort: 'updatedAt,desc'
    }

    const response = props.isShared
        ? await articleAPI.getSharedList(params)
        : await articleAPI.getList(params)

    articles.value = response.data.content || []
    totalCount.value = response.data.totalElements || 0
  } catch (error) {
    console.error('获取文章列表失败:', error)
    ElMessage.error('获取文章列表失败')
  } finally {
    loading.value = false
  }
}

// 处理筛选
const handleFilter = () => {
  currentPage.value = 1
  fetchArticles()
}

// 监听搜索关键词变化
watch(() => props.searchKeyword, (newKeyword) => {
  filterForm.keyword = newKeyword
  handleFilter()
}, { immediate: true })

// 重置筛选
const resetFilter = () => {
  filterForm.categoryId = undefined
  filterForm.status = undefined
  filterForm.keyword = ''
  dateRange.value = null
  currentPage.value = 1
  fetchArticles()
}

// 处理分页变化
const handlePageChange = (page: number) => {
  currentPage.value = page
  fetchArticles()
}

const handlePageSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  fetchArticles()
}

// 查看文章
const viewArticle = (id: number) => {
  router.push(`/dashboard/editor/${id}`)
}

// 处理文章命令
const handleArticleCommand = async (command: string, article: Article) => {
  switch (command) {
    case 'edit':
      router.push(`/dashboard/editor/${article.id}`)
      break
    case 'delete':
      await handleDeleteArticle(article)
      break
  }
}

// 删除文章
const handleDeleteArticle = async (article: Article) => {
  try {
    await ElMessageBox.confirm(
        `确定要删除文章"${article.title}"吗？删除后无法恢复。`,
        '删除确认',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
    )

    await articleAPI.delete(article.id)
    ElMessage.success('删除成功')
    fetchArticles()
  } catch (error) {
    // 用户取消删除
  }
}

// 格式化日期
const formatDate = (dateString: string) => {
  return dayjs(dateString).format('YYYY-MM-DD HH:mm')
}

// 组件挂载时获取数据
onMounted(() => {
  fetchArticles()
  if (categoryStore.categories.length === 0) {
    categoryStore.fetchCategoryTree()
  }
})
</script>

<style scoped>
.article-list-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.article-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-left h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #333;
}

.filter-bar {
  background: white;
  padding: 16px;
  border-radius: 6px;
  margin-bottom: 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.article-content {
  flex: 1;
  overflow: hidden;
}

.article-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.article-card {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 20px;
  background: white;
  cursor: pointer;
  transition: all 0.3s;
  height: fit-content;
}

.article-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.article-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  line-height: 1.4;
  flex: 1;
  margin-right: 12px;
}

.card-content {
  margin-bottom: 16px;
}

.article-summary {
  margin: 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-footer {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.article-meta {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.article-stats {
  display: flex;
  gap: 16px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #909399;
}

.article-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #909399;
}

.author {
  font-weight: 500;
}

.article-tags {
  margin-top: 12px;
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.pagination {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .article-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }

  .filter-bar .el-form {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .filter-bar .el-form-item {
    margin: 0;
  }

  .article-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .article-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }
}

@media (max-width: 480px) {
  .article-card {
    padding: 16px;
  }

  .card-header {
    flex-direction: column;
    gap: 8px;
  }

  .article-title {
    margin-right: 0;
  }
}
</style>