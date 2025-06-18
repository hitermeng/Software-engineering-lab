<template>
  <div class="category-tree-container">
    <!-- 页面头部 -->
    <div class="category-header">
      <div class="header-left">
        <el-button :icon="ArrowLeft" @click="goBackToDashboard">返回</el-button>
        <h2>分类管理</h2>
        <p>管理文章分类，支持多层级结构</p>
      </div>
      <div class="header-actions">
        <el-button 
          type="primary" 
          :icon="Plus" 
          @click="showAddDialog()"
        >
          新建分类
        </el-button>
        <el-button 
          v-if="selectedCategories.length > 0"
          type="danger" 
          :icon="Delete"
          @click="handleBatchDelete"
        >
          批量删除 ({{ selectedCategories.length }})
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-icon">🗂️</div>
        <div class="stat-content">
          <div class="stat-number">{{ totalCategories }}</div>
          <div class="stat-label">总分类数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">📊</div>
        <div class="stat-content">
          <div class="stat-number">{{ maxLevel }}</div>
          <div class="stat-label">最大层级</div>
        </div>
      </div>
<!--      <div class="stat-card">-->
<!--        <div class="stat-icon">📝</div>-->
<!--        <div class="stat-content">-->
<!--          <div class="stat-number">{{ totalArticles }}</div>-->
<!--          <div class="stat-label">关联文章</div>-->
<!--        </div>-->
<!--      </div>-->
    </div>

    <!-- 分类树表格 -->
    <div class="category-content">
      <el-card v-loading="categoryStore.loading">
        <div class="table-header">
          <div class="table-title">分类列表</div>
          <div class="table-actions">
            <el-button 
              size="small" 
              :icon="Refresh" 
              @click="refreshCategories"
              :loading="categoryStore.loading"
            >
              刷新
            </el-button>
            <el-button 
              size="small" 
              :icon="expandAll ? Fold : Expand"
              @click="toggleExpandAll"
            >
              {{ expandAll ? '全部收起' : '全部展开' }}
            </el-button>
          </div>
        </div>

        <el-table
          ref="treeRef"
          v-loading="categoryStore.loading"
          :data="categoryStore.categories"
          row-key="id"
          :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
          :default-expand-all="expandAll"
          @selection-change="handleSelectionChange"
          @drag-end="handleDragEnd"
          class="category-tree"
        >
          <el-table-column type="selection" width="55" />
          
          <el-table-column label="分类名称" min-width="200">
            <template #default="{ row }">
              <div class="node-content">
                <div 
                  class="category-color" 
                  :style="{ backgroundColor: row.color || '#409eff' }"
                ></div>
                <el-icon class="node-icon">
                  <Folder v-if="row.children && row.children.length > 0" />
                  <Document v-else />
                </el-icon>
                <span class="node-label">{{ row.name }}</span>
                <span class="node-description" v-if="row.description">
                  ({{ row.description }})
                </span>
                <el-tag v-if="!row.isActive" type="danger" size="small">已禁用</el-tag>
              </div>
            </template>
          </el-table-column>
          
<!--          <el-table-column label="文章数量" width="100" align="center">-->
<!--            <template #default="{ row }">-->
<!--              <el-badge :value="row.articleCount" :max="99" class="article-count">-->
<!--                <el-icon><Document /></el-icon>-->
<!--              </el-badge>-->
<!--            </template>-->
<!--          </el-table-column>-->
          
          <el-table-column label="排序" width="80" align="center">
            <template #default="{ row }">
              <span class="sort-number">{{ row.sort }}</span>
            </template>
          </el-table-column>
          
          <el-table-column label="状态" width="80" align="center">
            <template #default="{ row }">
              <el-switch
                v-model="row.isActive"
                @change="handleStatusChange(row)"
                :loading="categoryStore.loading"
              />
            </template>
          </el-table-column>
          
          <el-table-column label="创建时间" width="120">
            <template #default="{ row }">
              <span class="create-time">{{ formatDate(row.createTime) }}</span>
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <div class="node-actions">
                <el-button 
                  size="small" 
                  type="primary" 
                  :icon="Plus"
                  @click="showAddDialog(row)"
                  v-if="row.level < 3"
                >
                  添加子分类
                </el-button>
                <el-button 
                  size="small" 
                  :icon="Edit"
                  @click="showEditDialog(row)"
                >
                  编辑
                </el-button>
                <el-button 
                  size="small" 
                  type="danger" 
                  :icon="Delete"
                  @click="handleDelete(row)"
                  :disabled="row.articleCount > 0"
                >
                  删除
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <el-empty v-if="categoryStore.categories.length === 0" description="暂无分类数据" />
      </el-card>
    </div>

    <!-- 添加/编辑分类对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      @close="resetForm"
    >
      <el-form
        ref="formRef"
        :model="categoryForm"
        :rules="formRules"
        label-width="80px"
      >
        <el-form-item label="分类名称" prop="name">
          <el-input
            v-model="categoryForm.name"
            placeholder="请输入分类名称"
            maxlength="50"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="分类描述" prop="description">
          <el-input
            v-model="categoryForm.description"
            type="textarea"
            placeholder="请输入分类描述（可选）"
            maxlength="200"
            show-word-limit
            :rows="3"
          />
        </el-form-item>

        <el-form-item label="父级分类" prop="parentId">
          <el-tree-select
            v-model="categoryForm.parentId"
            :data="parentCategoryOptions"
            :props="{ label: 'name', value: 'id' }"
            placeholder="请选择父级分类（可选）"
            clearable
            check-strictly
            :render-after-expand="false"
          />
        </el-form-item>

        <el-form-item label="颜色" prop="color">
          <el-color-picker 
            v-model="categoryForm.color" 
            :predefine="predefineColors"
          />
        </el-form-item>
        
        <el-form-item label="图标" prop="icon">
          <el-select 
            v-model="categoryForm.icon" 
            placeholder="选择图标（可选）"
            clearable
            style="width: 100%"
          >
            <el-option 
              v-for="icon in iconOptions" 
              :key="icon.value"
              :label="icon.label"
              :value="icon.value"
            >
              <el-icon :class="icon.value" style="margin-right: 8px" />
              {{ icon.label }}
            </el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="排序" prop="sort">
          <el-input-number 
            v-model="categoryForm.sort" 
            :min="0"
            :max="999"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          {{ isEdit ? '更新' : '创建' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useCategoryStore } from '@/store/category'
import { 
  Plus, 
  Edit, 
  Delete, 
  Refresh, 
  Expand, 
  Fold,
  Document,
  Folder,
  ArrowLeft
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import type { Category } from '@/api'

const categoryStore = useCategoryStore()
const router = useRouter()

// 响应式状态
const treeRef = ref()
const formRef = ref<FormInstance>()
const dialogVisible = ref(false)
const submitting = ref(false)
const isEdit = ref(false)
const currentCategory = ref<Category | null>(null)
const expandAll = ref(false)
const selectedCategories = ref<Category[]>([])

// 表单数据
const categoryForm = ref({
  name: '',
  description: '',
  parentId: undefined as number | undefined,
  color: '#409eff',
  icon: '',
  sort: 0
})

// 表单验证规则
const formRules: FormRules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { min: 1, max: 50, message: '分类名称长度在 1 到 50 个字符', trigger: 'blur' }
  ]
}

// 预定义颜色
const predefineColors = [
  '#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#909399',
  '#c71585', '#ff69b4', '#ba55d3', '#9370db', '#8a2be2',
  '#4169e1', '#1e90ff', '#00bfff', '#87ceeb', '#87cefa'
]

// 图标选项
const iconOptions = [
  { label: '文件夹', value: 'Folder' },
  { label: '文档', value: 'Document' },
  { label: '标签', value: 'Collection' },
  { label: '星星', value: 'Star' },
  { label: '设置', value: 'Setting' },
  { label: '用户', value: 'User' },
  { label: '时间', value: 'Clock' },
  { label: '位置', value: 'Location' }
]

// 计算属性
const dialogTitle = computed(() => {
  return isEdit.value ? '编辑分类' : '添加分类'
})

const totalCategories = computed(() => {
  const countCategories = (cats: Category[]): number => {
    return cats.reduce((count, cat) => {
      return count + 1 + (cat.children ? countCategories(cat.children) : 0)
    }, 0)
  }
  return countCategories(categoryStore.categories)
})

const maxLevel = computed(() => {
  const getMaxLevel = (cats: Category[], currentLevel = 1): number => {
    return cats.reduce((maxLvl, cat) => {
      const childLevel = cat.children?.length 
        ? getMaxLevel(cat.children, currentLevel + 1) 
        : currentLevel
      return Math.max(maxLvl, childLevel)
    }, currentLevel)
  }
  return categoryStore.categories.length ? getMaxLevel(categoryStore.categories) : 0
})

// const totalArticles = computed(() => {
//   const countArticles = (cats: Category[]): number => {
//     return cats.reduce((count, cat) => {
//       return count + (cat.articleCount || 0) + (cat.children ? countArticles(cat.children) : 0)
//     }, 0)
//   }
//   return countArticles(categoryStore.categories)
// })

// 获取父级分类选项（排除当前编辑的分类及其子类）
const parentCategoryOptions = computed(() => {
  const filterCategory = (categories: Category[], excludeId?: number): Category[] => {
    return categories.filter(cat => cat.id !== excludeId).map(cat => ({
      ...cat,
      children: cat.children ? filterCategory(cat.children, excludeId) : []
    }))
  }

  return filterCategory(categoryStore.categories, currentCategory.value?.id)
})

// 方法
const refreshCategories = async () => {
  await categoryStore.fetchCategoryTree()
}

const toggleExpandAll = () => {
  expandAll.value = !expandAll.value
  if (treeRef.value) {
    if (expandAll.value) {
      treeRef.value.expandAll()
    } else {
      treeRef.value.collapseAll()
    }
  }
}

const handleSelectionChange = (selection: Category[]) => {
  selectedCategories.value = selection
}

// 显示添加对话框
const showAddDialog = (parentCategory?: Category) => {
  isEdit.value = false
  currentCategory.value = null
  categoryForm.value = {
    name: '',
    description: '',
    parentId: parentCategory?.id,
    color: '#409eff',
    icon: '',
    sort: 0
  }
  dialogVisible.value = true
}

// 显示编辑对话框
const showEditDialog = (category: Category) => {
  isEdit.value = true
  currentCategory.value = category
  categoryForm.value = {
    name: category.name,
    description: category.description || '',
    parentId: category.parentId,
    color: category.color || '#409eff',
    icon: category.icon || '',
    sort: category.sort || 0
  }
  dialogVisible.value = true
}

// 处理表单提交
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    const valid = await formRef.value.validate()
    if (valid) {
      submitting.value = true

      const categoryData = {
        name: categoryForm.value.name,
        description: categoryForm.value.description || undefined,
        parentId: categoryForm.value.parentId || undefined,
        color: categoryForm.value.color,
        icon: categoryForm.value.icon,
        sort: categoryForm.value.sort
      }

      if (isEdit.value && currentCategory.value) {
        await categoryStore.updateCategory({
          id: currentCategory.value.id,
          ...categoryData
        })
      } else {
        await categoryStore.createCategory(categoryData)
      }

      dialogVisible.value = false
      resetForm()
    }
  } catch (error) {
    console.error('提交分类失败:', error)
  } finally {
    submitting.value = false
  }
}

// 状态切换
const handleStatusChange = async (category: Category) => {
  try {
    await categoryStore.updateCategory({
      id: category.id,
      isActive: category.isActive
    })
  } catch (error) {
    // 回滚状态
    category.isActive = !category.isActive
  }
}

// 处理删除
const handleDelete = async (category: Category) => {
  if (category.articleCount && category.articleCount > 0) {
    ElMessage.warning('该分类下还有文章，无法删除')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要删除分类"${category.name}"吗？删除后该分类下的所有子分类也会被删除。`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await categoryStore.deleteCategory(category.id)
  } catch (error) {
    // 用户取消删除
  }
}

// 批量删除
const handleBatchDelete = async () => {
  const hasArticles = selectedCategories.value.some(cat => cat.articleCount && cat.articleCount > 0)
  if (hasArticles) {
    ElMessage.warning('选中的分类中有包含文章的分类，无法删除')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedCategories.value.length} 个分类吗？此操作不可撤销。`,
      '确认批量删除',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
        confirmButtonClass: 'el-button--danger'
      }
    )
    
    const ids = selectedCategories.value.map(cat => cat.id)
    await categoryStore.batchDeleteCategories(ids)
    selectedCategories.value = []
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
    }
  }
}

// 格式化日期
const formatDate = (date: string) => {
  return new Date(date).toLocaleDateString('zh-CN')
}

// 重置表单
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  categoryForm.value = {
    name: '',
    description: '',
    parentId: undefined,
    color: '#409eff',
    icon: '',
    sort: 0
  }
  currentCategory.value = null
  isEdit.value = false
}

// 添加拖拽排序功能
const handleDragEnd = async (evt: any) => {
  // 实现拖拽排序逻辑
}

// 返回仪表盘
const goBackToDashboard = () => {
  router.push('/dashboard')
}

// 组件挂载时获取分类数据
onMounted(() => {
  if (categoryStore.categories.length === 0) {
    categoryStore.fetchCategoryTree()
  }
})
</script>

<style scoped>
.category-tree-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  padding: 24px;
  background: #f5f7fa;
  overflow: hidden;
}

.category-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.header-left h2 {
  margin: 0 0 8px;
  font-size: 24px;
  color: #1f2937;
}

.header-left p {
  margin: 0;
  color: #6b7280;
  font-size: 14px;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  padding: 20px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: transform 0.2s, box-shadow 0.2s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  font-size: 32px;
  width: 56px;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  color: white;
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 24px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #6b7280;
}

.category-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0; /* 添加最小高度保证 */
}

.category-content .el-card {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;
}

.category-content .el-card__body {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;
  padding: 0 !important;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #e5e7eb;
  flex-shrink: 0;
}

.table-title {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
}

.table-actions {
  display: flex;
  gap: 8px;
}

.category-tree {
  width: 100%;
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-height: 0; /* 添加最小高度 */
}

.category-tree :deep(.el-table) {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 100% !important; /* 关键：强制高度100% */
}

.category-tree :deep(.el-table__body-wrapper) {
  flex: 1;
  overflow: auto !important; /* 强制启用滚动 */
  -webkit-overflow-scrolling: touch; /* 启用平滑滚动 */
}

.category-tree :deep(.el-table__body) {
  min-height: 100%; /* 确保内容足够滚动 */
}

.category-tree :deep(.el-table__header-wrapper) {
  position: sticky;
  top: 0;
  z-index: 3;
  background: white;
}

.category-tree :deep(.el-table__fixed-header-wrapper) {
  top: 0;
}

.el-empty {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.node-content {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.category-color {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  border: 2px solid white;
  box-shadow: 0 0 0 1px rgba(0, 0, 0, 0.1);
}

.node-icon {
  color: #606266;
  font-size: 16px;
}

.node-label {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

.node-description {
  font-size: 12px;
  color: #909399;
  margin-left: 8px;
}

.node-actions {
  display: flex;
  gap: 4px;
}

.node-actions .el-button {
  padding: 4px;
  min-height: auto;
}

.article-count :deep(.el-badge__content) {
  background-color: #10b981;
}

.sort-number {
  color: #6b7280;
  font-weight: 500;
}

.create-time {
  color: #6b7280;
  font-size: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .category-tree-container {
    padding: 16px;
  }

  .category-content {
    min-height: 300px; /* 移动端最小高度 */
  }

  .category-tree :deep(.el-table__body-wrapper) {
    max-height: calc(100vh - 400px); /* 移动端高度调整 */
  }

  .category-header {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }
  
  .header-actions {
    width: 100%;
    justify-content: center;
  }
  
  .stats-row {
    grid-template-columns: 1fr;
  }
  
  .node-actions {
    opacity: 1;
  }
  
  .node-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .node-actions {
    align-self: flex-end;
  }
}

/* 深度选择器修复 */
.category-tree :deep(.el-table__expand-column .el-table__expand-icon) {
  color: #409eff;
}

.category-tree :deep(.el-table__placeholder) {
  color: #c0c4cc;
}
</style>