<template>
  <div class="editor-container">
    <div class="editor-header">
      <div class="header-left">
        <el-button @click="goBack" :icon="ArrowLeft">返回</el-button>
        <h2>{{ isEdit ? '编辑文章' : '写文章' }}</h2>
      </div>

      <div class="header-right">
        <el-button @click="handleSave('DRAFT')" :loading="saving">
          保存草稿
        </el-button>
        <el-button type="primary" @click="handleSave('PUBLISHED')" :loading="saving">
          {{ isEdit ? '更新文章' : '发布文章' }}
        </el-button>
      </div>
    </div>

    <div class="editor-content" v-loading="loading">
      <el-form
          ref="formRef"
          :model="articleForm"
          :rules="formRules"
          class="article-form"
          label-position="top"
      >
        <!-- 基本信息 -->
        <div class="form-section">
          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item label="文章标题" prop="title">
                <el-input
                    v-model="articleForm.title"
                    placeholder="请输入文章标题"
                    maxlength="100"
                    show-word-limit
                    size="large"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="文章分类" prop="categoryId">
                <CategorySelect v-model="articleForm.categoryId" />
              </el-form-item>
            </el-col>

            <el-col :span="12">
              <el-form-item label="文章标签">
                <el-select
                    v-model="articleForm.tags"
                    multiple
                    filterable
                    allow-create
                    placeholder="请选择或输入标签"
                    style="width: 100%"
                >
                  <el-option
                      v-for="tag in popularTags"
                      :key="tag"
                      :label="tag"
                      :value="tag"
                  />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item label="文章摘要">
                <el-input
                    v-model="articleForm.summary"
                    type="textarea"
                    placeholder="请输入文章摘要（可选，系统会自动生成）"
                    :rows="3"
                    maxlength="500"
                    show-word-limit
                />
                <div class="summary-actions">
                  <el-button
                      size="small"
                      type="primary"
                      text
                      @click="generateSummary"
                      :loading="generatingSummary"
                  >
                    智能生成摘要
                  </el-button>
                </div>
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <!-- 文章内容 -->
        <div class="form-section">
          <el-form-item label="文章内容" prop="content">
            <div class="editor-wrapper">
              <QuillEditor
                  ref="quillEditorRef"
                  v-model:content="articleForm.content"
                  content-type="html"
                  :options="editorOptions"
                  style="min-height: 400px;"
                  @update:content="handleContentChange"
              />
            </div>
          </el-form-item>
        </div>

        <!-- 发布设置 -->
        <div class="form-section">
          <el-form-item label="发布设置">
            <el-checkbox v-model="articleForm.isShared">
              设为共享文章（所有用户可见）
            </el-checkbox>
          </el-form-item>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import { articleAPI, type Article } from '@/api'
import CategorySelect from '@/components/CategorySelect.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { QuillEditor } from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css'

// Props
interface Props {
  id?: string
}

const props = defineProps<Props>()

const router = useRouter()

// 响应式状态
const loading = ref(false)
const saving = ref(false)
const generatingSummary = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const quillEditorRef = ref()
const popularTags = ref<string[]>([])

// 定义表单数据类型（用于UI绑定）
interface ArticleFormData {
  title: string;
  content: string;
  summary: string;
  categoryId?: number;
  tags: string[]; // UI需要数组类型
  status: 'DRAFT' | 'PUBLISHED'; // UI需要字符串类型
  isShared: boolean; // UI需要布尔类型
}

// 定义提交数据类型（用于API请求）
interface ArticleSubmitData {
  title: string;
  content: string;
  summary: string;
  categoryId?: number;
  tags: string; // API需要字符串
  status: number; // API需要数字
  isShared: number; // API需要数字
}

// 文章表单数据
const articleForm = ref<ArticleFormData>({
  title: '',
  content: '',
  summary: '',
  categoryId: undefined,
  tags: [],
  status: 'DRAFT',
  isShared: false
})

// Quill编辑器配置
const editorOptions = {
  theme: 'snow',
  placeholder: '开始写作...',
  modules: {
    toolbar: [
      [{ 'header': [1, 2, 3, 4, 5, 6, false] }],
      ['bold', 'italic', 'underline', 'strike'],
      [{ 'color': [] }, { 'background': [] }],
      [{ 'list': 'ordered'}, { 'list': 'bullet' }],
      [{ 'indent': '-1'}, { 'indent': '+1' }],
      [{ 'align': [] }],
      ['blockquote', 'code-block'],
      ['link', 'image'],
      ['clean']
    ]
  }
}

// 表单验证规则
const formRules: FormRules = {
  title: [
    { required: true, message: '请输入文章标题', trigger: 'blur' },
    { min: 1, max: 100, message: '标题长度在 1 到 100 个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入文章内容', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        const text = quillEditorRef.value?.getText?.() || ''
        if (text.trim().length < 10) {
          callback(new Error('文章内容不能少于10个字符'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 处理内容变化
const handleContentChange = (content: string) => {
  articleForm.value.content = content
}

// 获取文章详情
const fetchArticle = async (id: number) => {
  try {
    loading.value = true
    const response = await articleAPI.getById(id)
    const article: Article = response.data

    articleForm.value = {
      title: article.title,
      content: article.content,
      summary: article.summary || '',
      categoryId: article.categoryId,
      tags: typeof article.tags === 'string' ? article.tags.split(',') : (article.tags || []),
      status: article.status === 0 ? 'DRAFT' : 'PUBLISHED',
      isShared: article.isShared === 1
    }
  } catch (error) {
    console.error('获取文章详情失败:', error)
    ElMessage.error('获取文章详情失败')
  } finally {
    loading.value = false
  }
}

// 获取热门标签
const fetchPopularTags = async () => {
  try {
    const response = await articleAPI.getPopularTags()
    popularTags.value = response.data || []
  } catch (error) {
    console.error('获取热门标签失败:', error)
  }
}

// 生成文章摘要
const generateSummary = async () => {
  if (!articleForm.value.content || articleForm.value.content.length < 50) {
    ElMessage.warning('请先输入足够的文章内容')
    return
  }

  try {
    generatingSummary.value = true
    const plainText = quillEditorRef.value?.getText?.() || ''
    const response = await articleAPI.generateSummary(plainText)
    articleForm.value.summary = response.data
    ElMessage.success('摘要生成成功')
  } catch (error) {
    console.error('生成摘要失败:', error)
    ElMessage.error('生成摘要失败，请手动输入')
  } finally {
    generatingSummary.value = false
  }
}

// 保存文章
const handleSave = async (status: 'DRAFT' | 'PUBLISHED') => {
  if (!formRef.value) return

  try {
    // 验证表单
    const valid = await formRef.value.validate()
    if (!valid) return

    saving.value = true

    const saveData: ArticleSubmitData = {
      title: articleForm.value.title,
      summary: articleForm.value.summary,
      categoryId: articleForm.value.categoryId,
      tags: articleForm.value.tags.join(','),
      status: status === 'DRAFT' ? 0 : 1,
      isShared: articleForm.value.isShared ? 1 : 0,
      content: articleForm.value.content
    }

    if (isEdit.value && props.id) {
      await articleAPI.update(parseInt(props.id), saveData)
      ElMessage.success(status === 'PUBLISHED' ? '文章更新成功' : '草稿保存成功')
    } else {
      await articleAPI.create(saveData)
      ElMessage.success(status === 'PUBLISHED' ? '文章发布成功' : '草稿保存成功')
    }

    // 返回文章列表
    router.push('/dashboard')
  } catch (error) {
    console.error('保存文章失败:', error)
    ElMessage.error('保存失败，请重试')
  } finally {
    saving.value = false
  }
}

// 返回上一页
const goBack = async () => {
  const hasChanges = articleForm.value.title ||
      articleForm.value.content ||
      articleForm.value.summary ||
      (articleForm.value.tags && articleForm.value.tags.length > 0)

  if (hasChanges) {
    try {
      await ElMessageBox.confirm(
          '当前有未保存的内容，确定要离开吗？',
          '提示',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
      )
    } catch (error) {
      return // 用户取消
    }
  }

  router.go(-1)
}

// 监听路由参数变化
watch(() => props.id, (newId) => {
  if (newId) {
    isEdit.value = true
    fetchArticle(parseInt(newId))
  } else {
    isEdit.value = false
  }
}, { immediate: true })

// 组件挂载
onMounted(() => {
  fetchPopularTags()
})
</script>

<style scoped>
.editor-container {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: white;
  border-radius: 8px;
  overflow: hidden;
}

.editor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-bottom: 1px solid #e4e7ed;
  background: #f8f9fa;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-left h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.header-right {
  display: flex;
  gap: 12px;
}

.editor-content {
  flex: 1;
  padding: 24px;
  overflow: auto;
}

.article-form {
  max-width: 1000px;
  margin: 0 auto;
}

.form-section {
  margin-bottom: 32px;
  padding: 24px;
  background: #fafbfc;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
}

.form-section:last-child {
  margin-bottom: 0;
}

.editor-wrapper {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
}

.editor-wrapper :deep(.ql-editor) {
  min-height: 400px;
  font-size: 14px;
  line-height: 1.6;
}

.editor-wrapper :deep(.ql-toolbar) {
  border-bottom: 1px solid #e4e7ed;
  background: #f8f9fa;
}

.summary-actions {
  margin-top: 8px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .editor-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }

  .header-left {
    justify-content: space-between;
  }

  .header-right {
    justify-content: flex-end;
  }

  .editor-content {
    padding: 16px;
  }

  .form-section {
    padding: 16px;
  }

  .quill-editor {
    min-height: 300px;
  }

  .editor-wrapper :deep(.ql-editor) {
    min-height: 300px;
  }
}
</style>