import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import axios from 'axios'

import './style.css'
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap-icons/font/bootstrap-icons.css'
import 'bootstrap/dist/js/bootstrap.bundle.min.js'

// 1. Cấu hình Request Interceptor
axios.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token') || sessionStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 2. Cấu hình Response Interceptor
axios.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error.response?.status

    if (status === 401) {
      // Het han token -> Xoa session va bat dang nhap lai
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      sessionStorage.removeItem('token')
      sessionStorage.removeItem('user')
      
      if (router.currentRoute.value.path !== '/login') {
        router.push('/login')
      }
    } else if (status === 403) {
      // Khong co quyen -> KHONG xoa token, chi sang trang chu neu dang vao trang cam
      console.warn('Tai khoan khong co quyen truy cap tai nguyen nay.')
      
      const currentPath = router.currentRoute.value.path
      if (currentPath.startsWith('/admin') || currentPath.includes('/orders')) {
        router.push('/')
      }
    }

    return Promise.reject(error)
  }
)

const app = createApp(App)
app.config.globalProperties.$http = axios
app.use(router)
app.mount('#app')