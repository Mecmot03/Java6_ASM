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

    <!-- Thanh tìm kiếm -->
    <div class="card shadow-sm border-0 mb-4">
      <div class="card-body">
        <div class="row g-2">
          <div class="col-md-8">
            <input
              v-model="keyword"
              type="text"
              class="form-control"
              placeholder="Nhập tên, danh mục hoặc thương hiệu..."
              @keyup.enter="searchProduct"
            >
          </div>
          <div class="col-md-2">
            <button class="btn btn-primary w-100" @click="searchProduct">
              <i class="bi bi-search me-1"></i> Tìm
            </button>
          </div>
          <div class="col-md-2">
            <button class="btn btn-secondary w-100" @click="loadProducts">
              Làm mới
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Bảng Sản phẩm -->
    <ProductTable
      :products="products"
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

    <!-- Modal Xác nhận xóa -->
    <DeleteModal
      :show="showDeleteModal"
      title="Xác nhận xóa Sản phẩm"
      :message="`Bạn có chắc chắn muốn xóa sản phẩm '${productToDelete?.name || ''}' không?`"
      @close="closeDeleteModal"
      @confirm="confirmDeleteProduct"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { notify } from '../../utils/notify'
import ProductService from '../../services/ProductService'
import ProductTable from '../../components/admin/ProductTable.vue'
import ProductForm from '../../components/admin/ProductForm.vue'
import DeleteModal from '../../components/admin/DeleteModal.vue'

const products = ref([])
const allProducts = ref([])
const keyword = ref('')
const selectedProduct = ref({})

const showFormModal = ref(false)
const showDeleteModal = ref(false)
const productToDelete = ref(null)

const loadProducts = async () => {
  keyword.value = ''
  try {
    const data = await ProductService.getAllProducts()
    allProducts.value = data || []
    products.value = [...allProducts.value]
  } catch (error) {
    console.error("Lỗi tải danh sách sản phẩm:", error)
  }
}

const searchProduct = () => {
  const kw = keyword.value.trim().toLowerCase()
  if (!kw) {
    products.value = [...allProducts.value]
    return
  }
  products.value = allProducts.value.filter(product =>
    (product.name && product.name.toLowerCase().includes(kw)) ||
    (product.brand && product.brand.toLowerCase().includes(kw)) ||
    (product.category?.name && product.category.name.toLowerCase().includes(kw))
  )
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

const saveProduct = async (productData) => {
  try {
    if (productData.id) {
      await ProductService.updateProduct(productData.id, productData)
      notify('Cập nhật sản phẩm thành công!', 'success')
    } else {
      await ProductService.createProduct(productData)
      notify('Thêm sản phẩm mới thành công!', 'success')
    }
    closeFormModal()
    await loadProducts()
  } catch (error) {
    console.error("Lỗi lưu sản phẩm:", error)
    notify(error.response?.data?.message || error.response?.data || "Lưu sản phẩm thất bại!", 'danger')
  }
}

const openDeleteModal = (productOrId) => {
  if (typeof productOrId === 'object' && productOrId !== null) {
    productToDelete.value = productOrId
  } else {
    productToDelete.value = allProducts.value.find(p => p.id === productOrId) || { id: productOrId }
  }
  showDeleteModal.value = true
}

const closeDeleteModal = () => {
  showDeleteModal.value = false
  productToDelete.value = null
}

// 🔴 XỬ LÝ XÓA VÀ LUÔN TẢI LẠI BẢNG KHI XÓA/ẨN SẢN PHẨM
const confirmDeleteProduct = async () => {
  if (!productToDelete.value) return

  const targetId = typeof productToDelete.value === 'object' ? productToDelete.value.id : productToDelete.value

  try {
    await ProductService.deleteProduct(targetId)
    notify('Đã xóa sản phẩm thành công!', 'success')
  } catch (error) {
    console.error("Lỗi khi xóa sản phẩm:", error)
    const errorMsg = error.response?.data?.message || error.response?.data || "Xóa sản phẩm thất bại!"
    notify(typeof errorMsg === 'string' ? errorMsg : "Xóa sản phẩm thất bại do vướng dữ liệu!", 'warning')
  } finally {
    closeDeleteModal()
    await loadProducts() // 🔴 Luôn làm mới danh sách bảng sản phẩm
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