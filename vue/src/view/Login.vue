<template>
  <div class="auth-container d-flex align-items-center justify-content-center py-5">
    <div class="auth-card bg-white rounded-4 shadow-lg overflow-hidden">
      
      <!-- HEADER LOGO -->
      <!-- <div class="auth-header text-center py-4 px-3">
        <router-link to="/" class="logo text-decoration-none d-inline-flex align-items-center mb-2">
          <span class="logo-icon me-2">⚡</span>
          <span class="logo-text">thegioidientu</span>
        </router-link>
        <p class="text-muted small mb-0">Hệ thống bán lẻ điện thoại & thiết bị điện tử</p>
      </div> -->

      <!-- TIÊU ĐỀ TRANG -->
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
        <form @submit.prevent="handleLogin">
          
          <!-- Tên đăng nhập / Email -->
          <div class="mb-3">
            <label class="form-label fw-bold text-dark small">Tài khoản / Email</label>
            <div class="input-group">
              <span class="input-group-text bg-light border-end-0 text-muted">
                <i class="bi bi-person"></i>
              </span>
              <input 
                type="text" 
                class="form-control bg-light border-start-0" 
                placeholder="Nhập tên đăng nhập hoặc email"
                v-model="loginForm.username"
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
            <button type="button" class="btn btn-outline-danger flex-fill btn-sm rounded-pill py-2">
              <i class="bi bi-google me-1"></i> Google
            </button>
            <button type="button" class="btn btn-outline-primary flex-fill btn-sm rounded-pill py-2">
              <i class="bi bi-facebook me-1"></i> Facebook
            </button>
          </div>

        </form>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const showPassword = ref(false)

const loginForm = ref({
  username: '',
  password: '',
  remember: false
})

const handleLogin = () => {
  alert(`Đăng nhập tài khoản: ${loginForm.value.username}`)
}
</script>

<style scoped>
.auth-container {
  min-height: 80vh;
  background-color: #f8f9fa;
}

.auth-card {
  width: 100%;
  max-width: 450px;
  border: 1px solid #eee;
}

.auth-header {
  background-color: #ffd400;
}

.logo-icon {
  background-color: #000;
  color: #ffd400;
  border-radius: 8px;
  padding: 4px 8px;
  font-size: 18px;
}

.logo-text {
  font-size: 22px;
  font-family: 'Arial Black', sans-serif;
  font-weight: 900;
  color: #000;
  display: inline-block;
  transform: skewX(-15deg);
}

.form-control:focus {
  box-shadow: none;
  border-color: #ffd400;
}

.cursor-pointer {
  cursor: pointer;
}
</style>