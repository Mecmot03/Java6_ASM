<template>
  <div class="container-fluid py-2">
    <!-- Header -->
    <div class="d-flex justify-content-between align-items-center mb-4">
      <div>
        <h3 class="fw-bold mb-1">Quản lý User</h3>
        <p class="text-muted small mb-0">Quản lý tài khoản khách hàng và phân quyền hệ thống</p>
      </div>
      <button class="btn btn-primary fw-bold px-3 shadow-sm" @click="openCreateModal">
        <i class="bi bi-plus-lg me-1"></i> Thêm User
      </button>
    </div>

    <!-- Thanh tìm kiếm -->
    <div class="card shadow-sm border-0 mb-4">
      <div class="card-body">
        <div class="row g-2">
          <div class="col-md-8">
            <input
              v-model="keyword"
              class="form-control"
              placeholder="Nhập họ tên hoặc email cần tìm..."
              @keyup.enter="searchUser"
            >
          </div>
          <div class="col-md-2">
            <button class="btn btn-primary w-100" @click="searchUser">
              <i class="bi bi-search me-1"></i> Tìm
            </button>
          </div>
          <div class="col-md-2">
            <button class="btn btn-secondary w-100" @click="loadUsers">
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

    <!-- Danh sách User Bảng -->
    <UserTable
      :users="users"
      @edit="openEditModal"
      @delete="openDeleteModal"
      @changeStatus="changeStatus"
    />

    <!-- Form Modal (Thêm / Sửa User) -->
    <UserForm
      :showModal="showFormModal"
      :user="selectedUser"
      @save="saveUser"
      @close="closeFormModal"
    />

    <!-- Modal Xác nhận xóa User -->
    <DeleteModal
      :show="showDeleteModal"
      title="Xác nhận xóa User"
      :message="`Bạn có chắc chắn muốn xóa tài khoản '${userToDelete?.email || userToDelete?.fullName || ''}' không?`"
      @close="showDeleteModal = false"
      @confirm="confirmDeleteUser"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { notify } from '../../utils/notify'
import UserService from '../../services/UserService'
import UserTable from '../../components/admin/UserTable.vue'
import UserForm from '../../components/admin/UserForm.vue'
import DeleteModal from '../../components/admin/DeleteModal.vue'

const users = ref([])
const rawUsers = ref([])
const keyword = ref('')
const selectedUser = ref({})
const loadError = ref('')

const showFormModal = ref(false)
const showDeleteModal = ref(false)
const userToDelete = ref(null)

const loadUsers = async () => {
  keyword.value = ''
  loadError.value = ''
  try {
    const data = await UserService.getAllUsers()
    rawUsers.value = Array.isArray(data) ? data : []
    users.value = [...rawUsers.value]
  } catch (error) {
    users.value = []
    rawUsers.value = []
    loadError.value = error.response?.data?.message || error.response?.data || 'Không tải được danh sách User!'
    notify(loadError.value, 'danger')
  }
}

const searchUser = () => {
  if (keyword.value.trim() === '') {
    users.value = [...rawUsers.value]
    return
  }
  const key = keyword.value.toLowerCase()
  users.value = rawUsers.value.filter(user =>
    (user.fullName && user.fullName.toLowerCase().includes(key)) ||
    (user.email && user.email.toLowerCase().includes(key))
  )
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

const openDeleteModal = (userOrId) => {
  if (typeof userOrId === 'object' && userOrId !== null) {
    userToDelete.value = userOrId
  } else {
    userToDelete.value = users.value.find(u => u.id === userOrId) || { id: userOrId }
  }
  showDeleteModal.value = true
}

// 🔴 CẬP NHẬT: LUÔN TẢI LẠI DANH SÁCH USER TRONG FINALLY
const confirmDeleteUser = async () => {
  if (!userToDelete.value) return
  const id = userToDelete.value.id || userToDelete.value

  try {
    await UserService.deleteUser(id)
    notify('Đã xóa User thành công!', 'success')
  } catch (error) {
    const errorMsg = error.response?.data?.message || error.response?.data || "Không thể xóa User này do vướng dữ liệu!"
    notify(typeof errorMsg === 'string' ? errorMsg : "Không thể xóa User!", 'danger')
  } finally {
    showDeleteModal.value = false
    userToDelete.value = null
    await loadUsers() // 🔴 Luôn làm mới lại dữ liệu hiển thị
  }
}

const changeStatus = async (userOrId) => {
  try {
    const id = typeof userOrId === 'object' ? userOrId.id : userOrId
    await UserService.changeStatus(id)
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