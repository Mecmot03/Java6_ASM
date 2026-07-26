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
              <!-- Hiển thị nhiều Badge Quyền cùng lúc -->
              <div class="d-flex flex-wrap gap-1">
                <span 
                  v-for="role in getUserRoles(user)" 
                  :key="role" 
                  class="badge" 
                  :class="getRoleBadgeClass(role)"
                >
                  {{ role }}
                </span>
              </div>
            </td>
            <td>
              <span class="badge" :class="user.enabled !== false ? 'bg-success' : 'bg-secondary'">
                {{ user.enabled !== false ? 'Hoạt động' : 'Đã khóa' }}
              </span>
            </td>
            <td class="text-center">
              <!-- Nút Sửa -->
              <button
                type="button"
                class="btn btn-warning btn-sm me-2"
                @click="$emit('edit', user)"
                title="Sửa thông tin"
              >
                <i class="bi bi-pencil-square"></i>
              </button>

              <!-- Nút Xóa -->
              <button
                type="button"
                class="btn btn-danger btn-sm me-2"
                @click="$emit('delete', user)"
                title="Xóa vĩnh viễn"
              >
                <i class="bi bi-trash"></i>
              </button>

              <!-- Nút Khóa / Mở khóa -->
              <button
                type="button"
                class="btn btn-secondary btn-sm"
                @click="$emit('changeStatus', user)"
                :title="user.enabled !== false ? 'Khóa tài khoản này' : 'Mở khóa tài khoản'"
              >
                <i
                  class="bi"
                  :class="user.enabled !== false ? 'bi-lock-fill' : 'bi-unlock-fill'"
                ></i>
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
defineProps({
  users: {
    type: Array,
    default: () => []
  }
})

defineEmits([
  'edit',
  'delete',
  'changeStatus'
])

// Lấy mảng danh sách quyền để render
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

// Phân biệt màu sắc từng Badge Quyền
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
.card { border-radius: 15px; }
.card-header { font-weight: bold; }
table { font-size: 14px; }
th { white-space: nowrap; }
td { vertical-align: middle; }
.badge { font-size: 12px; padding: 6px 10px; }
button { border-radius: 8px; }
.table tbody tr:hover { background: #f8f9fa; }
</style>