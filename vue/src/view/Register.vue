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
        <h4 class="fw-bold text-dark mb-1">Đăng Ký Tài Khoản</h4>
        <p class="text-muted small">
          Đã có tài khoản? 
          <router-link to="/login" class="fw-bold text-dark text-decoration-underline">
            Đăng nhập ngay
          </router-link>
        </p>
      </div>

      <!-- FORM ĐĂNG KÝ -->
      <div class="auth-body px-4 px-sm-5 pb-5">
        <form @submit.prevent="handleRegister">
          
          <!-- Họ và Tên -->
          <div class="mb-3">
            <label class="form-label fw-bold text-dark small">Họ và Tên</label>
            <div class="input-group">
              <span class="input-group-text bg-light border-end-0 text-muted">
                <i class="bi bi-card-heading"></i>
              </span>
              <input 
                type="text" 
                class="form-control bg-light border-start-0" 
                placeholder="Ví dụ: Nguyễn Văn A"
                v-model="registerForm.fullname"
                required
              />
            </div>
          </div>

          <!-- Tên đăng nhập -->
          <div class="mb-3">
            <label class="form-label fw-bold text-dark small">Tên đăng nhập</label>
            <div class="input-group">
              <span class="input-group-text bg-light border-end-0 text-muted">
                <i class="bi bi-person-badge"></i>
              </span>
              <input 
                type="text" 
                class="form-control bg-light border-start-0" 
                placeholder="Nhập tên tài khoản"
                v-model="registerForm.username"
                required
              />
            </div>
          </div>

          <!-- Email -->
          <div class="mb-3">
            <label class="form-label fw-bold text-dark small">Email</label>
            <div class="input-group">
              <span class="input-group-text bg-light border-end-0 text-muted">
                <i class="bi bi-envelope"></i>
              </span>
              <input 
                type="email" 
                class="form-control bg-light border-start-0" 
                placeholder="name@example.com"
                v-model="registerForm.email"
                required
              />
            </div>
          </div>

          <!-- Mật khẩu -->
          <div class="mb-3">
            <label class="form-label fw-bold text-dark small">Mật khẩu</label>
            <div class="input-group">
              <span class="input-group-text bg-light border-end-0 text-muted">
                <i class="bi bi-lock"></i>
              </span>
              <input 
                :type="showPassword ? 'text' : 'password'" 
                class="form-control bg-light border-start-0 border-end-0" 
                placeholder="Tối thiểu 6 ký tự"
                v-model="registerForm.password"
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

          <!-- Đồng ý điều khoản -->
          <div class="mb-4 form-check">
            <input type="checkbox" class="form-check-input" id="terms" required />
            <label class="form-check-label small text-muted" for="terms">
              Tôi đồng ý với <a href="#" class="text-primary text-decoration-none">Điều khoản dịch vụ</a>
            </label>
          </div>

          <!-- Nút đăng ký -->
          <button type="submit" class="btn btn-warning w-100 fw-bold py-2 rounded-pill shadow-sm text-dark">
            TẠO TÀI KHOẢN
          </button>

        </form>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const showPassword = ref(false)

const registerForm = ref({
  fullname: '',
  username: '',
  email: '',
  password: ''
})

const handleRegister = () => {
  alert(`Tạo tài khoản thành công cho: ${registerForm.value.fullname}`)
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