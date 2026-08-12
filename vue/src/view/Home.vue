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

    <!-- DANH SÁCH SẢN PHẨM -->
    <div class="row g-4">
      
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
            <i class="bi bi-box-seam me-1"></i> {{ products.length }} Sản phẩm
          </span>
        </div>
      </div>

      <!-- KHÔNG TÌM THẤY SẢN PHẨM -->
      <div v-if="products.length === 0" class="col-12 text-center py-5 my-3 bg-white rounded-4 shadow-sm">
        <i class="bi bi-search-heart display-1 text-warning opacity-50 d-block mb-3"></i>
        <h5 class="fw-bold text-dark">Rất tiếc, không tìm thấy sản phẩm phù hợp!</h5>
        <p class="text-muted small mb-0">Hãy thử điều chỉnh lại khoảng giá hoặc tìm kiếm từ khóa khác xem sao nhé.</p>
      </div>

      <!-- PRODUCT CARD -->
      <div v-for="product in products" :key="product.id" class="col-12 col-sm-6 col-md-4 col-lg-3">
        <div 
          class="card h-100 shadow-sm border-0 rounded-4 product-card position-relative overflow-hidden bg-white"
          :class="{ 'out-of-stock-card': product.quantity <= 0 }"
        >
          
          <div class="product-img-wrapper p-3 position-relative text-center bg-light rounded-top-4" @click="viewDetail(product.id)">
            <!-- Hết hàng Badge -->
            <span v-if="product.quantity <= 0" class="badge bg-secondary position-absolute top-0 start-0 m-3 px-2 py-1 rounded-pill shadow-sm fs-8 z-2">
              <i class="bi bi-x-circle me-1"></i>HẾT HÀNG
            </span>
            <span v-else-if="product.discountId" class="badge bg-danger position-absolute top-0 start-0 m-3 px-2 py-1 rounded-pill shadow-sm fs-8 z-2">
              <i class="bi bi-fire me-1"></i>GIẢM GIÁ
            </span>

            <img :src="getProductImage(product)" class="img-fluid product-img-zoom" :alt="product.name">
            
            <div class="hover-overlay position-absolute top-0 start-0 w-100 h-100 d-flex align-items-center justify-content-center bg-dark bg-opacity-10">
              <span class="btn btn-sm btn-light rounded-pill px-3 shadow fw-bold text-dark">
                <i class="bi bi-eye-fill me-1 text-warning"></i> Xem chi tiết
              </span>
            </div>
          </div>

          <div class="card-body p-3 d-flex flex-column justify-content-between">
            <div>
              <div class="d-flex align-items-center justify-content-between mb-1">
                <span class="badge bg-light text-secondary border rounded-pill fs-8">{{ product.brand || 'Khác' }}</span>
              </div>

              <h6 class="card-title fw-bold text-dark text-truncate mb-2" :title="product.name" @click="viewDetail(product.id)" style="cursor: pointer;">
                {{ product.name }}
              </h6>
            </div>

            <div class="mt-2">
              <div class="d-flex align-items-baseline gap-2 mb-3">
                <h5 class="text-danger fw-bold mb-0 fs-5">{{ formatPrice(product.price) }}</h5>
              </div>

              <!-- 🔴 Nút Thêm vào giỏ (Disabled nếu số lượng <= 0) -->
              <button 
                v-if="product.quantity > 0"
                class="btn btn-warning w-100 rounded-pill fw-bold text-dark py-2 shadow-sm d-flex align-items-center justify-content-center gap-2 btn-add-cart"
                @click="addToCart(product)"
              >
                <i class="bi bi-cart-plus-fill fs-6"></i>
                <span>Thêm vào giỏ</span>
              </button>
              <button 
                v-else
                class="btn btn-secondary w-100 rounded-pill fw-bold py-2 shadow-sm d-flex align-items-center justify-content-center gap-2"
                disabled
              >
                <i class="bi bi-slash-circle me-1"></i>
                <span>Tạm hết hàng</span>
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

const tempMinPrice = ref(0)
const tempMaxPrice = ref(100000000)
const appliedMinPrice = ref(0)
const appliedMaxPrice = ref(100000000)
const MAX_ALLOWED_PRICE = 100000000

const availableBrands = ref(['Apple', 'Samsung', 'Xiaomi', 'Dell', 'Logitech', 'Sony', 'Razer', 'Asus', 'Anker'])

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

const enforceMinPrice = () => {
  if (tempMinPrice.value > tempMaxPrice.value - 500000) {
    tempMinPrice.value = tempMaxPrice.value - 500000
  }
}

const enforceMaxPrice = () => {
  if (tempMaxPrice.value < tempMinPrice.value + 500000) {
    tempMaxPrice.value = tempMinPrice.value + 500000
  }
}

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

const resetFilters = () => {
  keyword.value = ''
  selectedCategoryId.value = null
  selectedCategoryName.value = ''
  selectedBrand.value = ''
  sortBy.value = 'default'
  priceSort.value = ''
  tempMinPrice.value = 0
  tempMaxPrice.value = MAX_ALLOWED_PRICE
  appliedMinPrice.value = 0
  appliedMaxPrice.value = MAX_ALLOWED_PRICE
}

const handleResetEvent = () => {
  resetFilters()
  fetchFilteredProducts()
}

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
    if (appliedMinPrice.value > 0) {
      params.minPrice = appliedMinPrice.value
    }
    if (appliedMaxPrice.value < MAX_ALLOWED_PRICE) {
      params.maxPrice = appliedMaxPrice.value
    }
    if (sortBy.value && sortBy.value !== 'default') {
      params.sortBy = sortBy.value
    }
    
    const response = await axios.get('/api/products/filter', { params })
    const resData = response.data
    products.value = Array.isArray(resData) ? resData : (resData.content || resData.data || [])

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
    const res = await axios.get(`/api/categories/${catId}`)
    selectedCategoryName.value = res.data?.name || ''
  } catch (e) {
    selectedCategoryName.value = ''
  }
}

const getProductImage = (p) => {
  const img = p.image
  return img ? '/images/' + img : 'https://via.placeholder.com/200'
}

watch(
  () => route.query,
  async (newQuery) => {
    if (!newQuery.keyword && !newQuery.categoryId) {
      resetFilters()
      fetchFilteredProducts()
    } else {
      keyword.value = newQuery.keyword ? newQuery.keyword.trim() : ''
      selectedCategoryId.value = newQuery.categoryId || null
      selectedBrand.value = '' 
      
      if (newQuery.categoryId) {
        await fetchCategoryName(newQuery.categoryId)
      } else {
        selectedCategoryName.value = ''
      }
      
      fetchFilteredProducts()
    }
  },
  { immediate: true, deep: true }
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
  fetchFilteredProducts()
}

const formatPrice = (price) => {
  if (price === undefined || price === null || isNaN(price)) return '0 đ'
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price)
}

const viewDetail = (id) => {
  router.push('/product/' + id)
}

const addToCart = async (product) => {
  if (product.quantity <= 0) {
    notify('Sản phẩm này tạm thời đã hết hàng!', 'warning')
    return
  }

  try {
    const userStorage = localStorage.getItem('user')
    const productId = product.id

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
    notify(error.response?.data?.message || "Không thể thêm vào giỏ hàng!", 'danger')
  }
}

onMounted(() => {
  window.addEventListener('scroll', handleHomeScroll, { passive: true })
  window.addEventListener('reset-home-filters', handleResetEvent)
  fetchFilteredProducts()
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleHomeScroll)
  window.removeEventListener('reset-home-filters', handleResetEvent)
})
</script>

<style scoped>
#mainBannerCarousel { background-color: #f8f9fa; cursor: pointer; }
.banner-img { object-fit: cover; transition: transform 0.3s ease; }
#mainBannerCarousel:hover .banner-img { transform: scale(1.01); }
.custom-indicator { width: 12px !important; height: 12px !important; border-radius: 50% !important; background-color: rgba(255, 255, 255, 0.7) !important; border: none !important; margin: 0 4px !important; }
.custom-indicator.active { background-color: #ffd400 !important; width: 28px !important; border-radius: 10px !important; }

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

.range-slider-container { position: relative; width: 140px; height: 24px; display: flex; align-items: center; }
.slider-track { position: absolute; width: 100%; height: 6px; border-radius: 4px; z-index: 1; }
.dual-range { position: absolute; width: 100%; -webkit-appearance: none; appearance: none; background: none; pointer-events: none; z-index: 2; margin: 0; }
.dual-range::-webkit-slider-thumb { -webkit-appearance: none; appearance: none; width: 16px; height: 16px; border-radius: 50%; background: #0d6efd; cursor: pointer; pointer-events: auto; border: 2px solid #ffffff; box-shadow: 0 1px 4px rgba(0, 0, 0, 0.4); transition: transform 0.1s; }
.dual-range::-webkit-slider-thumb:hover { transform: scale(1.15); }

.price-filter-box { border-color: #dee2e6 !important; }
.price-badge { font-size: 11.5px; min-width: 165px; text-align: center; letter-spacing: -0.2px; }

.section-badge-icon { width: 42px; height: 42px; }
.fw-extrabold { font-weight: 800; }
.tracking-tight { letter-spacing: -0.02em; }

.product-card { transition: transform 0.28s ease, box-shadow 0.28s ease; border: 1px solid #f1f3f5 !important; }
.product-card:hover { transform: translateY(-8px); box-shadow: 0 16px 32px rgba(0, 0, 0, 0.08) !important; }

/* Style sản phẩm hết hàng */
.out-of-stock-card { opacity: 0.8; }
.out-of-stock-card .product-img-wrapper img { filter: grayscale(30%); }

.product-img-wrapper { height: 200px; cursor: pointer; display: flex; align-items: center; justify-content: center; overflow: hidden; }
.product-img-zoom { max-height: 100%; object-fit: contain; transition: transform 0.35s ease; }
.product-card:hover .product-img-zoom { transform: scale(1.08); }

.hover-overlay { opacity: 0; transition: opacity 0.25s ease; }
.product-card:hover .hover-overlay { opacity: 1; }

.btn-add-cart { transition: background-color 0.2s, transform 0.15s ease; }
.btn-add-cart:hover { background-color: #e6be00 !important; transform: translateY(-1px); }

.no-scrollbar::-webkit-scrollbar { display: none; }
.no-scrollbar { -ms-overflow-style: none; scrollbar-width: none; }
.tracking-wider { letter-spacing: 0.03em; }
.fs-7 { font-size: 12px; }
.fs-8 { font-size: 11px; }
</style>