import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建axios实例
const api = axios.create({
    baseURL: '/api',
    timeout: 10000
})

// 请求拦截器
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token')
        if (token) {
            config.headers.Authorization = `Bearer ${token}`
        }
        return config
    },
    (error) => {
        return Promise.reject(error)
    }
)

// 响应拦截器
api.interceptors.response.use(
    (response) => {
        const { data } = response
        if (data.code === 200) {
            return data
        } else {
            ElMessage.error(data.message || '请求失败')
            return Promise.reject(new Error(data.message || '请求失败'))
        }
    },
    (error) => {
        if (error.response?.status === 401) {
            localStorage.removeItem('token')
            localStorage.removeItem('user')
            window.location.href = '/login'
        } else {
            ElMessage.error(error.response?.data?.message || '网络错误')
        }
        return Promise.reject(error)
    }
)

// 类型定义
export interface User {
    id: number
    username: string
    email: string
    nickname?: string
    avatar?: string
    createdAt: string
}

export interface Category {
    id: number
    name: string
    description?: string
    parentId?: number
    level: number
    children?: Category[]
    createdAt: string
    color?: string
    icon?: string
    sort?: number
    isActive?: boolean
    articleCount?: number
}

export interface Article {
    id: number
    title: string
    content: string
    summary?: string
    categoryId?: number
    categoryName?: string
    tags?: string[]
    status: 'DRAFT' | 'PUBLISHED'
    isShared: boolean
    viewCount: number
    likeCount: number
    userId: number
    username: string
    createdAt: string
    updatedAt: string
}

export interface LoginDTO {
    username: string
    password: string
}

export interface RegisterDTO {
    username: string
    password: string
    email: string
    nickname?: string
}

export interface CategoryDTO {
    name: string
    description?: string
    parentId?: number
    color?: string
    icon?: string
    sort?: number
    isActive?: boolean
}

export interface ArticleSaveDTO {
    title: string
    content: string
    summary?: string
    categoryId?: number
    tags?: string[]
    status: 'DRAFT' | 'PUBLISHED'
    isShared: boolean
}

export interface FilterDTO {
    keyword?: string
    categoryId?: number
    tags?: string[]
    status?: string
    startDate?: string
    endDate?: string
    isShared?: boolean
    page?: number
    size?: number
    sort?: string
}

// API接口
export const authAPI = {
    // 用户注册
    register: (data: RegisterDTO) => api.post('/auth/register', data),

    // 用户登录
    login: (data: LoginDTO) => api.post('/auth/login', data),

    // 获取用户信息
    getProfile: () => api.get('/auth/profile'),

    // 更新用户信息
    updateProfile: (data: Partial<User>) => api.put('/auth/profile', data),

    // 退出登录
    logout: () => api.post('/auth/logout')
}

export const categoryAPI = {
    // 创建分类
    create: (data: CategoryDTO) => api.post('/categories', data),

    // 更新分类
    update: (id: number, data: Partial<CategoryDTO>) => api.put(`/categories/${id}`, data),

    // 删除分类
    delete: (id: number) => api.delete(`/categories/${id}`),

    // 获取分类详情
    getById: (id: number) => api.get(`/categories/${id}`),

    // 获取分类树
    getTree: () => api.get('/categories/tree'),

    // 获取子分类
    getChildren: (parentId: number) => api.get(`/categories/children/${parentId}`),

    // 批量删除分类
    batchDelete: (ids: number[]) => api.post('/categories/batch-delete', { ids }),

    // 移动分类
    move: (id: number, parentId?: number, sort?: number) => 
        api.put(`/categories/${id}/move`, { parentId, sort }),

    // 更新排序
    updateSort: (sortData: { id: number; sort: number; parentId?: number }[]) => 
        api.put('/categories/sort', { sortData })
}

export const articleAPI = {
    // 创建文章
    create: (data: ArticleSaveDTO) => api.post('/articles', data),

    // 更新文章
    update: (id: number, data: ArticleSaveDTO) => api.put(`/articles/${id}`, data),

    // 删除文章
    delete: (id: number) => api.delete(`/articles/${id}`),

    // 获取文章详情
    getById: (id: number) => api.get(`/articles/${id}`),

    // 分页查询文章列表
    getList: (params: FilterDTO) => api.get('/articles', { params }),

    // 获取共享文章列表
    getSharedList: (params: FilterDTO) => api.get('/articles/shared', { params }),

    // 点赞文章
    like: (id: number) => api.post(`/articles/${id}/like`),

    // 获取热门标签
    getPopularTags: () => api.get('/articles/tags/popular'),

    // 获取文章统计数据
    getStatistics: () => api.get('/articles/statistics'),

    // 生成文章摘要
    generateSummary: (content: string) => api.post('/articles/generate-summary', { content })
}

export const searchAPI = {
    // 全文搜索
    search: (keyword: string, params?: FilterDTO) => api.get('/search', {
        params: { keyword, ...params }
    }),

    // 高级搜索
    advancedSearch: (params: FilterDTO) => api.get('/search/advanced', { params })
}

export interface UpdateUserRoleDTO {
    adminPassword: string;
    username: string;
    role: 'USER' | 'ADMIN';
}

export const userAPI = {
    // 更新用户角色
    updateUserRole: (data: UpdateUserRoleDTO) => api.put('/users/role', data)
}

export default api