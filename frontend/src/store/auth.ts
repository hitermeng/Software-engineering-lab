import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authAPI, type User, type LoginDTO, type RegisterDTO } from '@/api'
import { ElMessage } from 'element-plus'
import router from '@/router'

export const useAuthStore = defineStore('auth', () => {
    const user = ref<User | null>(null)
    const token = ref<string | null>(null)
    const loading = ref(false)

    // 计算属性
    const isAuthenticated = computed(() => !!token.value)
    const currentUser = computed(() => user.value)

    // 检查认证状态
    const checkAuth = () => {
        const savedToken = localStorage.getItem('token')
        const savedUser = localStorage.getItem('user')

        if (savedToken && savedUser && savedUser !== 'undefined') {
            token.value = savedToken
            try {
                user.value = JSON.parse(savedUser)
            } catch (e) {
                console.error("解析用户信息失败:", e)
                // 清理无效的本地存储数据
                localStorage.removeItem('user')
                localStorage.removeItem('token')
                token.value = null
                user.value = null
            }
        }
    }

    // 登录
    const login = async (loginData: LoginDTO) => {
        try {
            loading.value = true
            const response = await authAPI.login(loginData)
            console.log('后端登录响应的data:', response.data)

            // 从后端响应中正确获取 token 和用户信息
            token.value = response.data.accessToken
            user.value = response.data.userInfo

            // 保存到本地存储
            localStorage.setItem('token', response.data.accessToken)
            localStorage.setItem('user', JSON.stringify(response.data.userInfo))

            ElMessage.success('登录成功')
            router.push('/dashboard')
            return true
        } catch (error) {
            console.error('登录失败:', error)
            ElMessage.error('登录失败')
            return false
        } finally {
            loading.value = false
        }
    }

    // 注册
    const register = async (registerData: RegisterDTO) => {
        try {
            loading.value = true
            await authAPI.register(registerData)
            ElMessage.success('注册成功，请登录')
            return true
        } catch (error) {
            console.error('注册失败:', error)
            return false
        } finally {
            loading.value = false
        }
    }

    // 获取用户信息
    const fetchProfile = async () => {
        try {
            const response = await authAPI.getProfile()
            user.value = response.data
            localStorage.setItem('user', JSON.stringify(response.data))
        } catch (error) {
            console.error('获取用户信息失败:', error)
        }
    }

    // 更新用户信息
    const updateProfile = async (userData: Partial<User>) => {
        try {
            const response = await authAPI.updateProfile(userData)
            user.value = response.data
            localStorage.setItem('user', JSON.stringify(response.data))
            ElMessage.success('更新成功')
        } catch (error) {
            console.error('更新用户信息失败:', error)
        }
    }

    // 退出登录
    const logout = async () => {
        try {
            await authAPI.logout()
        } catch (error) {
            console.error('退出登录失败:', error)
        } finally {
            // 清理本地状态
            token.value = null
            user.value = null
            localStorage.removeItem('token')
            localStorage.removeItem('user')

            router.push('/login')
            ElMessage.success('已退出登录')
        }
    }

    return {
        user,
        token,
        loading,
        isAuthenticated,
        currentUser,
        checkAuth,
        login,
        register,
        fetchProfile,
        updateProfile,
        logout
    }
})