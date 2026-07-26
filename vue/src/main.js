import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import axios from 'axios'

import './style.css'
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap-icons/font/bootstrap-icons.css'
import 'bootstrap/dist/js/bootstrap.bundle.min.js'

// 🔴 Cấu hình Axios Interceptor để tự động đính kèm Token
axios.interceptors.request.use(
  (config) => {
    // Lấy token từ localStorage hoặc sessionStorage
    const token = localStorage.getItem('token') || sessionStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Bổ sung xử lý khi Token hết hạn hoặc bị từ chối quyền (401 / 403)
axios.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && (error.response.status === 401 || error.response.status === 403)) {
      // Nếu hết hạn hoặc không có quyền -> Xóa token và về lại trang Login
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      sessionStorage.removeItem('token')
      sessionStorage.removeItem('user')
      
      if (router.currentRoute.value.path !== '/login') {
        router.push('/login')
      }
    }
    return Promise.reject(error)
  }
)

const app = createApp(App)

// Gán Axios vào globalProperties để có thể dùng this.$http hoặc inject ở các component
app.config.globalProperties.$http = axios

app.use(router)
app.mount('#app')