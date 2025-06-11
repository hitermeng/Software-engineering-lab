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
        path: '',
        name: 'ArticleList',
        component: () => import('@/views/ArticleList.vue')
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
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, _from, next) => {
  const authStore = useAuthStore()
  
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login')
  } else if (to.path === '/login' && authStore.isAuthenticated) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router