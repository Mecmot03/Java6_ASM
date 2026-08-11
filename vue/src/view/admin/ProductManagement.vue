<template>
  <div class="container-fluid product-page">
    <!-- Header -->
    <div class="d-flex justify-content-between align-items-center mb-4">
      <div>
        <h3 class="fw-bold mb-1">Quản lý Sản phẩm</h3>
        <p class="text-muted small mb-0">Quản lý toàn bộ thông tin sản phẩm và kho hàng</p>
      </div>
      <button class="btn btn-primary fw-bold px-3 shadow-sm" @click="openCreateModal">
        <i class="bi bi-plus-lg me-1"></i> Thêm sản phẩm
      </button>
    </div>

    <!-- Thanh tìm kiếm & Thanh Lọc Trạng thái -->
    <div class="card shadow-sm border-0 mb-4">
      <div class="card-body">
        <div class="row g-2 align-items-center">
          <div class="col-md-6">
            <input
              v-model="keyword"
              type="text"
              class="form-control"
              placeholder="Nhập tên sản phẩm cần tìm..."
              @keyup.enter="searchProduct"
            >
          </div>

          <!-- Dropdown Lọc trạng thái -->
          <div class="col-md-3">
            <div class="d-flex align-items-center gap-2">
              <label class="text-secondary small text-nowrap mb-0 fw-bold">Trạng thái:</label>
              <select 
                v-model="statusFilter" 
                class="form-select fw-semibold border-secondary-subtle"
                @change="searchProduct"
              >
                <option :value="null">Tất cả sản phẩm</option>
                <option :value="true">Đang bán</option>
                <option :value="false">Ngừng bán</option>
              </select>
            </div>
          </div>

          <div class="col-md-3 d-flex gap-2">
            <button class="btn btn-primary w-100" @click="searchProduct">
              <i class="bi bi-search me-1"></i> Tìm
            </button>
            <button class="btn btn-secondary w-100" @click="resetSearch">
              Làm mới
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Bảng Sản phẩm -->
    <ProductTable
      :products="filteredProducts"
      @edit="openEditModal"
      @delete="openDeleteModal"
    />

    <!-- Modal Form Sản phẩm -->
    <ProductForm
      :showModal="showFormModal"
      :product="selectedProduct"
      @save="saveProduct"
      @close="closeFormModal"
    />

    <!-- Modal Xác nhận xóa Sản phẩm -->
    <DeleteModal
      :show="showDeleteModal"
      title="Xác nhận xóa Sản phẩm"
      :message="`Bạn có chắc chắn muốn xóa sản phẩm '${productToDelete?.name || ''}' không?`"
      @close="showDeleteModal = false"
      @confirm="confirmDeleteProduct"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { notify } from '../../utils/notify'
import ProductService from '../../services/ProductService'
import ProductTable from '../../components/admin/ProductTable.vue'
import ProductForm from '../../components/admin/ProductForm.vue'
import DeleteModal from '../../components/admin/DeleteModal.vue'

const rawProducts = ref([])
const keyword = ref('')
const searchSubmitted = ref('')
const statusFilter = ref(null) // null | true | false
const selectedProduct = ref({})

const showFormModal = ref(false)
const showDeleteModal = ref(false)
const productToDelete = ref(null)

// Tải danh sách tất cả sản phẩm
const loadProducts = async () => {
  try {
    const params = {
      keyword: keyword.value.trim() || null,
      status: statusFilter.value
    }
    const res = await ProductService.filterProducts(params)
    rawProducts.value = Array.isArray(res) ? res : (res?.data || [])
  } catch (error) {
    console.error("Lỗi tải danh sách sản phẩm:", error)
  }
}

// Lọc kết hợp Client-side để đảm bảo hoạt động kể cả khi Backend không lọc đúng
const filteredProducts = computed(() => {
  return rawProducts.value.filter(product => {
    // 1. Lọc theo tên sản phẩm
    const key = searchSubmitted.value.trim().toLowerCase()
    const matchesKeyword = !key || (product.name && product.name.toLowerCase().includes(key))

    // 2. Lọc theo trạng thái
    let matchesStatus = true
    if (statusFilter.value !== null) {
      matchesStatus = product.status === statusFilter.value
    }

    return matchesKeyword && matchesStatus
  })
})

const searchProduct = () => {
  searchSubmitted.value = keyword.value
  loadProducts()
}

const resetSearch = () => {
  keyword.value = ''
  searchSubmitted.value = ''
  statusFilter.value = null
  loadProducts()
}

const openCreateModal = () => {
  selectedProduct.value = {}
  showFormModal.value = true
}

const openEditModal = (product) => {
  selectedProduct.value = { ...product }
  showFormModal.value = true
}

const closeFormModal = () => {
  showFormModal.value = false
  selectedProduct.value = {}
}

const saveProduct = async (product) => {
  try {
    if (product.id) {
      await ProductService.updateProduct(product.id, product)
      notify('Cập nhật sản phẩm thành công!', 'success')
    } else {
      await ProductService.createProduct(product)
      notify('Thêm sản phẩm mới thành công!', 'success')
    }
    closeFormModal()
    loadProducts()
  } catch (error) {
    notify(error.response?.data?.message || "Lưu sản phẩm thất bại!", 'danger')
  }
}

const openDeleteModal = (productOrId) => {
  if (typeof productOrId === 'object' && productOrId !== null) {
    productToDelete.value = productOrId
  } else {
    productToDelete.value = rawProducts.value.find(p => p.id === productOrId) || { id: productOrId }
  }
  showDeleteModal.value = true
}

const confirmDeleteProduct = async () => {
  if (!productToDelete.value) return
  try {
    const id = productToDelete.value.id || productToDelete.value
    await ProductService.deleteProduct(id)
    notify('Đã xóa sản phẩm thành công!', 'success')
    loadProducts()
  } catch (error) {
    notify(error.response?.data?.message || "Xóa sản phẩm thất bại do vướng dữ liệu khóa ngoại!", 'danger')
  } finally {
    showDeleteModal.value = false
    productToDelete.value = null
  }
}

onMounted(() => {
  loadProducts()
})
</script>

<style scoped>
.card { border-radius: 12px; }
.product-page { background: #f8f9fa; min-height: 100vh; padding: 25px; }
</style>