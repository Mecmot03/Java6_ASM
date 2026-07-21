<template>
  <div class="flash-sale-page pb-5">

    <!-- 1. BANNER CHÍNH TRÀN KHUNG SÁT HEADER (DÙNG NỀN VÀNG ĐỒNG BỘ) -->
    <div class="hero-banner-wrapper">
      <img 
        src="/images/OG.jpg" 
        alt="Deal Đẹp Có Hạn - Rẻ Hơn Mỗi Ngày" 
        class="hero-banner-img" 
      />
    </div>

    <!-- 2. KHU VỰC DƯỚI BANNER (SẢN PHẨM & ĐỒNG HỒ) -->
    <div class="container mt-4">

      <!-- Header Flash Sale & Countdown -->
      <div class="bg-dark text-white p-3 rounded-4 mb-4 d-flex flex-wrap justify-content-between align-items-center shadow">
        <h3 class="m-0 text-warning fw-bold fs-4 d-flex align-items-center gap-2">
          ⚡ FLASH SALE GIỜ VÀNG
        </h3>

        <!-- Countdown Timer -->
        <div class="d-flex align-items-center gap-2 mt-2 mt-sm-0">
          <span class="text-light fw-medium">Kết thúc sau:</span>
          <div class="d-flex align-items-center gap-1 fw-bold">
            <span class="time-box bg-warning text-dark px-2 py-1 rounded">02</span> :
            <span class="time-box bg-warning text-dark px-2 py-1 rounded">45</span> :
            <span class="time-box bg-warning text-dark px-2 py-1 rounded">18</span>
          </div>
        </div>
      </div>

      <!-- DANH SÁCH SẢN PHẨM -->
      <div class="row g-3">
        <div v-for="product in flashProducts" :key="product.id" class="col-6 col-md-4 col-lg-3">
          <div class="card h-100 border-0 shadow-sm product-card position-relative overflow-hidden">
            <!-- Badge Giảm Giá -->
            <span class="badge bg-danger position-absolute top-0 end-0 m-2 fs-7">
              -{{ product.discount }}%
            </span>

            <img :src="product.image" class="card-img-top p-3 img-fit" :alt="product.name" />

            <div class="card-body d-flex flex-column justify-content-between">
              <div>
                <h6 class="card-title text-truncate-2 fw-semibold mb-2" :title="product.name">
                  {{ product.name }}
                </h6>
                <div class="d-flex align-items-baseline gap-2 mb-2">
                  <span class="text-danger fw-bold fs-5">
                    {{ formatCurrency(product.salePrice) }}
                  </span>
                  <span class="text-muted text-decoration-line-through fs-7">
                    {{ formatCurrency(product.originalPrice) }}
                  </span>
                </div>
              </div>

              <!-- Progress Bar -->
              <div class="mt-2">
                <div class="progress progress-custom position-relative" style="height: 16px;">
                  <div 
                    class="progress-bar bg-warning" 
                    role="progressbar"
                    :style="{ width: product.soldPercent + '%' }"
                  ></div>
                  <small class="progress-text fw-bold text-dark position-absolute w-100 text-center">
                    🔥 Đã bán {{ product.soldCount }}
                  </small>
                </div>
                <button class="btn btn-warning w-100 mt-3 fw-bold rounded-pill text-dark">
                  MUA NGAY
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
import { ref } from 'vue'

const formatCurrency = (val) => {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val)
}

const flashProducts = ref([
  {
    id: 1,
    name: 'iPhone 15 Pro Max 256GB - Chính hãng VN/A',
    originalPrice: 34990000,
    salePrice: 29590000,
    discount: 15,
    soldCount: 42,
    soldPercent: 85,
    image: 'https://cdn.vcell.vn/vcell/images/2023/09/iphone-15-pro-max-natural-titanium.png'
  },
  {
    id: 2,
    name: 'Samsung Galaxy S24 Ultra 12GB/256GB',
    originalPrice: 33990000,
    salePrice: 26990000,
    discount: 20,
    soldCount: 38,
    soldPercent: 76,
    image: 'https://cdn.vcell.vn/vcell/images/2024/01/samsung-galaxy-s24-ultra-grey.png'
  },
  {
    id: 3,
    name: 'Xiaomi 14 Ultra 5G (16GB/512GB)',
    originalPrice: 32990000,
    salePrice: 27990000,
    discount: 15,
    soldCount: 19,
    soldPercent: 40,
    image: 'https://cdn.vcell.vn/vcell/images/2024/03/xiaomi-14-ultra-black.png'
  },
  {
    id: 4,
    name: 'Tai nghe Bluetooth Apple AirPods Pro 2 MagSafe',
    originalPrice: 6190000,
    salePrice: 4890000,
    discount: 21,
    soldCount: 95,
    soldPercent: 95,
    image: 'https://cdn.vcell.vn/vcell/images/2022/09/airpods-pro-2.png'
  }
])
</script>

<style scoped>
/* Xóa sạch lề thừa ở trang FlashSale */
.flash-sale-page {
  background-color: #f8f9fa;
  min-height: 100vh;
  margin: 0 !important;
  padding: 0 !important;
}

/* Khung bọc Banner tràn sát 100% hai bên màn hình */
/* .hero-banner-wrapper {
  width: 100%;
  margin: 0;
  padding: 0;
  background-color: #ffd400; 
} */ cái này tutu

/* Ảnh banner tự do phình to 100% theo tỉ lệ thực, không bị xén chân */
.hero-banner-img {
  width: 100%;
  height: auto;       /* Bắt buộc auto để giữ nguyên tỉ lệ gốc của ảnh */
  display: block;
  max-height: none;   /* Bỏ mọi giới hạn chiều cao */
  object-fit: fill;   /* Đảm bảo phủ kín khung */
}

/* BẢNG PHỤ CHO CARDS & CÁC CHI TIẾT SẢN PHẨM BÊN DƯỚI */
.img-fit {
  height: 180px;
  object-fit: contain;
}

.text-truncate-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  height: 40px;
}

.time-box {
  font-size: 0.95rem;
}

.progress-custom {
  background-color: #ffe89a;
  border-radius: 10px;
}

.progress-text {
  font-size: 0.7rem;
  line-height: 16px;
}

.product-card {
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1) !important;
}
</style>