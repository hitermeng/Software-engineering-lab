<template>
  <div class="dashboard-container">
    <!-- 顶部导航栏 -->
    <header class="dashboard-header">
      <div class="header-left">
        <h1 class="logo">📝 文章管理系统</h1>
      </div>
      <div class="header-right">
        <div class="search-box">
          <el-input
              v-model="searchKeyword"
              placeholder="搜索文章..."
              clearable
              @keyup.enter="goToSearch"
              @clear="clearSearch"
          >
            <template #append>
              <el-button :icon="Search" @click="goToSearch" />
            </template>
          </el-input>
        </div>

        <el-dropdown @command="handleCommand">
          <span class="el-dropdown-link">
            <el-avatar :size="32" :src="userAvatar">
              {{ authStore.user?.nickname?.charAt(0) || authStore.user?.username?.charAt(0) }}
            </el-avatar>
            <span class="username">{{ authStore.user?.nickname || authStore.user?.username }}</span>
            <el-icon class="el-icon--right">
              <arrow-down />
            </el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">个人设置</el-dropdown-item>
              <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </header>

    <!-- 主体内容区域 -->
    <div class="dashboard-main">
      <!-- 侧边栏 -->
      <aside class="dashboard-sidebar">
        <el-menu
          :default-active="currentMenu"
          class="sidebar-menu"
          @select="handleMenuSelect"
        >
          <el-menu-item index="dashboard">
            <el-icon><House /></el-icon>
            <span>仪表盘</span>
          </el-menu-item>
          <el-menu-item index="articles">
            <el-icon><Document /></el-icon>
            <span>文章管理</span>
          </el-menu-item>
          <el-menu-item index="categories">
            <el-icon><Folder /></el-icon>
            <span>分类管理</span>
          </el-menu-item>
          <el-menu-item index="editor">
            <el-icon><EditPen /></el-icon>
            <span>写文章</span>
          </el-menu-item>
          <el-menu-item index="permission">
            <el-icon><Setting /></el-icon>
            <span>权限管理</span>
          </el-menu-item>
        </el-menu>
      </aside>

      <!-- 内容区域 -->
      <main class="dashboard-content">
        <!-- 当前显示仪表盘概览 -->
        <div v-if="currentMenu === 'dashboard'" class="dashboard-overview">
          <div class="page-header">
            <h2>仪表盘</h2>
            <p>欢迎回来，{{ authStore.user?.nickname || authStore.user?.username }}！</p>
          </div>

          <!-- 统计卡片 -->
          <div class="stats-grid">
            <div class="stat-card">
              <div class="stat-icon">
                <el-icon size="24"><Document /></el-icon>
              </div>
              <div class="stat-content">
                <h3>{{ stats.totalArticles }}</h3>
                <p>总文章数</p>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon">
                <el-icon size="24"><Folder /></el-icon>
              </div>
              <div class="stat-content">
                <h3>{{ stats.totalCategories }}</h3>
                <p>分类数量</p>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon">
                <el-icon size="24"><View /></el-icon>
              </div>
              <div class="stat-content">
                <h3>{{ stats.totalViews }}</h3>
                <p>总阅读量</p>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon">
                <el-icon size="24"><Star /></el-icon>
              </div>
              <div class="stat-content">
                <h3>{{ stats.publishedArticles }}</h3>
                <p>已发布</p>
              </div>
            </div>
          </div>

          <!-- 搜索卡片 -->
          <div class="search-card">
            <h3>快速搜索</h3>
            <div class="search-options">
              <el-input
                  v-model="quickSearchKeyword"
                  placeholder="输入关键词..."
                  clearable
                  @keyup.enter="quickSearch"
                  class="quick-search-input"
              >
                <template #append>
                  <el-button type="primary" @click="quickSearch">搜索</el-button>
                </template>
              </el-input>

              <div class="search-tips">
                <span>试试搜索：</span>
                <el-tag
                    v-for="(tag, index) in searchSuggestions"
                    :key="index"
                    effect="plain"
                    @click="useSuggestion(tag)"
                >
                  {{ tag }}
                </el-tag>
              </div>

              <el-button
                  type="text"
                  @click="goToAdvancedSearch"
                  class="advanced-search-btn"
              >
                <el-icon><Search /></el-icon>
                进入高级搜索
              </el-button>
            </div>
            <div class="hot-tags">
              <span>热门搜索：</span>
              <el-tag
                  v-for="tag in hotTags"
                  :key="tag"
                  type="info"
                  effect="plain"
                  @click="useSuggestion(tag)"
                  class="hot-tag"
              >
                {{ tag }}
              </el-tag>
            </div>
          </div>

          <!-- 快速操作 -->
          <div class="quick-actions">
            <h3>快速操作</h3>
            <div class="action-buttons">
              <el-button type="primary" :icon="EditPen" @click="goToEditor">
                写新文章
              </el-button>
              <el-button type="success" :icon="Document" @click="goToArticles">
                管理文章
              </el-button>
              <el-button type="warning" :icon="Folder" @click="goToCategories">
                管理分类
              </el-button>
            </div>
          </div>

          <!-- 最近文章 -->
          <div class="recent-articles">
            <h3>最近文章</h3>
            <el-table :data="recentArticles" stripe>
              <el-table-column prop="title" label="标题" min-width="200" />
              <el-table-column prop="category" label="分类" width="120" />
              <el-table-column prop="status" label="状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="row.status === '已发布' ? 'success' : 'warning'">
                    {{ row.status }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createdAt" label="创建时间" width="180" />
              <el-table-column label="操作" width="120">
                <template #default="{ row }">
                  <el-button type="primary" size="small" @click="editArticle(row.id)">
                    编辑
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>

        <!-- 其他菜单的占位内容 -->
        <div v-else class="coming-soon">
          <el-empty description="此功能正在开发中...">
            <el-button type="primary" @click="currentMenu = 'dashboard'">
              返回仪表盘
            </el-button>
          </el-empty>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/store/auth'
import { ElMessage, ElMessageBox } from 'element-plus'
import { articleAPI, searchAPI, ArticleVO } from '@/api'
import {
  ArrowDown,
  House,
  Document,
  Folder,
  EditPen,
  View,
  Star,
  Share,
  Setting,
  Search
} from '@element-plus/icons-vue'

const router = useRouter()
const authStore = useAuthStore()

// 搜索关键词
const searchKeyword = ref('')
const quickSearchKeyword = ref('')
const searchSuggestions = ref<string[]>(['最新文章', '技术分享', '产品更新', '使用教程'])
const hotTags = ref<string[]>([]) // 热门标签

// 获取热门搜索标签
const fetchHotTags = async () => {
  try {
    const response = await searchAPI.getHotSearchTags()
    hotTags.value = response.data
  } catch (error) {
    console.error('获取热门标签失败:', error)
  }
}

// 跳转到搜索页面
const goToSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({
      path: '/search',
      query: { q: searchKeyword.value.trim() }
    }).catch(err => {
      console.log('导航错误:', err)
      ElMessage.error('导航失败: ' + err.message)
    })
  } else {
    ElMessage.warning('请输入搜索关键词')
  }
}

// 清空搜索
const clearSearch = () => {
  searchKeyword.value = ''
}

// 快速搜索
const quickSearch = () => {
  if (quickSearchKeyword.value.trim()) {
    router.push({
      path: '/search',
      query: { q: quickSearchKeyword.value.trim() }
    }).catch(err => {
      console.log('导航错误:', err)
      ElMessage.error('导航失败: ' + err.message)
    })
  } else {
    ElMessage.warning('请输入搜索关键词')
  }
}

// 使用搜索建议
const useSuggestion = (suggestion: string) => {
  quickSearchKeyword.value = suggestion
  quickSearch()
}

// 进入高级搜索
const goToAdvancedSearch = () => {
  router.push({
    path: '/search', // 使用路径而不是名称
    query: { advanced: 'true' }
  }).catch(err => {
    console.log('导航错误:', err)
    ElMessage.error('导航失败: ' + err.message)
  })
}

// 当前选中的菜单
const currentMenu = ref('dashboard')

// 用户头像，改为计算属性
const userAvatar = computed(() => {
  return authStore.user?.avatar || '/default-avatar.png' // 提供一个默认头像路径，防止为空
})

// 统计数据
const stats = reactive({
  totalArticles: 0,
  totalCategories: 0,
  totalViews: 0,
  publishedArticles: 0
})

// 最近文章数据
const recentArticles = ref([
  {
    id: 1,
    title: '欢迎使用文章管理系统',
    category: '系统公告',
    status: '已发布',
    createdAt: '2024-03-15 10:30:00'
  },
  {
    id: 2,
    title: '如何开始写作',
    category: '使用指南',
    status: '草稿',
    createdAt: '2024-03-14 16:20:00'
  }
])

// 处理用户下拉菜单
const handleCommand = async (command: string) => {
  console.log('handleCommand 被调用，命令:', command);
  switch (command) {
    case 'profile':
      console.log('正在跳转到 /profile 页面...');
      router.push('/profile')
      break
    case 'logout':
      try {
        await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        await authStore.logout()
        router.push('/login')
      } catch (error) {
        // 用户取消操作
      }
      break
  }
}

// 处理菜单选择
const handleMenuSelect = (index: string) => {
  currentMenu.value = index
  switch (index) {
    case 'dashboard':
      router.push('/dashboard')
      break
    case 'articles':
      router.push('/articles')
      break
    case 'categories':
      router.push('/categories')
      break
    case 'editor':
      router.push('/editor')
      break
    case 'permission':
      router.push('/permission')
      break
  }
}

// 快速操作方法
const goToEditor = () => {
  router.push('/editor')
}

const goToArticles = () => {
  router.push('/articles')
}

const goToCategories = () => {
  router.push('/categories')
}

const editArticle = (id: number) => {
  router.push(`/editor/${id}`)
}

// 获取文章统计数据
const fetchArticleStatistics = async () => {
  try {
    const response = await articleAPI.getStatistics() // 假设后端有根据用户ID获取统计数据，或不需要用户ID
    stats.totalArticles = response.data.totalArticles
    stats.totalCategories = response.data.totalCategories
    stats.totalViews = response.data.totalViews
    stats.publishedArticles = response.data.publishedArticles
  } catch (error) {
    console.error('获取文章统计数据失败:', error)
  }
}

// 获取最近文章
const fetchRecentArticles = async () => {
  try {
    const response = await articleAPI.getList({
      page: 1,
      size: 5, // 获取最近的5篇文章
      sort: 'create_time,desc' // 按创建时间降序排序
    })
    // recentArticles.value = response.data
    // 转换数据格式以匹配前端
    recentArticles.value = response.data.records.map((article: ArticleVO) => ({
      id: article.id,
      title: article.title,
      category: article.categoryName || '未分类',
      status: article.status === 1 ? '已发布' : '草稿',
      createdAt: formatDate(article.createTime) // 格式化日期
    }))
  } catch (error) {
    console.error('获取最近文章失败:', error)
  }
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

onMounted(() => {
  authStore.fetchProfile()
  fetchArticleStatistics()
  fetchRecentArticles()
  fetchHotTags() // 获取热门标签
})
</script>

<style scoped>
.dashboard-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;
}

/* 头部导航 */
.dashboard-header {
  height: 60px;
  background: white;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header-left .logo {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.header-right {
  display: flex;
  align-items: center;
}

.el-dropdown-link {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #606266;
  font-size: 14px;
}

.username {
  margin: 0 8px;
}

/* 主体区域 */
.dashboard-main {
  flex: 1;
  display: flex;
  overflow: hidden;
}

/* 侧边栏 */
.dashboard-sidebar {
  width: 200px;
  background: white;
  border-right: 1px solid #e4e7ed;
}

.sidebar-menu {
  border-right: none;
  height: 100%;
}

.sidebar-menu .el-menu-item {
  height: 50px;
  line-height: 50px;
}

/* 内容区域 */
.dashboard-content {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h2 {
  margin: 0 0 8px;
  color: #303133;
  font-size: 24px;
  font-weight: 600;
}

.page-header p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

/* 统计卡片 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 16px;
  margin-bottom: 32px;
}

.stat-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  background: #f0f9ff;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  color: #409eff;
}

.stat-content h3 {
  margin: 0 0 4px;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.stat-content p {
  margin: 0;
  font-size: 14px;
  color: #909399;
}

/* 快速操作 */
.quick-actions {
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.quick-actions h3 {
  margin: 0 0 16px;
  color: #303133;
  font-size: 16px;
  font-weight: 600;
}

.action-buttons {
  display: flex;
  gap: 12px;
}

/* 最近文章 */
.recent-articles {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.recent-articles h3 {
  margin: 0 0 16px;
  color: #303133;
  font-size: 16px;
  font-weight: 600;
}

/* 占位内容 */
.coming-soon {
  background: white;
  border-radius: 8px;
  padding: 40px;
  text-align: center;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .dashboard-sidebar {
    width: 180px;
  }
  
  .dashboard-content {
    padding: 16px;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .action-buttons {
    flex-direction: column;
  }
}

.search-container {
  margin-right: 20px;
  width: 400px;
}

.search-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin: 20px 0;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.search-actions {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.hot-tags {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  margin-top: 10px;
}

.hot-tag {
  cursor: pointer;
  transition: all 0.2s;
}

.hot-tag:hover {
  background-color: #f5f7fa;
  transform: translateY(-2px);
}

.suggestion-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 12px;
}

</style>