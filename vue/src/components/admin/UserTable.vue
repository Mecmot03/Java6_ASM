<template>
  <div class="card shadow-sm border-0">
    <div class="card-header bg-dark text-white">
      <h5 class="mb-0">Danh sách User</h5>
    </div>

    <div class="table-responsive">
      <table class="table table-hover align-middle mb-0">
        <thead class="table-light">
          <tr>
            <th>ID</th>
            <th>Họ tên</th>
            <th>Email</th>
            <th>SĐT</th>
            <th>Quyền</th>
            <th>Trạng thái</th>
            <th class="text-center">Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in users" :key="user.id">
            <td>{{ user.id }}</td>
            <td>
              <strong>{{ user.fullName || '---' }}</strong>
            </td>
            <td>{{ user.email }}</td>
            <td>{{ user.phone || '---' }}</td>
            <td>
              <div class="d-flex flex-wrap gap-1">
                <span v-for="role in getUserRoles(user)" :key="role" class="badge" :class="getRoleBadgeClass(role)">
                  {{ role }}
                </span>
              </div>
            </td>
            <td>
              <span class="badge" :class="isUserActive(user) ? 'bg-success' : 'bg-danger'">
                {{ isUserActive(user) ? 'Hoạt động' : 'Đã khóa' }}
              </span>
            </td>
            <td class="text-center align-middle">
              <!-- Nút Chỉnh sửa -->
              <button class="btn btn-sm btn-warning text-white me-1" title="Chỉnh sửa" @click="$emit('edit', user)">
                <i class="bi bi-pencil-square"></i>
              </button>

              <!-- Nút Khóa / Mở khóa (Disabled nếu là chính Admin đang đăng nhập) -->
              <button
                type="button"
                class="btn btn-sm me-2"
                :class="isUserActive(user) ? 'btn-outline-danger' : 'btn-outline-success'"
                :disabled="isSelf(user.id)"
                :title="isSelf(user.id) ? 'Bạn không thể tự khóa tài khoản của chính mình' : (isUserActive(user) ? 'Khóa tài khoản này' : 'Mở khóa tài khoản')"
                @click="$emit('changeStatus', user)"
              >
                <i
                  class="bi me-1"
                  :class="isUserActive(user) ? 'bi-lock-fill' : 'bi-unlock-fill'"
                ></i>
                {{ isUserActive(user) ? 'Khóa' : 'Mở khóa' }}
              </button>
            </td>
          </tr>

          <tr v-if="!users || users.length === 0">
            <td colspan="7" class="text-center py-5 text-muted">
              <i class="bi bi-inbox fs-2"></i>
              <br>
              Chưa có dữ liệu User
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  users: {
    type: Array,
    default: () => []
  },
  currentAdminId: {
    type: [Number, String],
    default: null
  }
})

defineEmits([
  'edit',
  'changeStatus'
])

// Kiểm tra dòng dữ liệu này có phải chính Admin đang đăng nhập hay không
const isSelf = (userId) => {
  if (!props.currentAdminId || !userId) return false
  return String(props.currentAdminId) === String(userId)
}

const isUserActive = (user) => {
  return user.enabled !== false
}

const getUserRoles = (user) => {
  if (!user) return ['ROLE_USER']

  let roles = []
  if (Array.isArray(user.roles)) {
    roles = user.roles.map(r => typeof r === 'string' ? r : (r.name || r.authority || r.id))
  } else if (Array.isArray(user.authorities)) {
    roles = user.authorities.map(a => typeof a === 'string' ? a : (a.authority || a.name || a.id))
  } else {
    const userStr = JSON.stringify(user).toUpperCase()
    if (userStr.includes('ROLE_ADMIN') || userStr.includes('"ADMIN"')) roles.push('ROLE_ADMIN')
    if (userStr.includes('ROLE_STAFF') || userStr.includes('"STAFF"')) roles.push('ROLE_STAFF')
    if (userStr.includes('ROLE_USER') || userStr.includes('"USER"')) roles.push('ROLE_USER')
  }

  return roles.length > 0 ? roles : ['ROLE_USER']
}

const getRoleBadgeClass = (roleName) => {
  switch (roleName) {
    case 'ROLE_ADMIN':
      return 'bg-danger'
    case 'ROLE_STAFF':
      return 'bg-success'
    default:
      return 'bg-primary'
  }
}
</script>

<style scoped>
.card {
  border-radius: 15px;
}

.card-header {
  font-weight: bold;
}

table {
  font-size: 14px;
}

th {
  white-space: nowrap;
}

td {
  vertical-align: middle;
}

.badge {
  font-size: 12px;
  padding: 6px 10px;
}

button {
  border-radius: 8px;
}

.table tbody tr:hover {
  background: #f8f9fa;
}
</style>