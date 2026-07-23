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
              <a href="#" class="small text-decoration-none text-primary">Quên mật khẩu?</a>
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

          <!-- Ghi nhớ đăng nhập -->
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
            <!-- NÚT GOOGLE -->
            <button type="button" class="btn btn-outline-danger flex-fill btn-sm rounded-pill py-2" @click="loginWithGoogle">
              <i class="bi bi-google me-1"></i> Google
            </button>
            <!-- NÚT FACEBOOK -->
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
import { googleOneTap, googleTokenLogin } from 'vue3-google-login'
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

// Khởi tạo Facebook SDK khi component mounted
onMounted(() => {
  window.fbAsyncInit = function() {
    window.FB.init({
      appId      : '1037782925805986', 
      cookie     : true,
      xfbml      : true,
      version    : 'v18.0'
    });
  };
  (function(d, s, id) {
     var js, fjs = d.getElementsByTagName(s)[0];
     if (d.getElementById(id)) return;
     js = d.createElement(s); js.id = id;
     js.src = "https://connect.facebook.net/vi_VN/sdk.js"; // Đã đổi sang tiếng Việt
     fjs.parentNode.insertBefore(js, fjs);
   }(document, 'script', 'facebook-jssdk'));
})
// 1. ĐĂNG NHẬP THƯỜNG
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
    await saveSessionAndRedirect(response.data)
  } catch (error) {
    notify(error.response?.data?.message || 'Email hoặc mật khẩu không chính xác!', 'danger')
  }
}

// 2. ĐĂNG NHẬP GOOGLE
const loginWithGoogle = () => {
  googleTokenLogin({
    clientId: '670589969360-4pmagls4aa3rula94gkp4g3g096vao50.apps.googleusercontent.com' // Thay Client ID Google của Nhi vào đây
  }).then(async (response) => {
    try {
      // Gọi API lấy thông tin Google User thông qua Access Token
      const res = await axios.get(`https://www.googleapis.com/oauth2/v3/userinfo?access_token=${response.access_token}`)
      const googleUser = res.data

      // Gửi thông tin về Spring Boot để tạo account & cấp JWT
      const backendRes = await axios.post('/api/auth/social-login', {
        email: googleUser.email,
        fullName: googleUser.name,
        avatar: googleUser.picture,
        provider: 'GOOGLE'
      })
      await saveSessionAndRedirect(backendRes.data)
    } catch (err) {
      notify("Đăng nhập Google thất bại!", 'danger')
    }
  })
}

// 3. ĐĂNG NHẬP FACEBOOK
const loginWithFacebook = () => {
  window.FB.login((response) => {
    if (response.authResponse) {
      const accessToken = response.authResponse.accessToken;
      
      // Gọi Graph API của Facebook lấy name và picture
      axios.get(`https://graph.facebook.com/v18.0/me?fields=name,picture&access_token=${accessToken}`)
        .then(async (res) => {
          const fbUser = res.data;
          
          const backendRes = await axios.post('/api/auth/social-login', {
            email: `${fbUser.id}@facebook.com`, // Tự sinh email theo ID Facebook nếu không xin quyền email
            fullName: fbUser.name,
            avatar: fbUser.picture?.data?.url,
            provider: 'FACEBOOK'
          });

          await saveSessionAndRedirect(backendRes.data);
        })
        .catch(() => notify("Không thể lấy thông tin tài khoản Facebook!", 'danger'));
    } else {
      notify("Đăng nhập Facebook bị hủy bỏ!", 'warning');
    }
  }, { scope: 'public_profile' }); // 👈 Sửa dòng này: đổi 'public_profile,email' thành 'public_profile'
}
// Hàm lưu Token và chuyển hướng
const saveSessionAndRedirect = async (data) => {
  const token = data.token || data.accessToken || ''
  const userData = data.user || data

  if (token) localStorage.setItem('token', token)
  localStorage.setItem('user', JSON.stringify(userData))

  try {
    await mergeGuestCartIntoBackend(userData.id, axios)
  } catch (error) {
    console.error('Không thể đồng bộ giỏ khách:', error)
  }

  notify('Đăng nhập thành công!', 'success')
  window.dispatchEvent(new CustomEvent('user-logged-in'))

  const redirectPath = typeof route.query.redirect === 'string' ? route.query.redirect : '/'
  router.push(redirectPath)
}
</script>

<style scoped>
.auth-container { min-height: 80vh; background-color: #f8f9fa; }
.auth-card { width: 100%; max-width: 450px; border: 1px solid #eee; }
.cursor-pointer { cursor: pointer; }
</style>