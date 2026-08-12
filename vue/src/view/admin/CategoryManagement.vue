<template>
  <div class="container py-2">
    <!-- Header -->
    <div class="d-flex justify-content-between align-items-center mb-4">
      <div>
        <h3 class="fw-bold mb-1">Quản lý Danh mục</h3>
        <p class="text-muted small mb-0">Quản lý các nhóm sản phẩm hiển thị trên hệ thống</p>
      </div>
      <button class="btn btn-primary fw-bold px-3 shadow-sm" @click="openCreateModal">
        <i class="bi bi-plus-lg me-1"></i> Thêm danh mục
      </button>
    </div>

    <!-- Thanh tìm kiếm & Thanh Lọc Trạng thái -->
    <div class="card shadow-sm border-0 mb-4">
      <div class="card-body">
        <div class="row g-2 align-items-center">
          <!-- 🆕 NEW: Ô tìm kiếm tự động lọc ngay khi gõ -->
          <div class="col-md-6">
            <input
              v-model="keyword"
              class="form-control"
              placeholder="Nhập tên danh mục cần tìm..."
              @input="handleFilter"
            >
          </div>
          
          <!-- 🆕 NEW: Dropdown Lọc trạng thái kèm nhãn (đồng bộ giao diện User) -->
          <div class="col-md-4">
            <div class="d-flex align-items-center gap-2">
              <label class="text-secondary small text-nowrap mb-0 fw-bold">Trạng thái:</label>
              <select 
                v-model="filterStatus" 
                class="form-select fw-semibold border-secondary-subtle"
                @change="handleFilter"
              >
                <option :value="null">Tất cả trạng thái</option>
                <option :value="true">Đang bán</option>
                <option :value="false">Bị khóa</option>
              </select>
            </div>
          </div>

          <!-- 🆕 NEW: Nút làm mới gọn ở góc phải -->
          <div class="col-md-2">
            <button class="btn btn-secondary w-100" @click="resetFilter">
              Làm mới
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Component Bảng -->
    <CategoryTable 
      :categories="categories" 
      @edit="openEditModal" 
      @delete="openDeleteModal" 
    />

    <!-- Component Form Modal (Thêm / Sửa) -->
    <CategoryForm 
      :showModal="showModal"
      :isEditing="isEditing"
      :categoryData="selectedCategory"
      :loading="loading"
      @close="closeModal"
      @save="handleSave"
    />

    <!-- Component DeleteModal (Xác nhận xóa) -->
    <DeleteModal
      :show="showDeleteModal"
      title="Xác nhận xóa Danh mục"
      :message="`Bạn có chắc chắn muốn xóa danh mục '${categoryToDelete?.name || ''}' không?`"
      @close="showDeleteModal = false"
      @confirm="confirmDeleteCategory"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { notify } from '../../utils/notify'
import CategoryService from '../../services/CategoryService.js'
import CategoryTable from '../../components/admin/CategoryTable.vue'
import CategoryForm from '../../components/admin/CategoryForm.vue'
import DeleteModal from '../../components/admin/DeleteModal.vue'

// 🆕 NEW: Kho chứa danh mục gốc và danh sách hiển thị
const allCategories = ref([]) 
const categories = ref([])    
const keyword = ref('')
const filterStatus = ref(null) 
const isEditing = ref(false)
const loading = ref(false)
const showModal = ref(false)
const selectedCategory = ref({})

// Trạng thái Modal Xóa
const showDeleteModal = ref(false)
const categoryToDelete = ref(null)

// 🆕 NEW: Tải danh sách tất cả danh mục ban đầu và lưu vào kho gốc
const fetchCategories = async () => {
  try {
    const data = await CategoryService.getAllCategories()
    allCategories.value = Array.isArray(data) ? data : []
    categories.value = [...allCategories.value]
  } catch (error) {
    console.error("Lỗi hiển thị danh mục:", error)
    notify("Không thể tải danh sách danh mục", "danger")
  }
}

// 🆕 NEW: Hàm lọc trực tiếp trên mảng gốc cực kỳ mượt mà
const handleFilter = () => {
  let result = [...allCategories.value]

  // 1. Lọc theo trạng thái
  if (filterStatus.value !== null) {
    result = result.filter(cat => cat.status === filterStatus.value)
  }

  // 2. Lọc theo từ khóa
  if (keyword.value.trim() !== '') {
    const key = keyword.value.toLowerCase().trim()
    result = result.filter(cat => cat.name && cat.name.toLowerCase().includes(key))
  }

  categories.value = result
}

// 🆕 NEW: Làm mới bộ lọc và ô tìm kiếm
const resetFilter = () => {
  keyword.value = ''
  filterStatus.value = null
  categories.value = [...allCategories.value]
}

const openCreateModal = () => {
  isEditing.value = false
  selectedCategory.value = { id: null, name: '', description: '', image: '', status: true }
  showModal.value = true
}

const openEditModal = (cat) => {
  isEditing.value = true
  selectedCategory.value = { ...cat }
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
}

const handleSave = async (data) => {
  loading.value = true
  try {
    if (isEditing.value) {
      await CategoryService.updateCategory(data.id, data)
      notify('Đã cập nhật danh mục thành công!', 'success')
    } else {
      await CategoryService.createCategory(data)
      notify('Đã thêm danh mục mới thành công!', 'success')
    }
    await fetchCategories() 
    handleFilter()          
    closeModal()
  } catch (error) {
    notify("Lỗi khi lưu danh mục: " + (error.response?.data?.message || error.message), 'danger')
  } finally {
    loading.value = false
  }
}

const openDeleteModal = (catOrId) => {
  if (typeof catOrId === 'object' && catOrId !== null) {
    categoryToDelete.value = catOrId
  } else {
    categoryToDelete.value = allCategories.value.find(c => c.id === catOrId) || { id: catOrId }
  }
  showDeleteModal.value = true
}

const confirmDeleteCategory = async () => {
  if (!categoryToDelete.value) return
  try {
    const id = categoryToDelete.value.id || categoryToDelete.value
    await CategoryService.deleteCategory(id)
    notify('Đã xóa danh mục thành công!', 'success')
    await fetchCategories()
    handleFilter()
  } catch (error) {
    notify(error.response?.data?.message || "Không thể xóa danh mục này do đã có Sản phẩm thuộc danh mục!", 'danger')
  } finally {
    showDeleteModal.value = false
    categoryToDelete.value = null
  }
}

onMounted(() => {
  fetchCategories()
})
</script>

<style scoped>
.card {
  border-radius: 12px;
}
</style>