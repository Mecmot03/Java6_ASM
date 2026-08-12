<template>
  <div class="container-fluid py-2">
    <!-- Header trang -->
    <div class="d-flex justify-content-between align-items-center mb-4">
      <div>
        <h3 class="fw-bold mb-1">Quản lý User</h3>
        <p class="text-muted small mb-0">Quản lý tài khoản khách hàng và phân quyền hệ thống</p>
      </div>
      <button class="btn btn-primary fw-bold px-3 shadow-sm" @click="openCreateModal">
        <i class="bi bi-plus-lg me-1"></i> Thêm User
      </button>
    </div>

    <!-- Thanh tìm kiếm & Thanh Lọc Trạng thái -->
    <div class="card shadow-sm border-0 mb-4">
      <div class="card-body">
        <div class="row g-2 align-items-center">
          <!-- Ô tìm kiếm (Tự động lọc ngay khi gõ) -->
          <div class="col-md-6">
            <input
              v-model="keyword"
              class="form-control"
              placeholder="Nhập họ tên, email hoặc username cần tìm..."
              @input="searchUser"
            >
          </div>
          
          <!-- Dropdown Lọc trạng thái -->
          <div class="col-md-4">
            <div class="d-flex align-items-center gap-2">
              <label class="text-secondary small text-nowrap mb-0 fw-bold">Trạng thái:</label>
              <select 
                v-model="statusFilter" 
                class="form-select fw-semibold border-secondary-subtle"
              >
                <option value="ALL">Tất cả user</option>
                <option value="ACTIVE">Hoạt động</option>
                <option value="LOCKED">Bị khóa</option>
              </select>
            </div>
          </div>

          <!-- Nút làm mới -->
          <div class="col-md-2">
            <button class="btn btn-secondary w-100" @click="resetFilters">
              Làm mới
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Thông báo lỗi nếu có -->
    <div v-if="loadError" class="alert alert-danger mb-3">
      {{ loadError }}
    </div>

    <!-- Bảng danh sách User gốc -->
    <UserTable
      :users="filteredUsers"
      :currentAdminId="currentAdminId"
      @edit="openEditModal"
      @changeStatus="changeStatus"
    />

    <!-- Form Modal (Thêm / Sửa User) -->
    <UserForm
      :showModal="showFormModal"
      :user="selectedUser"
      @save="saveUser"
      @close="closeFormModal"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { notify } from '../../utils/notify'
import UserService from '../../services/UserService'
import UserTable from '../../components/admin/UserTable.vue'
import UserForm from '../../components/admin/UserForm.vue'

const rawUsers = ref([])
const keyword = ref('')
const searchSubmitted = ref('')
const statusFilter = ref('ALL')
const selectedUser = ref({})
const loadError = ref('')
const showFormModal = ref(false)

// Lấy ID của Admin đang đăng nhập từ localStorage
const currentAdminId = computed(() => {
  const userStr = localStorage.getItem('user') || localStorage.getItem('currentUser')
  if (!userStr) return null
  try {
    const u = JSON.parse(userStr)
    return u.id || u.userId || null
  } catch (e) {
    return null
  }
})

// Bắt chính xác trạng thái Hoạt động / Bị khóa
const getUserActiveStatus = (user) => {
  if (!user) return true

  if (typeof user.enabled === 'boolean') return user.enabled
  if (typeof user.locked === 'boolean') return !user.locked
  if (typeof user.isLocked === 'boolean') return !user.isLocked
  if (typeof user.active === 'boolean') return user.active

  if (typeof user.status === 'string') {
    const s = user.status.toUpperCase()
    return s === 'ACTIVE' || s === 'ENABLED' || s === '1'
  }

  return true
}

const filteredUsers = computed(() => {
  return rawUsers.value.filter(user => {
    // 1. Lọc theo từ khóa
    const key = searchSubmitted.value.trim().toLowerCase()
    const matchesKeyword = !key || 
      (user.fullName && user.fullName.toLowerCase().includes(key)) ||
      (user.email && user.email.toLowerCase().includes(key)) ||
      (user.username && user.username.toLowerCase().includes(key))

    // 2. Lọc theo trạng thái
    const isActive = getUserActiveStatus(user)
    let matchesStatus = true
    if (statusFilter.value === 'ACTIVE') matchesStatus = (isActive === true)
    if (statusFilter.value === 'LOCKED') matchesStatus = (isActive === false)

    return matchesKeyword && matchesStatus
  })
})

const loadUsers = async () => {
  loadError.value = ''
  try {
    const data = await UserService.getAllUsers()
    rawUsers.value = Array.isArray(data) ? data : []
  } catch (error) {
    rawUsers.value = []
    loadError.value = error.response?.data?.message || error.response?.data || 'Không tải được danh sách User!'
    notify(loadError.value, 'danger')
  }
}

const searchUser = () => {
  searchSubmitted.value = keyword.value
}

const resetFilters = () => {
  keyword.value = ''
  searchSubmitted.value = ''
  statusFilter.value = 'ALL'
  loadUsers()
}

const openCreateModal = () => {
  selectedUser.value = {}
  showFormModal.value = true
}

const openEditModal = (user) => {
  selectedUser.value = JSON.parse(JSON.stringify(user))
  showFormModal.value = true
}

const closeFormModal = () => {
  showFormModal.value = false
  selectedUser.value = {}
}

const saveUser = async (userData) => {
  try {
    if (userData.id) {
      await UserService.updateUser(userData.id, userData)
      notify('Cập nhật User thành công!', 'success')
    } else {
      await UserService.createUser(userData)
      notify('Thêm User thành công!', 'success')
    }
    closeFormModal()
    await loadUsers()
  } catch (error) {
    notify(error.response?.data?.message || "Không thể lưu User!", 'danger')
  }
}

// Hàm đổi trạng thái có kiểm tra chống tự khóa
const changeStatus = async (userOrId) => {
  const targetId = typeof userOrId === 'object' ? userOrId.id : userOrId

  // Kiểm tra nếu id cần khóa trùng với ID của Admin hiện tại
  if (currentAdminId.value && String(currentAdminId.value) === String(targetId)) {
    notify('Bạn không thể tự khóa hoặc đổi trạng thái tài khoản của chính mình!', 'warning')
    return
  }

  try {
    await UserService.changeStatus(targetId)
    notify('Cập nhật trạng thái thành công!', 'success')
    await loadUsers()
  } catch (error) {
    notify(error.response?.data?.message || "Không đổi được trạng thái!", 'danger')
  }
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.card { border-radius: 12px; }
.container-fluid { padding: 0; }
</style>