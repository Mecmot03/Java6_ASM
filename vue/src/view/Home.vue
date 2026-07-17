<template>
  <div class="container">

    <h3 class="fw-bold mb-4 text-uppercase">
      Tất cả sản phẩm
    </h3>

    <div class="row g-4">

      <div
        v-for="product in products"
        :key="product.id"
        class="col-12 col-sm-6 col-md-4 col-lg-3"
      >

        <div class="card h-100 shadow-sm border-0 product-card">

          <!-- Ảnh -->
          <div class="product-img-container p-3 d-flex align-items-center justify-content-center">

            <img
              :src="product.image ? '/images/' + product.image : 'https://via.placeholder.com/200'"
              class="card-img-top"
              :alt="product.name"
            >

          </div>

          <div class="card-body d-flex flex-column justify-content-between pt-0">

            <div>

              <h6
                class="card-title fw-bold text-dark text-truncate mb-1"
                :title="product.name"
              >
                {{ product.name }}
              </h6>

              <div class="d-flex align-items-center gap-2 mb-2">

                <span class="badge bg-light text-dark border">
                  {{ product.brand }}
                </span>

                <span
                  v-if="product.discountId"
                  class="badge bg-danger"
                >
                  Giảm giá
                </span>

              </div>

            </div>

            <div>

              <h5 class="text-danger fw-bold mb-3">
                {{ formatPrice(product.price) }}
              </h5>

              <!-- Nút xem chi tiết -->
              <button
                class="btn btn-outline-primary w-100 mb-2"
                @click="viewDetail(product.id)"
              >
                <i class="bi bi-eye"></i>
                Xem chi tiết
              </button>

              <!-- Nút thêm giỏ -->
              <button
                class="btn btn-warning w-100 fw-bold rounded-pill"
                @click="addToCart(product.id)"
              >
                <i class="bi bi-cart-plus me-1"></i>
                Thêm vào giỏ
              </button>

            </div>

          </div>

        </div>

      </div>

    </div>

  </div>
</template>

<script setup>

import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()

const products = ref([])

// Lấy danh sách sản phẩm
const fetchProducts = async () => {

  try {

    const response = await axios.get('http://localhost:8080/api/products')

    products.value = response.data

  } catch (error) {

    console.error("Lỗi khi tải sản phẩm:", error)

    // Dữ liệu demo khi chưa có Spring Boot
    products.value = [

      {
        id:1,
        name:"iPhone 16 Pro",
        brand:"Apple",
        price:32990000,
        image:"iphone16_1.jpg",
        discountId:1
      },

      {
        id:2,
        name:"Samsung Galaxy S26",
        brand:"Samsung",
        price:26990000,
        image:"s26_1.jpg",
        discountId:2
      },

      {
        id:3,
        name:"MacBook Air M5",
        brand:"Apple",
        price:32990000,
        image:"macbook_1.jpg",
        discountId:null
      },

      {
        id:4,
        name:"Asus ROG Zephyrus",
        brand:"Asus",
        price:39990000,
        image:"rog_1.jpg",
        discountId:null
      },

      {
        id:5,
        name:"AirPods Pro 3",
        brand:"Apple",
        price:6990000,
        image:"airpods_1.jpg",
        discountId:null
      }

    ]

  }

}

const formatPrice = (price) => {

  return new Intl.NumberFormat(
    'vi-VN',
    {
      style:'currency',
      currency:'VND'
    }
  ).format(price)

}

// Chuyển sang trang chi tiết
const viewDetail = (id) => {

  router.push('/product/' + id)

}

const addToCart = (productId) => {

  alert(`Đã thêm sản phẩm ID: ${productId} vào giỏ hàng!`)

}

onMounted(() => {

  fetchProducts()

})

</script>

<style scoped>

.product-card{
  transition:.3s;
  cursor:pointer;
}

.product-card:hover{
  transform:translateY(-8px);
  box-shadow:0 12px 20px rgba(0,0,0,.15);
}

.product-img-container{
  height:220px;
  overflow:hidden;
}

.product-img-container img{
  max-width:100%;
  max-height:100%;
  object-fit:contain;
}

</style>