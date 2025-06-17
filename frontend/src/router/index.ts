import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useAuthStore } from '@/store/auth'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/views/Dashboard.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: 'article',
        name: 'ArticleList',
        component: () => import('@/views/ArticleList.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'categories',
        name: 'CategoryTree',
        component: () => import('@/views/CategoryTree.vue')
      },
      {
        path: 'editor/:id?',
        name: 'Editor',
        component: () => import('@/views/Editor.vue'),
        props: true
      },
      {
        path: 'shared',
        name: 'SharedArticles',
        component: () => import('@/views/ArticleList.vue'),
        props: { isShared: true }
      }
    ]
  },
  {
    path: '/articles',
    name: 'ArticlesAlias',
    component: () => import('@/views/ArticleList.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/permission',
    name: 'PermissionManagement',
    component: () => import('@/views/PermissionManagement.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/categories',
    name: 'CategoriesAlias',
    component: () => import('@/views/CategoryTree.vue')
  },
  {
    path: '/editor/:id?',
    name: 'EditorAlias',
    component: () => import('@/views/Editor.vue'),
    props: true,
    meta: { requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'UserProfile',
    component: () => import('@/views/UserProfile.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/search',
    name: 'SearchPage',
    component: () => import('@/views/SearchPage.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/community',
    name: 'Community',
    component: () => import('@/views/Community.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/article/:id',
    name: 'ArticleDetail',
    component: () => import('@/views/ArticleDetail.vue'),
    props: true,
    meta: { requiresAuth: false }
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)

  if (requiresAuth && !authStore.isAuthenticated) {
    next('/login')
  } else {
    next()
  }
})

export default router