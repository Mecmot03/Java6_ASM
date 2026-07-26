<template>
  <div class="container py-4">
    <!-- BANNER SLIDER -->
    <div id="mainBannerCarousel" class="carousel slide mb-4 rounded-4 overflow-hidden shadow-sm border-0" data-bs-ride="carousel" data-bs-interval="4000">
      <div class="carousel-indicators mb-2">
        <button type="button" data-bs-target="#mainBannerCarousel" data-bs-slide-to="0" class="active custom-indicator"></button>
        <button type="button" data-bs-target="#mainBannerCarousel" data-bs-slide-to="1" class="custom-indicator"></button>
      </div>

      <div class="carousel-inner">
        <div class="carousel-item active">
          <router-link to="/">
            <img src="/images/thoai.png" class="d-block w-100 banner-img" alt="Khuyến mãi Điện thoại" />
          </router-link>
        </div>
        <div class="carousel-item">
          <router-link to="/">
            <img src="/images/deal.jpg" class="d-block w-100 banner-img" alt="Deal hot giá sốc" />
          </router-link>
        </div>
      </div>

      <button class="carousel-control-prev" type="button" data-bs-target="#mainBannerCarousel" data-bs-slide="prev">
        <span class="carousel-control-prev-icon rounded-circle p-3 bg-dark bg-opacity-50" aria-hidden="true"></span>
      </button>
      <button class="carousel-control-next" type="button" data-bs-target="#mainBannerCarousel" data-bs-slide="next">
        <span class="carousel-control-next-icon rounded-circle p-3 bg-dark bg-opacity-50" aria-hidden="true"></span>
      </button>
    </div>

    <!-- BỘ LỌC THƯƠNG HIỆU, SẮP XẾP & KHOẢNG GIÁ -->
    <div 
      class="filter-section mb-4 p-3 p-md-4 bg-white rounded-4 shadow-sm border-0"
      :class="{ 'filter-sticky-show': isScrollingUp }"
    >
      <div class="d-flex align-items-center gap-2 overflow-auto pb-3 border-bottom no-scrollbar">
        <span class="fw-bold text-dark me-2 flex-shrink-0 small text-uppercase tracking-wider">
          <i class="bi bi-tags-fill me-1 text-warning"></i>Thương hiệu:
        </span>
        <button 
          v-for="brand in availableBrands" 
          :key="brand"
          class="btn btn-filter btn-sm rounded-pill flex-shrink-0 px-3 py-1 fw-medium"
          :class="selectedBrand === brand ? 'active' : ''" 
          @click="selectBrand(brand)"
        >
          {{ brand }}
        </button>
      </div>

      <div class="d-flex align-items-center gap-3 pt-3 flex-wrap">
        <span class="fw-bold text-dark me-1 small text-uppercase tracking-wider flex-shrink-0">
          <i class="bi bi-sort-down me-1 text-warning"></i>Sắp xếp:
        </span>

        <div class="sort-pill-group p-1 bg-light rounded-pill d-inline-flex border flex-shrink-0">
          <button 
            class="btn btn-sort-pill btn-sm rounded-pill px-3 fw-medium"
            :class="sortBy === 'default' ? 'active' : ''" 
            @click="selectSort('default')"
          >
            Nổi bật
          </button>
          <button 
            class="btn btn-sort-pill btn-sm rounded-pill px-3 fw-medium"
            :class="sortBy === 'newest' ? 'active' : ''" 
            @click="selectSort('newest')"
          >
            Mới nhất
          </button>
        </div>

        <div class="flex-shrink-0">
          <select 
            class="form-select form-select-sm rounded-pill ps-3 pe-5 py-1 border-light-subtle bg-light shadow-none fw-medium text-secondary custom-price-select"
            v-model="priceSort" 
            @change="onPriceSortChange"
          >
            <option value="">Giá: Mặc định</option>
            <option value="price_asc">Giá: Thấp đến Cao</option>
            <option value="price_desc">Giá: Cao đến Thấp</option>
          </select>
        </div>

        <!-- BỘ LỌC KHOẢNG GIÁ 2 ĐẦU -->
        <div class="price-filter-box d-flex align-items-center gap-2 bg-light px-3 py-1 rounded-pill border flex-shrink-0">
          <i class="bi bi-funnel-fill text-warning"></i>
          <span class="small fw-bold text-nowrap">Khoảng giá:</span>
          
          <div class="range-slider-container mx-2">
            <div class="slider-track" :style="trackStyle"></div>
            <input 
              type="range" 
              min="0" 
              max="100000000" 
              step="500000" 
              v-model.number="tempMinPrice" 
              @input="enforceMinPrice"
              class="dual-range" 
            />
            <input 
              type="range" 
              min="0" 
              max="100000000" 
              step="500000" 
              v-model.number="tempMaxPrice" 
              @input="enforceMaxPrice"
              class="dual-range" 
            />
          </div>

          <span class="badge bg-warning text-dark font-monospace price-badge">
            {{ formatCurrency(tempMinPrice) }} - {{ formatCurrency(tempMaxPrice) }}
          </span>

          <button class="btn btn-dark btn-sm rounded-pill px-3 py-1 fs-7 fw-bold" @click="applyPriceFilter">
            Áp dụng
          </button>
        </div>

      </div>
    </div>

    <!-- DANH SÁCH SẢN PHẨM (THIẾT KẾ MỚI VỚI NGUYÊN NĂNG LỰC FILTER) -->
    <div class="row g-4">
      
      <!-- TIÊU ĐỀ KHỐI SẢN PHẨM HIỆN ĐẠI -->
      <div class="col-12">
        <div class="d-flex align-items-center justify-content-between p-3 bg-white rounded-4 shadow-sm border border-light">
          <div class="d-flex align-items-center gap-3">
            <div class="section-badge-icon bg-warning text-dark rounded-circle d-flex align-items-center justify-content-center shadow-sm">
              <i class="bi bi-lightning-charge-fill fs-5"></i>
            </div>
            <div>
              <h4 class="fw-extrabold text-dark mb-0 text-uppercase tracking-tight">
                <span v-if="keyword">Kết quả tìm kiếm: <span class="text-danger">"{{ keyword }}"</span></span>
                <span v-else-if="selectedCategoryName">Danh mục: <span class="text-primary">{{ selectedCategoryName }}</span></span>
                <span v-else>Sản Phẩm Nổi Bật</span>
              </h4>
              <p class="text-muted small mb-0">Cam kết chính hãng - Đổi trả tận nơi - Bảo hành 12 tháng</p>
            </div>
          </div>

          <span class="badge bg-dark text-warning rounded-pill px-3 py-2 fw-bold fs-7 shadow-sm">
            <i class="bi bi-box-seam me-1"></i> {{ filteredProducts.length }} Sản phẩm
          </span>
        </div>
      </div>

      <!-- KHÔNG TÌM THẤY SẢN PHẨM -->
      <div v-if="filteredProducts.length === 0" class="col-12 text-center py-5 my-3 bg-white rounded-4 shadow-sm">
        <i class="bi bi-search-heart display-1 text-warning opacity-50 d-block mb-3"></i>
        <h5 class="fw-bold text-dark">Rất tiếc, không tìm thấy sản phẩm phù hợp!</h5>
        <p class="text-muted small mb-0">Hãy thử điều chỉnh lại khoảng giá hoặc tìm kiếm từ khóa khác xem sao nhé.</p>
      </div>

      <!-- PRODUCT CARD THIẾT KẾ MỚI -->
      <div v-for="product in filteredProducts" :key="product.id || product.Id" class="col-12 col-sm-6 col-md-4 col-lg-3">
        <div class="card h-100 shadow-sm border-0 rounded-4 product-card position-relative overflow-hidden bg-white">
          
          <!-- KHU VỰC ẢNH SẢN PHẨM -->
          <div class="product-img-wrapper p-3 position-relative text-center bg-light rounded-top-4" @click="viewDetail(product.id || product.Id)">
            <!-- BADGE GIẢM GIÁ / MỚI -->
            <span v-if="product.discountId || product.DiscountId" class="badge bg-danger position-absolute top-0 start-0 m-3 px-2 py-1 rounded-pill shadow-sm fs-8">
              <i class="bi bi-fire me-1"></i>GIẢM GIÁ
            </span>

            <img :src="getProductImage(product)" class="img-fluid product-img-zoom" :alt="getProductName(product)">
            
            <!-- NÚT XEM NHANH KHI HOVER -->
            <div class="hover-overlay position-absolute top-0 start-0 w-100 h-100 d-flex align-items-center justify-content-center bg-dark bg-opacity-10">
              <span class="btn btn-sm btn-light rounded-pill px-3 shadow fw-bold text-dark">
                <i class="bi bi-eye-fill me-1 text-warning"></i> Xem chi tiết
              </span>
            </div>
          </div>

          <!-- THÔNG TIN SẢN PHẨM -->
          <div class="card-body p-3 d-flex flex-column justify-content-between">
            <div>
              <div class="d-flex align-items-center justify-content-between mb-1">
                <span class="badge bg-light text-secondary border rounded-pill fs-8">{{ getProductBrand(product) }}</span>
                
              </div>

              <h6 class="card-title fw-bold text-dark text-truncate mb-2" :title="getProductName(product)" @click="viewDetail(product.id || product.Id)" style="cursor: pointer;">
                {{ getProductName(product) }}
              </h6>
            </div>

            <div class="mt-2">
              <div class="d-flex align-items-baseline gap-2 mb-3">
                <h5 class="text-danger fw-bold mb-0 fs-5">{{ formatPrice(product.price ?? product.Price) }}</h5>
              </div>

              <!-- NÚT MUA HÀNG PHỦ ĐẦY CHIỀU NGANG -->
              <button 
                class="btn btn-warning w-100 rounded-pill fw-bold text-dark py-2 shadow-sm d-flex align-items-center justify-content-center gap-2 btn-add-cart"
                @click="addToCart(product)"
              >
                <i class="bi bi-cart-plus-fill fs-6"></i>
                <span>Thêm vào giỏ</span>
              </button>
            </div>
          </div>

        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import axios from 'axios'
import { addGuestCartItem } from '../utils/cart'
import { confirmDialog } from '../utils/dialog'
import { notify } from '../utils/notify'

const router = useRouter()
const route = useRoute()

const products = ref([])
const keyword = ref('')
const selectedCategoryId = ref(null)
const selectedCategoryName = ref('')
const selectedBrand = ref('')
const sortBy = ref('default')
const priceSort = ref('')

// Quản lý khoảng giá 2 đầu
const tempMinPrice = ref(0)
const tempMaxPrice = ref(100000000)
const appliedMinPrice = ref(0)
const appliedMaxPrice = ref(100000000)
const MAX_ALLOWED_PRICE = 100000000

const availableBrands = ref(['Apple', 'Samsung', 'Xiaomi', 'Dell', 'Logitech', 'Sony', 'Razer', 'Asus', 'Anker'])

// Quản lý trạng thái cuộn mượt bằng requestAnimationFrame
const isScrollingUp = ref(false)
let lastScrollPos = 0
let ticking = false

const handleHomeScroll = () => {
  if (!ticking) {
    window.requestAnimationFrame(() => {
      const currentScroll = window.scrollY || document.documentElement.scrollTop
      
      if (currentScroll <= 120) {
        isScrollingUp.value = false
      } else {
        const diff = currentScroll - lastScrollPos
        if (Math.abs(diff) > 10) {
          isScrollingUp.value = diff < 0
          lastScrollPos = currentScroll
        }
      }
      ticking = false
    })
    ticking = true
  }
}

// Đảm bảo nút Min không vượt nút Max
const enforceMinPrice = () => {
  if (tempMinPrice.value > tempMaxPrice.value - 500000) {
    tempMinPrice.value = tempMaxPrice.value - 500000
  }
}

// Đảm bảo nút Max không bé hơn nút Min
const enforceMaxPrice = () => {
  if (tempMaxPrice.value < tempMinPrice.value + 500000) {
    tempMaxPrice.value = tempMinPrice.value + 500000
  }
}

// Tính dải màu vàng hiển thị ở giữa 2 nút kéo
const trackStyle = computed(() => {
  const minPercent = (tempMinPrice.value / MAX_ALLOWED_PRICE) * 100
  const maxPercent = (tempMaxPrice.value / MAX_ALLOWED_PRICE) * 100
  return {
    background: `linear-gradient(to right, #dee2e6 ${minPercent}%, #ffd400 ${minPercent}%, #ffd400 ${maxPercent}%, #dee2e6 ${maxPercent}%)`
  }
})

const formatCurrency = (value) => {
  if (!value || value === 0) return '0đ'
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value)
}

const isAuthError = (error) => {
  const status = error?.response?.status
  return status === 401 || status === 403
}

// Lọc sản phẩm theo khoảng giá Min - Max đã áp dụng
const filteredProducts = computed(() => {
  return products.value.filter(product => {
    const price = Number(product.price ?? product.Price ?? 0)
    if (price === 0) return true
    return price >= appliedMinPrice.value && price <= appliedMaxPrice.value
  })
})

const fetchFilteredProducts = async () => {
  try {
    const params = {}

    if (keyword.value && keyword.value.trim() !== '') {
      params.keyword = keyword.value.trim()
    }
    if (selectedCategoryId.value) {
      params.categoryId = selectedCategoryId.value
    }
    if (selectedBrand.value && selectedBrand.value.trim() !== '') {
      params.brand = selectedBrand.value.trim()
    }
    if (sortBy.value && sortBy.value !== 'default') {
      params.sortBy = sortBy.value
    }
    
    const response = await axios.get('/api/products/filter', { params })

    if (Array.isArray(response.data)) {
      products.value = response.data
    } else if (response.data && response.data.content) {
      products.value = response.data.content
    } else if (response.data && response.data.data) {
      products.value = response.data.data
    } else {
      products.value = []
    }

  } catch (error) {
    if (!isAuthError(error)) {
      console.error("Lỗi tải sản phẩm:", error)
    }
    products.value = []
  }
}

const fetchCategoryName = async (catId) => {
  if (!catId) {
    selectedCategoryName.value = ''
    return
  }
  try {
    const res = await axios.get('/api/categories')
    const found = res.data.find(c => (c.id || c.Id) == catId)
    if (found) selectedCategoryName.value = found.name || found.Name
  } catch (e) {
    if (!isAuthError(e)) {
      console.error("Lỗi lấy thông tin danh mục:", e)
    }
    selectedCategoryName.value = ''
  }
}

const getProductName = (p) => p.name || p.Name || 'Sản phẩm'
const getProductBrand = (p) => p.brand || p.Brand || 'Khác'
const getProductImage = (p) => {
  const img = p.image || p.Image
  return img ? '/images/' + img : 'https://via.placeholder.com/200'
}

watch(
  () => [route.query.keyword, route.query.categoryId],
  async ([newKeyword, newCatId]) => {
    keyword.value = newKeyword ? newKeyword.trim() : ''
    selectedCategoryId.value = newCatId || null
    selectedBrand.value = '' 
    
    if (!newCatId) {
      selectedCategoryName.value = ''
    } else {
      await fetchCategoryName(newCatId)
    }
    
    fetchFilteredProducts()
  },
  { immediate: true }
)

const selectBrand = (brand) => {
  selectedBrand.value = selectedBrand.value === brand ? '' : brand
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

const applyPriceFilter = () => {
  appliedMinPrice.value = tempMinPrice.value
  appliedMaxPrice.value = tempMaxPrice.value
}

const formatPrice = (price) => {
  if (price === undefined || price === null || isNaN(price)) return '0 đ'
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price)
}

const viewDetail = (id) => {
  router.push('/product/' + id)
}

// Thêm vào giỏ hàng thật vào DB Spring Boot
const addToCart = async (product) => {
  try {
    const userStorage = localStorage.getItem('user')
    const productId = product.id || product.Id

    if (!userStorage) {
      addGuestCartItem(product, 1)
      window.dispatchEvent(new CustomEvent('cart-updated'))
      notify('Đã thêm sản phẩm vào giỏ hàng tạm.', 'success')

      if (await confirmDialog('Đã thêm sản phẩm vào giỏ hàng tạm. Bạn có muốn xem giỏ hàng ngay không?')) {
        router.push('/cart')
      }
      return
    }

    const userId = JSON.parse(userStorage).id

    await axios.post(`/api/cart/add?userId=${userId}`, {
      productId: productId,
      quantity: 1
    })

    window.dispatchEvent(new CustomEvent('cart-updated'))

    if (await confirmDialog("Đã thêm sản phẩm vào giỏ hàng! Bạn có muốn đến trang Giỏ hàng ngay không?")) {
      router.push('/cart')
    }
  } catch (error) {
    console.error("Lỗi thêm vào giỏ hàng:", error)
    notify(error.response?.data?.message || "Không thể thêm vào giỏ hàng. Vui lòng kiểm tra lại Backend!", 'danger')
  }
}

onMounted(() => {
  window.addEventListener('scroll', handleHomeScroll, { passive: true })
  fetchFilteredProducts()
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleHomeScroll)
})
</script>

<style scoped>
#mainBannerCarousel { background-color: #f8f9fa; cursor: pointer; }
.banner-img { object-fit: cover; transition: transform 0.3s ease; }
#mainBannerCarousel:hover .banner-img { transform: scale(1.01); }
.custom-indicator { width: 12px !important; height: 12px !important; border-radius: 50% !important; background-color: rgba(255, 255, 255, 0.7) !important; border: none !important; margin: 0 4px !important; }
.custom-indicator.active { background-color: #ffd400 !important; width: 28px !important; border-radius: 10px !important; }

/* BỘ LỌC STICKY */
.filter-section { 
  background: #ffffff; 
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03) !important; 
  border: 1px solid #f1f3f5 !important; 
  will-change: transform;
  transition: transform 0.28s cubic-bezier(0.16, 1, 0.3, 1), box-shadow 0.28s ease;
}

.filter-section.filter-sticky-show {
  position: sticky;
  top: 85px;
  z-index: 890;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1) !important;
}

.btn-filter { background-color: #f8f9fa; color: #495057; border: 1px solid #e9ecef; transition: all 0.25s ease; }
.btn-filter:hover { background-color: #e9ecef; color: #212529; }
.btn-filter.active { background-color: #000000; color: #ffd400; border-color: #000000; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15); }
.sort-pill-group { background-color: #f1f3f5 !important; }
.btn-sort-pill { color: #6c757d; border: none; transition: all 0.2s ease; }
.btn-sort-pill.active { background-color: #ffffff; color: #000000; box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08); }

/* DUAL RANGE SLIDER */
.range-slider-container {
  position: relative;
  width: 140px;
  height: 24px;
  display: flex;
  align-items: center;
}

.slider-track {
  position: absolute;
  width: 100%;
  height: 6px;
  border-radius: 4px;
  z-index: 1;
}

.dual-range {
  position: absolute;
  width: 100%;
  -webkit-appearance: none;
  appearance: none;
  background: none;
  pointer-events: none;
  z-index: 2;
  margin: 0;
}

.dual-range::-webkit-slider-thumb {
  -webkit-appearance: none;
  appearance: none;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: #0d6efd;
  cursor: pointer;
  pointer-events: auto;
  border: 2px solid #ffffff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.4);
  transition: transform 0.1s;
}

.dual-range::-webkit-slider-thumb:hover { transform: scale(1.15); }

.price-filter-box { border-color: #dee2e6 !important; }
.price-badge { font-size: 11.5px; min-width: 165px; text-align: center; letter-spacing: -0.2px; }

/* TIÊU ĐỀ SECTION MỚI */
.section-badge-icon { width: 42px; height: 42px; }
.fw-extrabold { font-weight: 800; }
.tracking-tight { letter-spacing: -0.02em; }

/* PRODUCT CARD MỚI HIỆN ĐẠI */
.product-card { 
  transition: transform 0.28s ease, box-shadow 0.28s ease; 
  border: 1px solid #f1f3f5 !important; 
}
.product-card:hover { 
  transform: translateY(-8px); 
  box-shadow: 0 16px 32px rgba(0, 0, 0, 0.08) !important; 
}

.product-img-wrapper { 
  height: 200px; 
  cursor: pointer; 
  display: flex; 
  align-items: center; 
  justify-content: center; 
  overflow: hidden;
}

.product-img-zoom { 
  max-height: 100%; 
  object-fit: contain; 
  transition: transform 0.35s ease; 
}

.product-card:hover .product-img-zoom { 
  transform: scale(1.08); 
}

/* OVERLAY HOVER HIỆN NÚT XEM CHI TIẾT */
.hover-overlay {
  opacity: 0;
  transition: opacity 0.25s ease;
}
.product-card:hover .hover-overlay {
  opacity: 1;
}

.btn-add-cart {
  transition: background-color 0.2s, transform 0.15s ease;
}
.btn-add-cart:hover {
  background-color: #e6be00 !important;
  transform: translateY(-1px);
}

.no-scrollbar::-webkit-scrollbar { display: none; }
.no-scrollbar { -ms-overflow-style: none; scrollbar-width: none; }
.tracking-wider { letter-spacing: 0.03em; }
.fs-7 { font-size: 12px; }
.fs-8 { font-size: 11px; }
</style>