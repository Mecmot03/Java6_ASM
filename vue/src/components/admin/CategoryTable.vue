<template>
  <div class="card border-0 shadow-sm rounded-3">
    <div class="card-header bg-dark text-white">
      <h5 class="mb-0">Danh sách danh mục</h5>
    </div>
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
            <tr v-if="!categories || categories.length === 0">
              <td colspan="6" class="text-center py-4 text-muted">
                Đang tải dữ liệu hoặc chưa có danh mục nào...
              </td>
            </tr>
            <tr v-for="(cat, index) in categories" :key="cat.id">
              <td class="ps-4 fw-bold text-muted">{{ index + 1 }}</td>
              <td class="fw-bold text-dark">{{ cat.name }}</td>
              <td class="text-muted small">{{ cat.description || 'Không có mô tả' }}</td>
              <td>
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
                <button type="button" class="btn btn-sm btn-outline-primary me-2" @click="$emit('edit', cat)" title="Sửa danh mục">
                  <i class="bi bi-pencil-square"></i>
                </button>
                <button type="button" class="btn btn-sm btn-outline-danger" @click="openDeleteModal(cat.id)" title="Xóa danh mục">
                  <i class="bi bi-trash"></i>
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>

  <!-- Modal xác nhận xóa -->
  <DeleteModal :show="showDelete" @confirm="confirmDelete" @close="closeDelete" />
</template>

<script setup>
import { ref } from 'vue'
import DeleteModal from './DeleteModal.vue'

defineProps({
  categories: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['edit', 'delete'])

const showDelete = ref(false)
const deleteId = ref(null)

const openDeleteModal = (id) => {
  deleteId.value = id
  showDelete.value = true
}

const confirmDelete = () => {
  emit('delete', deleteId.value)
  showDelete.value = false
}

const closeDelete = () => {
  showDelete.value = false
}

const getImageUrl = (imageName) => {
  if (!imageName) return 'https://placehold.co/48x48?text=No+Img'
  if (imageName.startsWith('data:') || imageName.startsWith('blob:')) {
    return imageName
  }
  return `/images/${imageName}`
}

const handleImageError = (e) => {
  e.target.src = 'https://placehold.co/48x48?text=No+Img'
}
</script>