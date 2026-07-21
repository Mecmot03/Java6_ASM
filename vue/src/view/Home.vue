<template>
  <div class="container py-4">
    <!-- BANNER SLIDER (CHUYỂN 2 ANH DEAL & THOAI) -->
    <div id="mainBannerCarousel" class="carousel slide mb-4 rounded-4 overflow-hidden shadow-sm border-0"
      data-bs-ride="carousel" data-bs-interval="4000">
      <!-- Nút chấm chuyển trang bên dưới -->
      <div class="carousel-indicators mb-2">
        <button type="button" data-bs-target="#mainBannerCarousel" data-bs-slide-to="0"
          class="active custom-indicator"></button>
        <button type="button" data-bs-target="#mainBannerCarousel" data-bs-slide-to="1"
          class="custom-indicator"></button>
      </div>

      <!-- Danh sách Banner -->
      <div class="carousel-inner">
        <!-- Banner 1: thoai.png -->
        <div class="carousel-item active">
          <router-link to="/">
            <img src="/images/thoai.png" class="d-block w-100 banner-img" alt="Khuyến mãi Điện thoại" />
          </router-link>
        </div>

        <!-- Banner 2: deal.jpg -->
        <div class="carousel-item">
          <router-link to="/">
            <img src="/images/deal.jpg" class="d-block w-100 banner-img" alt="Deal hot giá sốc" />
          </router-link>
        </div>
      </div>

      <!-- Nút chuyển sang Trái / Phải -->
      <button class="carousel-control-prev" type="button" data-bs-target="#mainBannerCarousel" data-bs-slide="prev">
        <span class="carousel-control-prev-icon rounded-circle p-3 bg-dark bg-opacity-50" aria-hidden="true"></span>
      </button>
      <button class="carousel-control-next" type="button" data-bs-target="#mainBannerCarousel" data-bs-slide="next">
        <span class="carousel-control-next-icon rounded-circle p-3 bg-dark bg-opacity-50" aria-hidden="true"></span>
      </button>
    </div>
    <!-- KHU VỰC BỘ LỌC THƯƠNG HIỆU & SẮP XẾP -->
    <div class="filter-section mb-4 p-3 p-md-4 bg-white rounded-4 shadow-sm border-0">

      <!-- Hàng 1: Nút lọc theo Thương hiệu -->
      <div class="d-flex align-items-center gap-2 overflow-auto pb-3 border-bottom no-scrollbar">
        <span class="fw-bold text-dark me-2 flex-shrink-0 small text-uppercase tracking-wider">
          <i class="bi bi-tags-fill me-1 text-warning"></i>Thương hiệu:
        </span>
        <!-- <button 
          class="btn btn-filter btn-sm rounded-pill flex-shrink-0 px-3 py-1 fw-medium" 
          :class="selectedBrand === '' ? 'active' : ''"
          @click="selectBrand('')"
        >
          Tất cả
        </button> -->
        <button v-for="brand in availableBrands" :key="brand"
          class="btn btn-filter btn-sm rounded-pill flex-shrink-0 px-3 py-1 fw-medium"
          :class="selectedBrand === brand ? 'active' : ''" @click="selectBrand(brand)">
          {{ brand }}
        </button>
      </div>

      <!-- Hàng 2: Lựa chọn Sắp xếp (Đã gộp về chung 1 bên) -->
      <div class="d-flex align-items-center gap-2 pt-3 flex-wrap">
        <span class="fw-bold text-dark me-1 small text-uppercase tracking-wider flex-shrink-0">
          <i class="bi bi-sort-down me-1 text-warning"></i>Sắp xếp:
        </span>

        <!-- Cụm nút Nổi bật / Mới nhất -->
        <div class="sort-pill-group p-1 bg-light rounded-pill d-inline-flex border flex-shrink-0">
          <button class="btn btn-sort-pill btn-sm rounded-pill px-3 fw-medium"
            :class="sortBy === 'default' ? 'active' : ''" @click="selectSort('default')">
            Nổi bật
          </button>
          <button class="btn btn-sort-pill btn-sm rounded-pill px-3 fw-medium"
            :class="sortBy === 'newest' ? 'active' : ''" @click="selectSort('newest')">
            Mới nhất
          </button>
        </div>

        <!-- Ô chọn Giá đặt ngay sát bên cạnh -->
        <div class="flex-shrink-0">
          <select
            class="form-select form-select-sm rounded-pill ps-3 pe-5 py-1 border-light-subtle bg-light shadow-none fw-medium text-secondary custom-price-select"
            v-model="priceSort" @change="onPriceSortChange">
            <option value="">Giá: Mặc định</option>
            <option value="price_asc">Giá: Thấp đến Cao</option>
            <option value="price_desc">Giá: Cao đến Thấp</option>
          </select>
        </div>
      </div>

    </div>

    <!-- TIÊU ĐỀ THAY ĐỔI ĐỘNG -->
    <div class="row g-4">
      <div class="col-12 d-flex align-items-center justify-content-between">
        <h4 class="fw-bold text-dark mb-0 position-relative section-title">
          <span v-if="keyword">Kết quả tìm kiếm: <span class="text-black">"{{ keyword }}"</span></span>
          <span v-else-if="selectedCategoryName">Danh mục: <span class="text-black">{{ selectedCategoryName
          }}</span></span>
          <span v-else>Tất cả sản phẩm</span>
        </h4>
        <span class="badge bg-light text-dark border rounded-pill px-3 py-2 fw-normal">
          {{ products.length }} sản phẩm
        </span>
      </div>

      <div v-if="products.length === 0" class="col-12 text-center py-5 my-3 bg-white rounded-4 shadow-sm">
        <i class="bi bi-box-seam display-1 text-muted opacity-25 d-block mb-3"></i>
        <p class="text-muted fs-5 fw-medium mb-0">Không tìm thấy sản phẩm nào phù hợp!</p>
      </div>

      <!-- DANH SÁCH SẢN PHẨM -->
      <div v-for="product in products" :key="product.id" class="col-12 col-sm-6 col-md-4 col-lg-3">
        <div class="card h-100 shadow-sm border-0 rounded-4 product-card overflow-hidden">
          <div class="product-img-container p-3 d-flex align-items-center justify-content-center position-relative">
            <span v-if="product.discountId"
              class="badge bg-danger rounded-pill position-absolute top-0 start-0 m-3 px-2 py-1">
              Giảm giá
            </span>
            <img :src="product.image ? '/images/' + product.image : 'https://via.placeholder.com/200'"
              class="card-img-top product-hover-zoom" :alt="product.name">
          </div>

          <div class="card-body d-flex flex-column justify-content-between pt-2 px-3 pb-3">
            <div>
              <div class="mb-1">
                <span class="badge bg-light text-secondary border rounded-pill small">{{ product.brand }}</span>
              </div>
              <h6 class="card-title fw-bold text-dark text-truncate mb-2" :title="product.name">
                {{ product.name }}
              </h6>
            </div>

            <div>
              <h5 class="text-danger fw-bold mb-3">{{ formatPrice(product.price) }}</h5>
              <div class="d-flex gap-2">
                <button class="btn btn-light btn-sm flex-grow-1 rounded-pill fw-medium text-secondary"
                  @click="viewDetail(product.id)">
                  <i class="bi bi-eye"></i> Xem
                </button>
                <button class="btn btn-warning btn-sm flex-grow-1 rounded-pill fw-bold text-dark"
                  @click="addToCart(product.id)">
                  <i class="bi bi-cart-plus me-1"></i> Mua
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const route = useRoute()

const products = ref([])
const keyword = ref('')
const selectedCategoryId = ref(null)
const selectedCategoryName = ref('')
const selectedBrand = ref('')
const sortBy = ref('default')
const priceSort = ref('')

const availableBrands = ref(['Apple', 'Samsung', 'Xiaomi', 'Dell', 'Logitech', 'Sony', 'Razer', 'Asus', 'Anker'])

const fetchFilteredProducts = async () => {
  try {
    const params = {}
    if (keyword.value.trim()) params.keyword = keyword.value.trim()
    if (selectedCategoryId.value) params.categoryId = selectedCategoryId.value
    if (selectedBrand.value) params.brand = selectedBrand.value
    if (sortBy.value) params.sortBy = sortBy.value

    const response = await axios.get('http://localhost:8080/api/products/filter', { params })
    products.value = response.data
  } catch (error) {
    console.error("Lỗi tải sản phẩm:", error)
    products.value = []
  }
}

const fetchCategoryName = async (catId) => {
  if (!catId) {
    selectedCategoryName.value = ''
    return
  }
  try {
    const res = await axios.get('http://localhost:8080/api/categories')
    const found = res.data.find(c => c.id == catId)
    if (found) selectedCategoryName.value = found.name
  } catch (e) {
    console.error("Lỗi lấy thông tin danh mục:", e)
  }
}

watch(
  () => [route.query.keyword, route.query.categoryId],
  async ([newKeyword, newCatId]) => {
    keyword.value = newKeyword || ''
    selectedCategoryId.value = newCatId || null
    selectedBrand.value = ''

    if (newCatId) {
      await fetchCategoryName(newCatId)
    } else {
      selectedCategoryName.value = ''
    }

    fetchFilteredProducts()
  },
  { immediate: true }
)

const selectBrand = (brand) => {
  selectedBrand.value = brand
  fetchFilteredProducts()
}

const selectSort = (type) => {
  priceSort.value = ''
  sortBy.value = type
  fetchFilteredProducts()
}

const onPriceSortChange = () => {
  if (priceSort.value) {
    sortBy.value = priceSort.value
    fetchFilteredProducts()
  }
}

const formatPrice = (price) => {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price)
}

const viewDetail = (id) => {
  router.push('/product/' + id)
}

const addToCart = (productId) => {
  alert(`Đã thêm sản phẩm ID: ${productId} vào giỏ hàng!`)
}

onMounted(() => {
  fetchFilteredProducts()
})
</script>

<style scoped>
/* Custom Banner Carousel */
#mainBannerCarousel {
  background-color: #f8f9fa;
  cursor: pointer;
}

.banner-img {
  object-fit: cover;
  transition: transform 0.3s ease;
}

#mainBannerCarousel:hover .banner-img {
  transform: scale(1.01);
}

/* Tùy chỉnh thanh chỉ số (dots) bên dưới banner */
.custom-indicator {
  width: 12px !important;
  height: 12px !important;
  border-radius: 50% !important;
  background-color: rgba(255, 255, 255, 0.7) !important;
  border: none !important;
  margin: 0 4px !important;
}

.custom-indicator.active {
  background-color: #ffd400 !important; /* Màu vàng thương hiệu TGDĐ */
  width: 28px !important;
  border-radius: 10px !important;
}

/* Bo góc mượt & Shadow cho Filter Container */
.filter-section {
  background: #ffffff;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03) !important;
  border: 1px solid #f1f3f5 !important;
}

/* Custom nút Thương hiệu */
.btn-filter {
  background-color: #f8f9fa;
  color: #495057;
  border: 1px solid #e9ecef;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

.btn-filter:hover {
  background-color: #e9ecef;
  color: #212529;
}

.btn-filter.active {
  background-color: #000000;
  color: #ffd400;
  border-color: #000000;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

/* Segmented Control cho Sắp xếp */
.sort-pill-group {
  background-color: #f1f3f5 !important;
}

.btn-sort-pill {
  color: #6c757d;
  border: none;
  transition: all 0.2s ease;
}

.btn-sort-pill.active {
  background-color: #ffffff;
  color: #000000;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
}

/* Product Card Styling */
.product-card {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid #f1f3f5 !important;
}

.product-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.08) !important;
}

.product-img-container {
  height: 200px;
  background-color: #fafafa;
}

.product-hover-zoom {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
  transition: transform 0.3s ease;
}

.product-card:hover .product-hover-zoom {
  transform: scale(1.06);
}

/* Utility Class giấu thanh cuộn ngang */
.no-scrollbar::-webkit-scrollbar {
  display: none;
}

.no-scrollbar {
  -ms-overflow-style: none;
  scrollbar-width: none;
}

.tracking-wider {
  letter-spacing: 0.03em;
}
</style>