<template>
  <div class="auth-container d-flex align-items-center justify-content-center py-5">
    <div class="auth-card bg-white rounded-4 shadow-lg overflow-hidden">
      
      <div class="px-4 pt-4 pb-2 text-center">
        <h4 class="fw-bold text-dark mb-1">Đăng Nhập Tài Khoản</h4>
        <p class="text-muted small">
          Chưa có tài khoản? 
          <router-link to="/register" class="fw-bold text-dark text-decoration-underline">
            Đăng ký ngay
          </router-link>
        </p>
      </div>

      <!-- FORM ĐĂNG NHẬP -->
      <div class="auth-body px-4 px-sm-5 pb-5">
        <form @submit.prevent="handleLogin" novalidate>
          
          <!-- Email đăng nhập -->
          <div class="mb-3">
            <label class="form-label fw-bold text-dark small">Email đăng nhập</label>
            <div class="input-group">
              <span class="input-group-text bg-light border-end-0 text-muted">
                <i class="bi bi-person"></i>
              </span>
              <input 
                type="email" 
                class="form-control bg-light border-start-0" 
                placeholder="Nhập email đã đăng ký"
                v-model="loginForm.email"
                required
              />
            </div>
          </div>

          <!-- Mật khẩu -->
          <div class="mb-3">
            <div class="d-flex justify-content-between align-items-center mb-1">
              <label class="form-label fw-bold text-dark small mb-0">Mật khẩu</label>
              <router-link to="/forgot-password" class="small text-decoration-none text-primary">
                Quên mật khẩu?
              </router-link>
            </div>
            <div class="input-group">
              <span class="input-group-text bg-light border-end-0 text-muted">
                <i class="bi bi-lock"></i>
              </span>
              <input 
                :type="showPassword ? 'text' : 'password'" 
                class="form-control bg-light border-start-0 border-end-0" 
                placeholder="Nhập mật khẩu"
                v-model="loginForm.password"
                required
              />
              <span 
                class="input-group-text bg-light border-start-0 text-muted cursor-pointer"
                @click="showPassword = !showPassword"
              >
                <i :class="showPassword ? 'bi bi-eye-slash' : 'bi bi-eye'"></i>
              </span>
            </div>
          </div>

          <!-- Ghi nhớ đăng nhập (Fix Copilot: Xử lý remember) -->
          <div class="mb-4 form-check">
            <input type="checkbox" class="form-check-input" id="rememberMe" v-model="loginForm.remember" />
            <label class="form-check-label small text-muted" for="rememberMe">Ghi nhớ đăng nhập</label>
          </div>

          <!-- Nút submit -->
          <button type="submit" class="btn btn-warning w-100 fw-bold py-2 rounded-pill shadow-sm mb-3 text-dark">
            ĐĂNG NHẬP
          </button>

          <!-- Hoặc đăng nhập bằng -->
          <div class="text-center my-3 position-relative">
            <hr class="text-muted opacity-25" />
            <span class="position-absolute top-50 start-50 translate-middle bg-white px-2 small text-muted">
              hoặc đăng nhập bằng
            </span>
          </div>

          <div class="d-flex gap-2">
            <button type="button" class="btn btn-outline-danger flex-fill btn-sm rounded-pill py-2" @click="loginWithGoogle">
              <i class="bi bi-google me-1"></i> Google
            </button>
            <button type="button" class="btn btn-outline-primary flex-fill btn-sm rounded-pill py-2" @click="loginWithFacebook">
              <i class="bi bi-facebook me-1"></i> Facebook
            </button>
          </div>

        </form>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRoute, useRouter } from 'vue-router'
import { googleTokenLogin } from 'vue3-google-login'
import { notify } from '../utils/notify'
import { mergeGuestCartIntoBackend } from '../utils/cart'

const router = useRouter()
const route = useRoute()
const showPassword = ref(false)

const loginForm = ref({
  email: '',
  password: '',
  remember: false
})

let facebookSdkReady

onMounted(() => {
  facebookSdkReady = new Promise((resolve, reject) => {
    window.fbAsyncInit = function() {
      window.FB.init({
        appId: '1037782925805986',
        cookie: true,
        xfbml: true,
        version: 'v18.0'
      })
      resolve(window.FB)
    }

    if (window.FB) {
      window.fbAsyncInit()
      return
    }

    const existingScript = document.getElementById('facebook-jssdk')
    if (existingScript) {
      existingScript.addEventListener('error', reject, { once: true })
      return
    }

    const script = document.createElement('script')
    script.id = 'facebook-jssdk'
    script.async = true
    script.defer = true
    script.src = 'https://connect.facebook.net/vi_VN/sdk.js'
    script.onerror = reject
    document.body.appendChild(script)
  })
})

// 1. ĐĂNG NHẬP BẰNG FORM THƯỜNG
const handleLogin = async () => {
  if (!loginForm.value.email.trim() || !loginForm.value.password.trim()) {
    notify('Vui lòng nhập đầy đủ email và mật khẩu.', 'warning')
    return
  }

  try {
    const response = await axios.post('/api/auth/login', {
      email: loginForm.value.email,
      password: loginForm.value.password
    })

    if (response.data && response.data.token) {
      await saveSessionAndRedirect(response.data)
    } else {
      notify('Đăng nhập thất bại: Không nhận được token từ server!', 'danger')
    }
  } catch (error) {
    const msg = error.response?.data?.message || 'Email hoặc mật khẩu không chính xác!'
    notify(msg, 'danger')
  }
}

// 2. ĐĂNG NHẬP GOOGLE (Fix Copilot: Thêm catch bắt lỗi đóng Popup)
const loginWithGoogle = () => {
  googleTokenLogin({
    clientId: '171393197889-uh57dtarmfni6fg03lerp3ahrt219lug.apps.googleusercontent.com'
  })
  .then(async (response) => {
    try {
      const backendRes = await axios.post('/api/auth/social-login', {
        token: response.access_token,
        provider: 'GOOGLE'
      })
      await saveSessionAndRedirect(backendRes.data)
    } catch (err) {
      const msg = err.response?.data?.message || 'Đăng nhập Google thất bại!'
      notify(msg, 'danger')
    }
  })
  .catch(() => {
    notify('Đăng nhập Google bị hủy bỏ hoặc có lỗi xảy ra!', 'warning')
  })
}

// 3. ĐĂNG NHẬP FACEBOOK (Fix Copilot: Thêm check window.FB an toàn)
const loginWithFacebook = async () => {
  try {
    const facebook = await facebookSdkReady
    if (!facebook) {
      throw new Error('Facebook SDK chưa sẵn sàng')
    }

    facebook.login((response) => {
      if (!response.authResponse) {
        notify('Đăng nhập Facebook bị hủy bỏ hoặc bị từ chối quyền!', 'warning')
        return
      }

      axios.post('/api/auth/social-login', {
        token: response.authResponse.accessToken,
        provider: 'FACEBOOK'
      })
        .then(async (backendRes) => {
          await saveSessionAndRedirect(backendRes.data)
        })
        .catch((err) => {
          const msg = err.response?.data?.message || 'Backend từ chối token Facebook!'
          notify(msg, 'danger')
        })
    }, { scope: 'public_profile,email' })
  } catch (error) {
    console.error('Facebook SDK error:', error)
    notify('Facebook SDK chưa sẵn sàng hoặc cấu hình ứng dụng chưa đúng!', 'warning')
    return
  }
}

// 4. LƯU SESSION & CHUYỂN HƯỚNG TẬP TRUNG (Fix Copilot: Xử lý remember & Chuẩn hóa Role)
const saveSessionAndRedirect = async (data) => {
  const token = data.token || ''
  const storage = loginForm.value.remember ? localStorage : sessionStorage

  // Nếu dùng sessionStorage thì xóa hết vết cũ ở localStorage để tránh xung đột
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  sessionStorage.removeItem('token')
  sessionStorage.removeItem('user')

  if (token) storage.setItem('token', token)
  storage.setItem('user', JSON.stringify(data))

  // Đồng bộ giỏ hàng khách
  try {
    if (data.id) {
      await mergeGuestCartIntoBackend(data.id, axios)
    }
  } catch (error) {
    console.error('Không thể đồng bộ giỏ khách:', error)
  }

  notify('Đăng nhập thành công!', 'success')
  window.dispatchEvent(new CustomEvent('user-logged-in'))

  // Phân quyền mượt dựa trên mảng roles chuẩn trả về từ Backend LoginResponse
  const roles = data.roles || []
  const primaryRole = data.role || data.primaryRole || ''

  if (roles.includes('ROLE_ADMIN') || roles.includes('ADMIN') || primaryRole === 'ROLE_ADMIN' || primaryRole === 'ADMIN') {
    router.push('/admin/users')
  } else {
    const redirectPath = typeof route.query.redirect === 'string' ? route.query.redirect : '/'
    router.push(redirectPath)
  }
}
</script>

<style scoped>
.auth-container { min-height: 80vh; background-color: #f8f9fa; }
.auth-card { width: 100%; max-width: 450px; border: 1px solid #eee; }
.cursor-pointer { cursor: pointer; }
</style>