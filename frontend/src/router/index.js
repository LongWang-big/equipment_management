import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/login/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/login/Register.vue')
  },
  {
    path: '/',
    component: () => import('../layouts/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/dashboard/Dashboard.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'device',
        name: 'Device',
        component: () => import('../views/device/Device.vue'),
        meta: { title: '设备管理' }
      },
      {
        path: 'category',
        name: 'Category',
        component: () => import('../views/category/Category.vue'),
        meta: { title: '分类管理' }
      },
      {
        path: 'inventory',
        name: 'Inventory',
        component: () => import('../views/inventory/Inventory.vue'),
        meta: { title: '库存管理' }
      },
      {
        path: 'stock-record',
        name: 'StockRecord',
        component: () => import('../views/stock-record/StockRecord.vue'),
        meta: { title: '出入库记录' }
      },
      {
        path: 'purchase',
        name: 'Purchase',
        component: () => import('../views/purchase/Purchase.vue'),
        meta: { title: '采购管理' }
      },
      {
        path: 'scrap',
        name: 'Scrap',
        component: () => import('../views/scrap/Scrap.vue'),
        meta: { title: '报损管理' }
      },
      {
        path: 'user',
        name: 'User',
        component: () => import('../views/user/User.vue'),
        meta: { title: '用户管理' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 导航守卫
router.beforeEach((to, from, next) => {
  if (to.path === '/login' || to.path === '/register') {
    next()
    return
  }
  const authStore = useAuthStore()
  if (!authStore.token) {
    next('/login')
  } else {
    next()
  }
})

export default router
