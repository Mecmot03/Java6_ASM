<template>
  <div class="row g-4">
    <h3 class="fw-bold mb-3 text-dark text-uppercase">Sản phẩm nổi bật</h3>
    
    <!-- Lặp qua danh sách sản phẩm lấy được từ API hoặc dữ liệu dự phòng -->
    <div v-for="product in products" :key="product.id" class="col-12 col-sm-6 col-md-4 col-lg-3">
      <div class="card h-100 shadow-sm border-0 product-card">
        <!-- Ảnh sản phẩm: trỏ về thư mục public/images/ của Vue hoặc dùng ảnh placeholder nếu rỗng -->
        <div class="product-img-container p-3 d-flex align-items-center justify-content-center">
          <img :src="product.image ? '/images/' + product.image : 'https://via.placeholder.com/200'" 
               class="card-img-top" 
               :alt="product.name">
        </div>
        
        <div class="card-body d-flex flex-column justify-content-between pt-0">
          <div>
            <h6 class="card-title fw-bold text-dark text-truncate mb-1" :title="product.name">
              {{ product.name }}
            </h6>
            <div class="d-flex align-items-center gap-2 mb-2">
              <span class="badge bg-light text-dark border">{{ product.brand }}</span>
              <!-- Hiển thị nhãn giảm giá nếu có ID Discount -->
              <span v-if="product.discountId" class="badge bg-danger">Giảm giá</span>
            </div>
          </div>
          <div>
            <!-- Định dạng giá tiền Việt Nam Đồng -->
            <h5 class="text-danger fw-bold mb-3">
              {{ new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(product.price) }}
            </h5>
            <button class="btn btn-warning w-100 fw-bold rounded-pill" @click="addToCart(product.id)">
              <i class="bi bi-cart-plus me-1"></i> Thêm vào giỏ
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const products = ref([])

// Hàm gọi API - Chỉ lấy dữ liệu THẬT từ SQL Server qua Spring Boot
const fetchProducts = async () => {
  try {
    const response = await axios.get('http://localhost:8080/api/products')
    // Gán trực tiếp dữ liệu thật từ database vào danh sách hiển thị
    products.value = response.data
  } catch (error) {
    console.error("Lỗi khi tải danh sách sản phẩm từ Spring Boot:", error)
    // Nếu lỗi kết nối, giao diện sẽ trống trơn (mảng rỗng) chứ không thèm hiện dữ liệu ảo nữa!
    products.value = [] 
  }
}

const addToCart = (productId) => {
  alert(`Đã thêm sản phẩm ID: ${productId} vào giỏ hàng thành công!`)
}

onMounted(() => {
  fetchProducts()
})
</script>

<style scoped>
.product-card {
  transition: transform 0.2s, box-shadow 0.2s;
  cursor: pointer;
}
.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0,0,0,0.1) !important;
}
.product-img-container {
  height: 200px;
  overflow: hidden;
}
.product-img-container img {
  max-height: 100%;
  max-width: 100%;
  object-fit: contain;
}
</style>