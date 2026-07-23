<template>
  <div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <div>
        <h3 class="fw-bold mb-1">Thông tin tài khoản</h3>
        <p class="text-muted mb-0">Chỉnh sửa thông tin cá nhân của bạn.</p>
      </div>
      <router-link to="/" class="btn btn-outline-dark btn-sm rounded-pill px-3">
        <i class="bi bi-house me-1"></i> Về trang chủ
      </router-link>
    </div>

    <div class="card border-0 shadow-sm rounded-4 p-4">
      <div class="row g-4 align-items-start">
        <div class="col-md-4 text-center">
          <div class="avatar-circle mx-auto mb-3 d-flex align-items-center justify-content-center">
            <i class="bi bi-person-fill fs-1 text-warning"></i>
          </div>
          <h4 class="fw-bold mb-1">{{ form.fullName || 'Người dùng' }}</h4>
          <p class="text-muted mb-0">{{ form.email || 'Chưa có email' }}</p>
        </div>

        <div class="col-md-8">
          <div class="row g-3">
            <div class="col-md-6">
              <label class="form-label text-muted small mb-1">Họ và tên</label>
              <input v-model.trim="form.fullName" type="text" class="form-control" placeholder="Nhập họ và tên" />
            </div>
            <div class="col-md-6">
              <label class="form-label text-muted small mb-1">Email</label>
              <input :value="form.email" type="text" class="form-control bg-light" readonly />
            </div>
            <div class="col-md-6">
              <label class="form-label text-muted small mb-1">Số điện thoại</label>
              <input v-model.trim="form.phone" type="text" class="form-control" placeholder="Nhập số điện thoại" />
            </div>
            <div class="col-md-6">
              <label class="form-label text-muted small mb-1">Vai trò</label>
              <input :value="roleLabel" type="text" class="form-control bg-light" readonly />
            </div>
            <div class="col-12">
              <label class="form-label text-muted small mb-1">Địa chỉ</label>
              <input v-model.trim="form.address" type="text" class="form-control" placeholder="Nhập địa chỉ" />
            </div>
            <div class="col-12">
              <label class="form-label text-muted small mb-1">Mật khẩu mới</label>
              <input v-model="form.password" type="password" class="form-control" placeholder="Để trống nếu không đổi mật khẩu" />
            </div>
          </div>

          <div class="d-flex gap-2 mt-4">
            <button class="btn btn-warning rounded-pill px-4 fw-bold text-dark" :disabled="saving" @click="saveProfile">
              {{ saving ? 'Đang lưu...' : 'Lưu thay đổi' }}
            </button>
            <button class="btn btn-outline-secondary rounded-pill px-4" :disabled="saving" @click="resetProfile">
              Hủy thay đổi
            </button>
            <router-link
              v-if="isAdmin"
              to="/orders?status=PENDING"
              class="btn btn-outline-dark rounded-pill px-4"
            >
              Xem quản lý đơn hàng
            </router-link>
            <router-link
              v-else
              to="/order-history?status=PENDING"
              class="btn btn-outline-dark rounded-pill px-4"
            >
              Xem lịch sử đặt hàng
            </router-link>
          </div>
        </div>
      </div>
    </div>

    <div v-if="message" class="alert mt-3" :class="messageType === 'success' ? 'alert-success' : 'alert-danger'">
      {{ message }}
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import axios from 'axios'
import { getAuthConfig } from '../services/authRequest'

const originalUser = ref({})
const saving = ref(false)
const message = ref('')
const messageType = ref('success')

const form = reactive({
  fullName: '',
  email: '',
  phone: '',
  address: '',
  password: ''
})

const roleLabel = computed(() => {
  const role = originalUser.value?.role
  if (role === 'ROLE_ADMIN' || role === 'ADMIN') return 'Quản trị viên'
  return 'Khách hàng'
})

const isAdmin = computed(() => {
  const role = originalUser.value?.role
  return role === 'ROLE_ADMIN' || role === 'ADMIN'
})

const loadProfile = () => {
  const raw = localStorage.getItem('user')
  if (!raw) {
    originalUser.value = {}
    return
  }

  try {
    const user = JSON.parse(raw) || {}
    originalUser.value = user
    form.fullName = user.fullName || ''
    form.email = user.email || ''
    form.phone = user.phone || ''
    form.address = user.address || ''
    form.password = ''
  } catch (error) {
    originalUser.value = {}
  }
}

const resetProfile = () => {
  loadProfile()
  message.value = ''
}

const saveProfile = async () => {
  message.value = ''
  saving.value = true

  try {
    const payload = {
      fullName: form.fullName,
      phone: form.phone,
      address: form.address,
      password: form.password
    }

    const response = await axios.put('/api/auth/me', payload, getAuthConfig())
    const updatedUser = {
      ...originalUser.value,
      ...response.data
    }

    localStorage.setItem('user', JSON.stringify(updatedUser))
    originalUser.value = updatedUser
    form.fullName = updatedUser.fullName || ''
    form.email = updatedUser.email || ''
    form.phone = updatedUser.phone || ''
    form.address = updatedUser.address || ''
    form.password = ''

    window.dispatchEvent(new Event('user-updated'))

    messageType.value = 'success'
    message.value = 'Cập nhật thông tin thành công.'
  } catch (error) {
    messageType.value = 'danger'
    message.value = error.response?.data?.message || error.response?.data || 'Không thể cập nhật thông tin.'
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  loadProfile()
})
</script>

<style scoped>
.avatar-circle {
  width: 110px;
  height: 110px;
  border-radius: 50%;
  background: #fff8e1;
  border: 1px solid rgba(255, 193, 7, 0.25);
}
</style>
