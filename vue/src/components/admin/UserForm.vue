<template>
  <div v-if="showModal" class="modal fade show d-block" tabindex="-1" style="background: rgba(0,0,0,0.5)">
    <div class="modal-dialog modal-lg modal-dialog-centered">
      <div class="modal-content border-0 shadow-lg">
        <div class="modal-header bg-primary text-white">
          <h5 class="modal-title fw-bold">
            <i class="bi me-2" :class="form.id ? 'bi-person-gear' : 'bi-person-plus-fill'"></i>
            {{ form.id ? "Cập nhật tài khoản User" : "Thêm tài khoản User mới" }}
          </h5>
          <button type="button" class="btn-close btn-close-white" @click="close"></button>
        </div>

        <form @submit.prevent="save" novalidate>
          <div class="modal-body p-4" style="max-height: 80vh; overflow-y: auto;">
            <div class="row g-3">
              <!-- Họ tên -->
              <div class="col-md-6">
                <label class="form-label fw-semibold">Họ và tên <span class="text-danger">*</span></label>
                <input ref="inputFullName" v-model="form.fullName" class="form-control" placeholder="Ví dụ: Nguyễn Văn A" required>
              </div>

              <!-- Email -->
              <div class="col-md-6">
                <label class="form-label fw-semibold">Email <span class="text-danger">*</span></label>
                <input ref="inputEmail" type="email" v-model="form.email" class="form-control" placeholder="example@gmail.com" required>
              </div>

              <!-- Mật khẩu -->
              <div class="col-md-6">
                <label class="form-label fw-semibold">Mật khẩu <span v-if="!form.id" class="text-danger">*</span></label>
                <input ref="inputPassword" type="password" v-model="form.password" class="form-control"
                  :placeholder="form.id ? 'Để trống nếu không muốn đổi' : 'Nhập mật khẩu'">
              </div>

              <!-- SĐT -->
              <div class="col-md-6">
                <label class="form-label fw-semibold">Số điện thoại <span class="text-danger">*</span></label>
                <input ref="inputPhone" v-model="form.phone" class="form-control" placeholder="0901234567" maxlength="10">
              </div>

              <!-- Địa chỉ -->
              <div class="col-12">
                <label class="form-label fw-semibold">Địa chỉ</label>
                <input v-model="form.address" class="form-control" placeholder="Nhập địa chỉ chi tiết">
              </div>

              <!-- Phân quyền hệ thống (Checkbox đa chọn) -->
              <div class="col-12" ref="roleSection">
                <label class="form-label fw-semibold d-block">Phân quyền hệ thống <span class="text-danger">*</span></label>
                <div class="p-3 border rounded bg-light d-flex flex-wrap gap-4">
                  <div class="form-check">
                    <input 
                      class="form-check-input" 
                      type="checkbox" 
                      id="roleUser" 
                      value="ROLE_USER" 
                      v-model="selectedRoles"
                    >
                    <label class="form-check-label fw-medium text-primary" for="roleUser">
                      Khách hàng (ROLE_USER)
                    </label>
                  </div>

                  <div class="form-check">
                    <input 
                      class="form-check-input" 
                      type="checkbox" 
                      id="roleStaff" 
                      value="ROLE_STAFF" 
                      v-model="selectedRoles"
                    >
                    <label class="form-check-label fw-medium text-success" for="roleStaff">
                      Nhân viên (ROLE_STAFF)
                    </label>
                  </div>

                  <div class="form-check">
                    <input 
                      class="form-check-input" 
                      type="checkbox" 
                      id="roleAdmin" 
                      value="ROLE_ADMIN" 
                      v-model="selectedRoles"
                    >
                    <label class="form-check-label fw-medium text-danger" for="roleAdmin">
                      Quản trị viên (ROLE_ADMIN)
                    </label>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="modal-footer bg-light">
            <button type="button" class="btn btn-secondary px-4" @click="close">Hủy</button>
            <button class="btn btn-primary fw-bold px-4" type="submit">
              <i class="bi bi-check-circle-fill me-1"></i> {{ form.id ? "Cập nhật" : "Thêm mới" }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, watch, nextTick } from 'vue'
import { notify } from '../../utils/notify'

const props = defineProps({
  showModal: Boolean,
  user: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['save', 'close'])

// Khai báo Element Refs hỗ trợ tự cuộn & focus
const inputFullName = ref(null)
const inputEmail = ref(null)
const inputPassword = ref(null)
const inputPhone = ref(null)
const roleSection = ref(null)

const emptyForm = {
  id: null,
  fullName: '',
  email: '',
  password: '',
  phone: '',
  address: ''
}

const form = reactive({ ...emptyForm })
const selectedRoles = ref(['ROLE_USER'])

// Hàm cuộn mượt và focus vào element lỗi
const focusElement = async (el) => {
  await nextTick()
  if (el) {
    el.scrollIntoView({ behavior: 'smooth', block: 'center' })
    if (typeof el.focus === 'function') el.focus()
  }
}

// Trích xuất tất cả vai trò mà User đang có
const extractUserRoles = (userData) => {
  if (!userData) return ['ROLE_USER']
  const rolesFound = new Set()

  const list = userData.authorities || userData.roles || []
  if (Array.isArray(list)) {
    list.forEach(item => {
      const name = typeof item === 'string' ? item : (item.authority || item.role?.id || item.name || item.id)
      if (name) rolesFound.add(String(name).toUpperCase())
    })
  }

  const str = JSON.stringify(userData).toUpperCase()
  if (str.includes('ROLE_ADMIN')) rolesFound.add('ROLE_ADMIN')
  if (str.includes('ROLE_STAFF')) rolesFound.add('ROLE_STAFF')
  if (str.includes('ROLE_USER')) rolesFound.add('ROLE_USER')

  const result = Array.from(rolesFound)
  return result.length > 0 ? result : ['ROLE_USER']
}

watch(() => props.user, (val) => {
  if (!val || !val.id) {
    Object.assign(form, emptyForm)
    selectedRoles.value = ['ROLE_USER']
    return
  }

  Object.assign(form, emptyForm, val, { password: '' })
  selectedRoles.value = extractUserRoles(val)
}, { immediate: true, deep: true })

const save = () => {
  const fullName = String(form.fullName || '').trim()
  const email = String(form.email || '').trim()
  const password = String(form.password || '').trim()
  const phone = String(form.phone || '').trim()

  // 1. Validate Họ Tên
  if (!fullName) {
    notify('Vui lòng nhập họ và tên.', 'warning')
    focusElement(inputFullName.value)
    return
  }

  // 2. Validate Email
  if (!email) {
    notify('Vui lòng nhập email.', 'warning')
    focusElement(inputEmail.value)
    return
  }

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(email)) {
    notify('Email không đúng định dạng.', 'warning')
    focusElement(inputEmail.value)
    return
  }

  // 3. Validate Mật khẩu (Chỉ bắt buộc khi thêm mới)
  if (!form.id && !password) {
    notify('Vui lòng nhập mật khẩu cho tài khoản mới.', 'warning')
    focusElement(inputPassword.value)
    return
  }

  // 4. Validate Số điện thoại
  if (!phone) {
    notify('Vui lòng nhập số điện thoại.', 'warning')
    focusElement(inputPhone.value)
    return
  }

  const phoneRegex = /^0(3|5|7|8|9)[0-9]{8}$/
  if (!phoneRegex.test(phone)) {
    notify('Số điện thoại không đúng định dạng (bắt đầu bằng 03, 05, 07, 08, 09 và đủ 10 số).', 'warning')
    focusElement(inputPhone.value)
    return
  }

  // 5. Validate Phân quyền
  if (!selectedRoles.value || selectedRoles.value.length === 0) {
    notify('Vui lòng chọn ít nhất 1 quyền hạn!', 'warning')
    focusElement(roleSection.value)
    return
  }

  // Tạo payload gửi đi
  const payload = {
    ...form,
    fullName,
    email,
    password,
    phone,
    role: selectedRoles.value[0],
    roles: selectedRoles.value,
    authorities: selectedRoles.value.map(r => ({
      authority: r,
      role: { id: r }
    }))
  }

  emit('save', payload)
}

const close = () => {
  emit('close')
}
</script>

<style scoped>
.form-control { border-radius: 8px; }
.form-check-input { cursor: pointer; width: 1.2em; height: 1.2em; }
.form-check-label { cursor: pointer; }
</style>