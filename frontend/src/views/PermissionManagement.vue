<template>
  <div class="permission-management-container">
    <h2>权限管理</h2>

    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>修改用户权限</span>
        </div>
      </template>

      <el-form :model="permissionForm" :rules="rules" ref="permissionFormRef" label-width="120px">
        <el-form-item label="管理员密码" prop="adminPassword">
          <el-input v-model="permissionForm.adminPassword" type="password" show-password></el-input>
        </el-form-item>

        <el-form-item label="目标用户账号" prop="targetUsername">
          <el-input v-model="permissionForm.targetUsername"></el-input>
        </el-form-item>

        <el-form-item label="选择新角色" prop="newRole">
          <el-select v-model="permissionForm.newRole" placeholder="请选择角色">
            <el-option label="普通用户" value="USER"></el-option>
            <el-option label="管理员" value="ADMIN"></el-option>
            <el-option label="禁用" value="DISABLED"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit">提交更改</el-button>
          <el-button @click="router.push('/dashboard')">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { userAPI } from '@/api'
import { useRouter } from 'vue-router'

interface PermissionForm {
  adminPassword: string
  targetUsername: string
  newRole: 'USER' | 'ADMIN' | 'DISABLED'
}

const permissionFormRef = ref<FormInstance>()
const permissionForm = reactive<PermissionForm>({
  adminPassword: '',
  targetUsername: '',
  newRole: 'USER'
})

const rules = reactive<FormRules<PermissionForm>>({
  adminPassword: [
    { required: true, message: '请输入管理员密码', trigger: 'blur' }
  ],
  targetUsername: [
    { required: true, message: '请输入目标用户账号', trigger: 'blur' }
  ],
  newRole: [
    { required: true, message: '请选择新角色', trigger: 'change' }
  ]
})

const router = useRouter()

const handleSubmit = async () => {
  if (!permissionFormRef.value) return
  await permissionFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const roleText = permissionForm.newRole === 'DISABLED' ? '禁用' : 
                        permissionForm.newRole === 'ADMIN' ? '管理员' : '普通用户'
        
        await ElMessageBox.confirm(
          `确定要将用户 "${permissionForm.targetUsername}" 的权限修改为 "${roleText}" 吗？`,
          '权限修改确认',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )

        if (permissionForm.newRole === 'DISABLED') {
          // 更新用户状态为禁用
          await userAPI.updateUserStatus(
            permissionForm.targetUsername,
            0,
            permissionForm.adminPassword
          )
        } else {
          // 更新用户角色
          await userAPI.updateUserRole({
            adminPassword: permissionForm.adminPassword,
            username: permissionForm.targetUsername,
            role: permissionForm.newRole
          })
          
          // 确保用户状态为启用
          await userAPI.updateUserStatus(
            permissionForm.targetUsername,
            1,
            permissionForm.adminPassword
          )
        }

        ElMessage.success('权限修改成功！')
        // 清空表单
        if (permissionFormRef.value) {
          permissionFormRef.value.resetFields()
        }
      } catch (error: any) {
        if (error !== 'cancel') {
          console.error('权限修改失败:', error)
          ElMessage.error(error.response?.data?.message || '权限修改失败，请检查管理员密码或用户账号')
        }
      }
    } else {
      ElMessage.warning('请填写完整信息')
    }
  })
}
</script>

<style scoped>
.permission-management-container {
  padding: 20px;
}

.box-card {
  max-width: 600px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style> 