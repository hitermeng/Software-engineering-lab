<template>
  <div class="user-profile-container">
    <h2>个人设置</h2>

    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>基本信息</span>
        </div>
      </template>

      <el-form :model="userForm" :rules="rules" ref="userFormRef" label-width="100px">
        <el-form-item label="头像">
          <el-upload
            class="avatar-uploader"
            action="/api/upload" 
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
            :headers="uploadHeaders"
          >
            <template #trigger>
              <el-avatar :size="80" :src="userForm.avatar" class="user-avatar">
                {{ authStore.user?.nickname?.charAt(0) || authStore.user?.username?.charAt(0) }}
              </el-avatar>
            </template>
          </el-upload>
        </el-form-item>

        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="userForm.nickname" placeholder="请输入昵称"></el-input>
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="authStore.loading">保存修改</el-button>
          <el-button @click="router.push('/dashboard')">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch, computed } from 'vue'
import { useAuthStore } from '@/store/auth'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import type { User } from '@/api'
import { useRouter } from 'vue-router'

const authStore = useAuthStore()
const userFormRef = ref<FormInstance>()
const router = useRouter()

const userForm = reactive<Partial<User>>({
  nickname: '',
  email: '',
  avatar: ''
})

// 上传组件的请求头
const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${authStore.token}`
}))

// 头像上传成功回调
const handleAvatarSuccess = (response: any) => {
  // 假设后端返回的数据中，图片 URL 在 response.data.url
  if (response.code === 200 && response.data && response.data.url) {
    userForm.avatar = response.data.url
    ElMessage.success('头像上传成功！')
  } else {
    ElMessage.error(response.message || '头像上传失败，请重试！')
  }
}

// 头像上传前校验
const beforeAvatarUpload = (rawFile: any) => {
  const isJPGPNG = rawFile.type === 'image/jpeg' || rawFile.type === 'image/png'
  const isLt2M = rawFile.size / 1024 / 1024 < 2

  if (!isJPGPNG) {
    ElMessage.error('头像图片只能是 JPG/PNG 格式!')
  }
  if (!isLt2M) {
    ElMessage.error('头像图片大小不能超过 2MB!')
  }
  return isJPGPNG && isLt2M
}

// 复制当前用户信息到表单
const copyUserData = () => {
  if (authStore.user) {
    userForm.nickname = authStore.user.nickname || ''
    userForm.email = authStore.user.email || ''
    userForm.avatar = authStore.user.avatar || ''
  }
}

// 监听 authStore.user 变化，自动更新表单
watch(() => authStore.user, () => {
  copyUserData()
}, { immediate: true })

const rules = reactive<FormRules<typeof userForm>>({
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
})

const handleSubmit = async () => {
  if (!userFormRef.value) return
  await userFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await authStore.updateProfile(userForm)
        ElMessage.success('个人信息更新成功！')
      } catch (error) {
        console.error('更新个人信息失败:', error)
        ElMessage.error('更新失败，请稍后再试')
      }
    } else {
      ElMessage.warning('请填写完整信息')
    }
  })
}

onMounted(() => {
  // 确保在组件挂载时获取最新用户信息
  if (!authStore.user) {
    authStore.fetchProfile()
  }
  copyUserData()
})
</script>

<style scoped>
.user-profile-container {
  padding: 20px;
  max-width: 600px;
  margin: 0 auto;
}

.box-card {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* Simplified avatar uploader styling */
.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
  width: 80px; /* Match avatar size */
  height: 80px; /* Match avatar size */
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.user-avatar {
  position: relative;
  /* Ensure the avatar itself is visually inside the upload area */
  z-index: 1; /* Make sure it's above any overlay */
}

/* Overlay for the plus icon on hover */
.avatar-uploader .el-upload:hover .user-avatar::after {
  content: '+';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.4); /* 半透明遮罩 */
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  color: white;
  border-radius: 50%; /* Make it round like the avatar */
  z-index: 2; /* Make it above the avatar */
}
</style> 