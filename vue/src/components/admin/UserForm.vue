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
              <!-- Họ tên (🔴 Validate) -->
              <div class="col-md-6">
                <label class="form-label fw-semibold">Họ và tên <span class="text-danger">*</span></label>
                <input 
                  v-model="form.fullName" 
                  class="form-control" 
                  :class="{ 'is-invalid': errors.fullName }"
                  placeholder="Ví dụ: Nguyễn Văn A"
                >
                <div v-if="errors.fullName" class="invalid-feedback d-block fw-bold">
                  {{ errors.fullName }}
                </div>
              </div>

              <!-- Email (🔴 Validate) -->
              <div class="col-md-6">
                <label class="form-label fw-semibold">Email <span class="text-danger">*</span></label>
                <input 
                  type="email" 
                  v-model="form.email" 
                  class="form-control" 
                  :class="{ 'is-invalid': errors.email }"
                  placeholder="example@gmail.com"
                >
                <div v-if="errors.email" class="invalid-feedback d-block fw-bold">
                  {{ errors.email }}
                </div>
              </div>

              <!-- Mật khẩu (🔴 Validate) -->
              <div class="col-md-6">
                <label class="form-label fw-semibold">Mật khẩu <span v-if="!form.id" class="text-danger">*</span></label>
                <input 
                  type="password" 
                  v-model="form.password" 
                  class="form-control"
                  :class="{ 'is-invalid': errors.password }"
                  :placeholder="form.id ? 'Để trống nếu không muốn đổi' : 'Nhập mật khẩu từ 6 ký tự'"
                >
                <div v-if="errors.password" class="invalid-feedback d-block fw-bold">
                  {{ errors.password }}
                </div>
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

              <!-- Phân quyền hệ thống (🔴 Validate) -->
              <div class="col-12" ref="roleSection">
                <label class="form-label fw-semibold d-block">Phân quyền hệ thống <span class="text-danger">*</span></label>
                <div 
                  class="p-3 border rounded bg-light d-flex flex-wrap gap-4"
                  :class="{ 'border-danger': errors.roles }"
                >
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
                <div v-if="errors.roles" class="invalid-feedback d-block fw-bold mt-1">
                  {{ errors.roles }}
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

// 🔴 Biến lưu trữ lỗi trực quan dưới ô nhập liệu
const errors = reactive({
  fullName: '',
  email: '',
  password: '',
  roles: ''
})

const clearErrors = () => {
  errors.fullName = ''
  errors.email = ''
  errors.password = ''
  errors.roles = ''
}

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
  clearErrors()
  if (!val || !val.id) {
    Object.assign(form, emptyForm)
    selectedRoles.value = ['ROLE_USER']
    return
  }

  Object.assign(form, emptyForm, val, { password: '' })
  selectedRoles.value = extractUserRoles(val)
}, { immediate: true, deep: true })

// 🔴 KIỂM TRA VALIDATE KHI BẤM LƯU TÀI KHOẢN
const save = () => {
  clearErrors()
  let hasError = false

  const fullName = String(form.fullName || '').trim()
  const email = String(form.email || '').trim()
  const password = String(form.password || '').trim()
  const phone = String(form.phone || '').trim()

  // 1. Kiểm tra Họ và tên
  if (!fullName) {
    errors.fullName = "Vui lòng nhập họ và tên!"
        focusElement(inputFullName.value)
    hasError = true
    return
  }

  // 2. Kiểm tra Email
  if (!email) {
    errors.email = "Vui lòng nhập địa chỉ email!"
    hasError = true
        focusElement(inputEmail.value)
    return
  } else if (!/^\S+@\S+\.\S+$/.test(email)) {
    errors.email = "Email không đúng định dạng (Ví dụ: name@gmail.com)!"
    hasError = true
    focusElement(inputEmail.value)
    return
  }

  // 3. Kiểm tra Mật khẩu (bắt buộc khi tạo mới, tùy chọn khi sửa)
  if (!form.id && !password) {
    errors.password = "Vui lòng nhập mật khẩu cho tài khoản mới!"
    hasError = true
        focusElement(inputPassword.value)
    return
  } else if (password && password.length < 6) {
    errors.password = "Mật khẩu phải chứa ít nhất 6 ký tự!"
    hasError = true
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
  
  // 5. Kiểm tra Phân quyền
  if (!selectedRoles.value || selectedRoles.value.length === 0) {
    errors.roles = "Vui lòng chọn ít nhất 1 quyền hạn cho tài khoản!"
    hasError = true
       focusElement(roleSection.value)
    return
  }

  if (hasError) {
    notify('Vui lòng kiểm tra lại thông tin nhập trong form!', 'warning')
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
  clearErrors()
  emit('close')
}
</script>

<style scoped>
.form-control { border-radius: 8px; }
.form-check-input { cursor: pointer; width: 1.2em; height: 1.2em; }
.form-check-label { cursor: pointer; }
.invalid-feedback { font-size: 12px; color: #dc3545; }
</style>