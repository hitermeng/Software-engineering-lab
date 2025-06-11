<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <h1>文章管理系统</h1>
        <p>欢迎使用个人文章管理平台</p>
      </div>

      <el-tabs v-model="activeTab" class="login-tabs">
        <!-- 登录表单 -->
        <el-tab-pane label="登录" name="login">
          <el-form
              ref="loginFormRef"
              :model="loginForm"
              :rules="loginRules"
              class="login-form"
              @submit.prevent="handleLogin"
          >
            <el-form-item prop="username">
              <el-input
                  v-model="loginForm.username"
                  placeholder="用户名"
                  size="large"
                  :prefix-icon="User"
                  clearable
              />
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                  v-model="loginForm.password"
                  type="password"
                  placeholder="密码"
                  size="large"
                  :prefix-icon="Lock"
                  show-password
                  clearable
                  @keyup.enter="handleLogin"
              />
            </el-form-item>

            <el-form-item>
              <el-button
                  type="primary"
                  size="large"
                  :loading="authStore.loading"
                  class="login-button"
                  @click="handleLogin"
              >
                {{ authStore.loading ? '登录中...' : '登录' }}
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 注册表单 -->
        <el-tab-pane label="注册" name="register">
          <el-form
              ref="registerFormRef"
              :model="registerForm"
              :rules="registerRules"
              class="login-form"
              @submit.prevent="handleRegister"
          >
            <el-form-item prop="username">
              <el-input
                  v-model="registerForm.username"
                  placeholder="用户名"
                  size="large"
                  :prefix-icon="User"
                  clearable
              />
            </el-form-item>

            <el-form-item prop="email">
              <el-input
                  v-model="registerForm.email"
                  placeholder="邮箱"
                  size="large"
                  :prefix-icon="Message"
                  clearable
              />
            </el-form-item>

            <el-form-item prop="nickname">
              <el-input
                  v-model="registerForm.nickname"
                  placeholder="昵称（可选）"
                  size="large"
                  :prefix-icon="Avatar"
                  clearable
              />
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                  v-model="registerForm.password"
                  type="password"
                  placeholder="密码"
                  size="large"
                  :prefix-icon="Lock"
                  show-password
                  clearable
              />
            </el-form-item>

            <el-form-item prop="confirmPassword">
              <el-input
                  v-model="registerForm.confirmPassword"
                  type="password"
                  placeholder="确认密码"
                  size="large"
                  :prefix-icon="Lock"
                  show-password
                  clearable
                  @keyup.enter="handleRegister"
              />
            </el-form-item>

            <el-form-item>
              <el-button
                  type="primary"
                  size="large"
                  :loading="authStore.loading"
                  class="login-button"
                  @click="handleRegister"
              >
                <span v-if="authStore.loading">注册中...</span>
                <span v-else>注册</span>
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>

      <div class="login-footer">
        <p>默认管理员账号：admin / admin123456</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { User, Lock, Message, Avatar } from '@element-plus/icons-vue'
import { useAuthStore } from '@/store/auth'
import type { FormInstance, FormRules } from 'element-plus'
import type { LoginDTO, RegisterDTO } from '@/api'

const authStore = useAuthStore()
const activeTab = ref('login')

// 表单引用
const loginFormRef = ref<FormInstance>()
const registerFormRef = ref<FormInstance>()

// 登录表单
const loginForm = reactive<LoginDTO>({
  username: '',
  password: ''
})

// 注册表单
const registerForm = reactive<RegisterDTO & { confirmPassword: string }>({
  username: '',
  email: '',
  nickname: '',
  password: '',
  confirmPassword: ''
})

// 表单验证规则
const loginRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6个字符', trigger: 'blur' }
  ]
}

const registerRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 处理登录
const handleLogin = async () => {
  if (!loginFormRef.value) return

  try {
    const valid = await loginFormRef.value.validate()
    if (valid) {
      await authStore.login(loginForm)
    }
  } catch (error) {
    console.error('登录表单验证失败:', error)
  }
}

// 处理注册
const handleRegister = async () => {
  if (!registerFormRef.value) return

  try {
    const valid = await registerFormRef.value.validate()
    if (valid) {
      const success = await authStore.register(registerForm)
      if (success) {
        activeTab.value = 'login'
        // 清空注册表单
        Object.assign(registerForm, {
          username: '',
          email: '',
          nickname: '',
          password: '',
          confirmPassword: ''
        })
      }
    }
  } catch (error) {
    console.error('注册表单验证失败:', error)
  }
}
</script>

<style scoped>
  .login-container {
    min-height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    padding: 20px;
  }

  .login-card {
    width: 100%;
    max-width: 400px;
    background: white;
    border-radius: 12px;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
    overflow: hidden;
  }

  .login-header {
    padding: 40px 30px 20px;
    text-align: center;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
  }

  .login-header h1 {
    margin: 0 0 10px;
    font-size: 24px;
    font-weight: 600;
  }

  .login-header p {
    margin: 0;
    font-size: 14px;
    opacity: 0.9;
  }

  .login-tabs {
    padding: 30px;
  }

  .login-tabs :deep(.el-tabs__header) {
    margin: 0 0 30px;
  }

  .login-tabs :deep(.el-tabs__nav-wrap) {
    justify-content: center;
  }

  .login-form {
    .el-form-item {
      margin-bottom: 20px;
    }
  }

  .login-button {
    width: 100%;
    height: 44px;
    font-size: 16px;
    font-weight: 500;
  }

  .login-footer {
    padding: 20px 30px;
    text-align: center;
    background: #f8f9fa;
    border-top: 1px solid #e9ecef;
  }

  .login-footer p {
    margin: 0;
    font-size: 12px;
    color: #6c757d;
  }
</style>