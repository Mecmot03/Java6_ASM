<template>
  <div v-if="showModal" class="modal fade show d-block" tabindex="-1" style="background: rgba(0,0,0,0.5)">
    <div class="modal-dialog modal-dialog-centered">
      <div class="modal-content border-0 shadow-lg">
        <div class="modal-header bg-primary text-white">
          <h5 class="modal-title fw-bold">
            <i class="bi me-2" :class="isEditing ? 'bi-pencil-square' : 'bi-folder-plus'"></i>
            {{ isEditing ? 'Chỉnh sửa Danh mục' : 'Thêm Danh mục Mới' }}
          </h5>
          <button type="button" class="btn-close btn-close-white" @click="$emit('close')"></button>
        </div>
        <form @submit.prevent="submitForm" novalidate>
          <div class="modal-body p-4">
            <div class="mb-3">
              <label class="form-label fw-semibold">Tên danh mục <span class="text-danger">*</span></label>
              <input type="text" class="form-control" v-model="formData.name" required placeholder="Ví dụ: Bàn phím" />
            </div>
            
            <div class="mb-3">
              <label class="form-label fw-semibold">Mô tả</label>
              <textarea class="form-control" rows="2" v-model="formData.description" placeholder="Nhập mô tả ngắn"></textarea>
            </div>

            <!-- Nút Chọn File Ảnh -->
            <div class="mb-3">
              <label class="form-label fw-semibold">Hình ảnh danh mục</label>
              <input 
                type="file" 
                class="form-control" 
                accept="image/*" 
                @change="handleFileChange" 
              />
              <div class="form-text small" v-if="formData.image">
                File đã chọn: <code>{{ formData.image }}</code>
              </div>
            </div>

            <!-- Khung Xem trước Hình ảnh -->
            <div v-if="imagePreview" class="mb-3 text-center p-2 bg-light rounded border">
              <div class="small text-muted mb-1">Xem trước hình ảnh:</div>
              <img 
                :src="imagePreview" 
                alt="Preview" 
                class="rounded img-thumbnail" 
                style="max-height: 120px;" 
                @error="handleImageError"
              />
            </div>

            <div class="form-check form-switch mb-3">
              <input class="form-check-input" type="checkbox" id="statusSwitch" v-model="formData.status" />
              <label class="form-check-label fw-semibold" for="statusSwitch">Kích hoạt danh mục</label>
            </div>
          </div>
          <div class="modal-footer bg-light">
            <button type="button" class="btn btn-secondary px-4" @click="$emit('close')">Hủy</button>
            <button type="submit" class="btn btn-primary fw-bold px-4" :disabled="loading">
              <span v-if="loading" class="spinner-border spinner-border-sm me-1"></span>
              {{ isEditing ? 'Cập nhật' : 'Thêm mới' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { notify } from '../../utils/notify'

const props = defineProps({
  showModal: Boolean,
  isEditing: Boolean,
  categoryData: {
    type: Object,
    default: () => ({})
  },
  loading: Boolean
})

const emit = defineEmits(['close', 'save'])

const formData = ref({
  id: null,
  name: '',
  description: '',
  image: '',
  status: true
})

const imagePreview = ref('')

const getImageUrl = (imageName) => {
  if (!imageName) return 'https://placehold.co/48x48?text=No+Img'
  if (imageName.startsWith('data:') || imageName.startsWith('blob:')) {
    return imageName
  }
  return `/images/${imageName}`
}

watch(() => props.categoryData, (newVal) => {
  if (newVal) {
    formData.value = { ...newVal }
    imagePreview.value = newVal.image ? getImageUrl(newVal.image) : ''
  }
}, { immediate: true })

const handleFileChange = (event) => {
  const file = event.target.files[0]
  if (file) {
    formData.value.image = file.name
    imagePreview.value = URL.createObjectURL(file)
  }
}

const handleImageError = (e) => {
  e.target.src = 'https://placehold.co/48x48?text=No+Img'
}

const submitForm = () => {
  if (!formData.value.name.trim()) {
    notify('Vui lòng nhập tên danh mục.', 'warning')
    return
  }
  emit('save', formData.value)
}
</script>

<style scoped>
.form-control { border-radius: 8px; }
.form-check-input { width: 2.5em; height: 1.25em; cursor: pointer; }
</style>