import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建axios实例
const api = axios.create({
    baseURL: '/api',
    timeout: 60000
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
    type?: string
}

export interface Article {
    id: number
    title: string
    content: string
    summary?: string
    categoryId?: number
    categoryName?: string
    tags?: string | string[]
    status: number
    isShared: number
    viewCount: number
    likeCount: number
    userId: number
    username: string
    createTime: string
    updateTime: string
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
    tags?: string
    status: number
    isShared: number
}

export interface FilterDTO {
    keyword?: string
    categoryId?: number
    tag?: string
    status?: string
    startDate?: string
    endDate?: string
    isShared?: boolean
    page?: number
    size?: number
    sort?: string
    sortField?: string
    sortOrder?: string
    sortBy?: string
}

export interface IPage<T> {
    records: T[]
    total: number
    size: number
    current: number
    pages: number
}

export interface ArticleVO {
    id: number
    title: string
    content: string
    summary?: string
    categoryId?: number
    categoryName?: string
    categoryType?: string
    tags: string
    tagArray?: string[]
    status: number
    isShared: number
    viewCount: number
    likeCount: number
    commentCount: number
    userId: number
    username: string
    authorName: string
    authorAvatar?: string
    createTime: string
    updateTime: string
}

// 评论相关接口
export interface Comment {
    id: number
    articleId: number
    content: string
    userId: number
    userName: string
    userAvatar?: string
    parentId?: number
    replyToUserId?: number
    replyToUserName?: string
    createTime: string
    replies?: Comment[]
}

export interface CommentDTO {
    articleId: number
    content: string
    parentId?: number
    replyToUserId?: number
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
    create: (data: CategoryDTO) => api.post('/api/categories', data),

    // 更新分类
    update: (id: number, data: Partial<CategoryDTO>) => api.put(`/api/categories/${id}`, data),

    // 删除分类
    delete: (id: number) => api.delete(`/api/categories/${id}`),

    // 获取分类详情
    getById: (id: number) => api.get(`/api/categories/${id}`),

    // 获取分类树
    getTree: () => api.get('/api/categories'),

    // 获取子分类
    getChildren: (parentId: number) => api.get(`/api/categories/children/${parentId}`),

    // 批量删除分类
    batchDelete: (ids: number[]) => api.delete('/api/categories/batch', { data: ids }),
    // 移动分类
    move: (id: number, parentId?: number, sort?: number) =>
        api.put(`/api/categories/${id}/move`, { parentId, sort }),

    // 更新排序
    updateSort: (sortData: { id: number; sort: number; parentId?: number }[]) =>
        api.put('/api/categories/sort', { sortData })
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
    getList: (params: FilterDTO) => api.get<IPage<ArticleVO>>('/articles', { params }),

    // 获取共享文章列表
    getSharedList: (params: FilterDTO) => api.get('/articles/shared', { params }),

    // 点赞文章
    like: (id: number) => api.post(`/articles/${id}/like`),

    // 获取热门标签
    getPopularTags: () => api.get('/articles/tags/popular'),

    // 获取文章统计数据
    getStatistics: () => api.get('/articles/statistics'),

    // 生成文章摘要
    generateSummary: (content: string) => api.post('/articles/generate-summary', { content }),

    // 获取最近文章列表
    getRecentArticles: () => api.get('/articles/recent'),

    // 获取公开文章列表（社区文章）
    getPublicArticles: (params: FilterDTO) => api.get<IPage<ArticleVO>>('/articles/public', { params }),

    // 获取热门文章
    getHotArticles: () => api.get<ArticleVO[]>('/articles/hot'),

    // 获取热门分类
    getHotCategories: () => api.get<Category[]>('/articles/categories/hot'),

    // 点赞文章
    likeArticle: (id: number) => api.post(`/articles/${id}/like`),

    // 取消点赞
    unlikeArticle: (id: number) => api.delete(`/articles/${id}/like`),

    // 获取文章评论列表
    getComments: (params: { articleId: number | string; page: number; size: number }) =>
        api.get<IPage<Comment>>(`/articles/${params.articleId}/comments`, {
            params: { page: params.page, size: params.size }
        }),

    // 创建评论
    createComment: (data: CommentDTO) => api.post(`/articles/${data.articleId}/comments`, data),

    // 删除评论
    deleteComment: (id: number) => api.delete(`/articles/comments/${id}`),

    // 获取文章详情（包含点赞状态）
    getArticleDetail: (id: number | string) => api.get<ArticleVO & { isLiked: boolean }>(`/articles/${id}/detail`),

    // 获取仪表盘统计数据
    getDashboardStatistics: () => api.get('/articles/dashboard/statistics')
}

export const searchAPI = {
    // 基本搜索
    basicSearch: (params: {
        keyword: string
        page?: number
        size?: number
    }) => api.get<IPage<ArticleVO>>('/search', { params }),

    // 高级搜索
    advancedSearch: (params: {
        keyword?: string
        categoryId?: number
        tag?: string
        sortField?: string
        sortOrder?: string
        page?: number
        size?: number
    }) => api.get<IPage<ArticleVO>>('/search/advanced', { params }),

    // 获取热门搜索标签
    getHotSearchTags: () => api.get<string[]>('/articles/tags/popular'),

    // 获取搜索建议
    getSuggestions: (keyword: string) =>
        api.get<string[]>(`/search/suggestions?keyword=${keyword}`)
}

export interface UpdateUserRoleDTO {
    adminPassword: string;
    username: string;
    role: 'USER' | 'ADMIN';
}

export const userAPI = {
    // 更新用户角色
    updateUserRole: (data: UpdateUserRoleDTO) => api.put('/api/users/role', data),
    
    // 更新用户状态
    updateUserStatus: (username: string, status: number, adminPassword: string) => 
        api.put(`/api/users/${username}/status`, null, {
            params: {
                status,
                adminPassword
            }
        })
}

export default api