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

    <!-- Thanh tìm kiếm -->
    <div class="card shadow-sm border-0 mb-4">
      <div class="card-body">
        <div class="row g-2">
          <div class="col-md-8">
            <input
              v-model="keyword"
              class="form-control"
              placeholder="Nhập tên danh mục cần tìm..."
              @keyup.enter="searchCategory"
            >
          </div>
          <div class="col-md-2">
            <button class="btn btn-primary w-100" @click="searchCategory">
              <i class="bi bi-search me-1"></i> Tìm
            </button>
          </div>
          <div class="col-md-2">
            <button class="btn btn-secondary w-100" @click="fetchCategories">
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

const categories = ref([])
const keyword = ref('')
const isEditing = ref(false)
const loading = ref(false)
const showModal = ref(false)
const selectedCategory = ref({})

// Trạng thái Modal Xóa
const showDeleteModal = ref(false)
const categoryToDelete = ref(null)

// Tải danh sách & Reset thanh tìm kiếm
const fetchCategories = async () => {
  keyword.value = '' // Xóa trắng ô tìm kiếm khi bấm Làm mới
  try {
    categories.value = await CategoryService.getAllCategories()
  } catch (error) {
    console.error("Lỗi hiển thị danh mục:", error)
  }
}

// Xử lý Tìm kiếm Danh mục
const searchCategory = async () => {
  if (keyword.value.trim() === '') {
    fetchCategories()
    return
  }
  
  try {
    const allData = await CategoryService.getAllCategories()
    const key = keyword.value.toLowerCase()
    categories.value = allData.filter(cat => 
      cat.name && cat.name.toLowerCase().includes(key)
    )
  } catch (error) {
    console.error("Lỗi tìm kiếm danh mục:", error)
  }
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
    categoryToDelete.value = categories.value.find(c => c.id === catOrId) || { id: catOrId }
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