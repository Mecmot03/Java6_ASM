<template>
  <div class="container py-4">
    <!-- Header -->
    <div class="d-flex justify-content-between align-items-center mb-4">
      <div>
        <h3 class="fw-bold mb-1">Quản lý Danh mục</h3>
        <p class="text-muted small mb-0">Quản lý các nhóm sản phẩm hiển thị trên hệ thống</p>
      </div>
      <button class="btn btn-warning fw-bold px-3" @click="openCreateModal">
        <i class="bi bi-plus-lg me-1"></i> Thêm danh mục
      </button>
    </div>

    <!-- Bảng dữ liệu -->
    <div class="card border-0 shadow-sm rounded-3">
      <div class="card-body p-0">
        <div class="table-responsive">
          <table class="table table-hover align-middle mb-0">
            <thead class="table-light">
              <tr>
                <th class="ps-4" style="width: 80px;">STT</th>
                <th>Tên danh mục</th>
                <th>Mô tả</th>
                <th>Hình ảnh</th>
                <th>Trạng thái</th>
                <th class="text-end pe-4" style="width: 160px;">Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="categories.length === 0">
                <td colspan="6" class="text-center py-4 text-muted">
                  Đang tải dữ liệu hoặc chưa có danh mục nào...
                </td>
              </tr>
              <tr v-for="(cat, index) in categories" :key="cat.id">
                <td class="ps-4 fw-bold text-muted">{{ index + 1 }}</td>
                <!-- Đã bỏ icon trước tên danh mục -->
                <td class="fw-bold text-dark">{{ cat.name }}</td>
                <td class="text-muted small">{{ cat.description || 'Không có mô tả' }}</td>
                <td>
                  <!-- Hiển thị thumbnail hình ảnh -->
                  <img 
                    :src="getImageUrl(cat.image)" 
                    :alt="cat.name" 
                    class="rounded border object-fit-cover" 
                    style="width: 48px; height: 48px;" 
                    @error="handleImageError"
                  />
                </td>
                <td>
                  <span class="badge" :class="cat.status ? 'bg-success' : 'bg-secondary'">
                    {{ cat.status ? 'Hoạt động' : 'Ẩn' }}
                  </span>
                </td>
                <td class="text-end pe-4">
                  <button class="btn btn-sm btn-outline-primary me-2" @click="openEditModal(cat)">
                    <i class="bi bi-pencil-square"></i>
                  </button>
                  <button class="btn btn-sm btn-outline-danger" @click="handleDelete(cat.id)">
                    <i class="bi bi-trash"></i>
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- Modal Form -->
    <div v-if="showModal" class="modal fade show d-block" tabindex="-1" style="background: rgba(0,0,0,0.5)">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow">
          <div class="modal-header bg-warning text-dark">
            <h5 class="modal-title fw-bold">
              {{ isEditing ? 'Chỉnh sửa Danh mục' : 'Thêm Danh mục Mới' }}
            </h5>
            <button type="button" class="btn-close" @click="closeModal"></button>
          </div>
          <form @submit.prevent="handleSubmit">
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

              <!-- Khung Xem trước Hình ảnh (Preview) -->
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
              <button type="button" class="btn btn-secondary" @click="closeModal">Hủy</button>
              <button type="submit" class="btn btn-warning fw-bold" :disabled="loading">
                <span v-if="loading" class="spinner-border spinner-border-sm me-1"></span>
                {{ isEditing ? 'Cập nhật' : 'Thêm mới' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import CategoryService from '../../services/CategoryService.js'

const categories = ref([])
const isEditing = ref(false)
const loading = ref(false)
const showModal = ref(false)
const imagePreview = ref('')

const formData = ref({
  id: null,
  name: '',
  description: '',
  image: '',
  status: true
})

// Đường dẫn lấy ảnh từ thư mục public/images
const getImageUrl = (imageName) => {
  if (!imageName) return 'https://placehold.co/48x48?text=No+Img'
  
  // Nếu là dữ liệu xem trước khi vừa chọn file mới
  if (imageName.startsWith('data:') || imageName.startsWith('blob:')) {
    return imageName
  }
  
  return `/images/${imageName}`
}

// Xử lý khi bấm nút chọn file ảnh từ máy tính
const handleFileChange = (event) => {
  const file = event.target.files[0]
  if (file) {
    formData.value.image = file.name
    imagePreview.value = URL.createObjectURL(file)
  }
}

// Chống vỡ giao diện nếu chưa có file ảnh trong thư mục public
const handleImageError = (e) => {
  e.target.src = 'https://placehold.co/48x48?text=No+Img'
}

// Lấy danh sách từ Backend
const fetchCategories = async () => {
  try {
    categories.value = await CategoryService.getAllCategories()
  } catch (error) {
    console.error("Lỗi hiển thị danh mục:", error)
  }
}

const openCreateModal = () => {
  isEditing.value = false
  formData.value = { id: null, name: '', description: '', image: '', status: true }
  imagePreview.value = ''
  showModal.value = true
}

const openEditModal = (cat) => {
  isEditing.value = true
  formData.value = { ...cat }
  imagePreview.value = cat.image ? getImageUrl(cat.image) : ''
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
  imagePreview.value = ''
}

const handleSubmit = async () => {
  loading.value = true
  try {
    if (isEditing.value) {
      await CategoryService.updateCategory(formData.value.id, formData.value)
    } else {
      await CategoryService.createCategory(formData.value)
    }
    await fetchCategories()
    closeModal()
  } catch (error) {
    alert("Lỗi khi lưu danh mục: " + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

const handleDelete = async (id) => {
  if (confirm("Bạn có chắc chắn muốn xóa danh mục này?")) {
    try {
      await CategoryService.deleteCategory(id)
      await fetchCategories()
    } catch (error) {
      alert("Không thể xóa danh mục này do đã có Sản phẩm thuộc danh mục (ràng buộc Khóa ngoại)!")
    }
  }
}

onMounted(() => {
  fetchCategories()
})
</script>