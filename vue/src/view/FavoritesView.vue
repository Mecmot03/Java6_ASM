<template>
  <div class="container py-4">


    <div class="d-flex align-items-center justify-content-between mb-4">
      <h3 class="fw-bold text-dark mb-0 d-flex align-items-center gap-2">
        <i class="bi bi-heart-fill text-danger"></i> Sản phẩm đã yêu thích ({{ favorites.length }})
      </h3>
    </div>

    <!-- Khi không có sản phẩm nào -->
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-warning" role="status"></div>
    </div>

    <div v-else-if="favorites.length === 0" class="card border-0 shadow-sm rounded-4 p-5 text-center bg-white">
      <i class="bi bi-heartbreak display-1 text-muted opacity-50 mb-3"></i>
      <h5 class="fw-bold text-secondary">Danh sách yêu thích trống!</h5>
      <p class="text-muted small">Hãy thả tim các sản phẩm bạn thích để xem lại sau nhé.</p>
      <router-link to="/" class="btn btn-warning rounded-pill px-4 fw-bold text-dark mx-auto mt-2">
        Khám phá sản phẩm
      </router-link>
    </div>

    <!-- Danh sách sản phẩm yêu thích -->
    <div v-else class="row g-4">
      <div v-for="item in favorites" :key="item.id" class="col-lg-3 col-md-4 col-sm-6">
        <div class="card h-100 border-0 shadow-sm rounded-4 product-card p-3 position-relative bg-white">
          <!-- Nút xóa khỏi danh sách -->
          <button 
            class="btn btn-sm btn-light rounded-circle position-absolute top-0 end-0 m-3 shadow-sm delete-btn"
            title="Bỏ yêu thích"
            @click.stop="removeFavorite(item.product.id)"
          >
            <i class="bi bi-x-lg text-danger"></i>
          </button>

          <img 
            :src="getImage(item.product.image)" 
            class="card-img-top mb-3 product-img" 
            :alt="item.product.name"
            @click="viewProduct(item.product.id)"
          >

          <div class="card-body p-0 d-flex flex-column justify-content-between" @click="viewProduct(item.product.id)">
            <div>
              <span class="text-muted fs-8 d-block mb-1">{{ item.product.brand }}</span>
              <h6 class="fw-bold text-dark text-truncate mb-2" :title="item.product.name">{{ item.product.name }}</h6>
            </div>

            <div class="d-flex align-items-center justify-content-between mt-2">
              <h6 class="text-danger fw-bold mb-0">{{ formatPrice(item.product.price) }}</h6>
              <button class="btn btn-sm btn-warning rounded-circle p-2" title="Thêm vào giỏ" @click.stop="addToCart(item.product)">
                <i class="bi bi-cart-plus"></i>
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
import { addGuestCartItem } from '../utils/cart'
import { notify } from '../utils/notify'

const router = useRouter()
const favorites = ref([])
const loading = ref(true)

const getUserId = () => {
  const userStorage = localStorage.getItem('user')
  if (userStorage) {
    try { return JSON.parse(userStorage).id || null } catch { return null }
  }
  return null
}

const fetchFavorites = async () => {
  const userId = getUserId()
  if (!userId) {
    loading.value = false
    router.push('/login')
    return
  }

  try {
    const response = await axios.get(`/api/favorites/user/${userId}`)
    favorites.value = response.data
  } catch (error) {
    favorites.value = []
  } finally {
    loading.value = false
  }
}

const removeFavorite = async (productId) => {
  const userId = getUserId()
  try {
    await axios.delete(`/api/favorites?userId=${userId}&productId=${productId}`)
    favorites.value = favorites.value.filter(item => item.product.id !== productId)
    notify("Đã xóa khỏi danh sách yêu thích!", "info")
  } catch (error) {
    notify("Thao tác thất bại!", "danger")
  }
}

const viewProduct = (id) => {
  router.push('/product/' + id)
}

const addToCart = (product) => {
  const userId = getUserId()
  if (!userId) {
    addGuestCartItem(product, 1)
  } else {
    axios.post(`/api/cart/add?userId=${userId}`, { productId: product.id, quantity: 1 })
  }
  window.dispatchEvent(new CustomEvent('cart-updated'))
  notify("Đã thêm sản phẩm vào giỏ hàng!", "success")
}

const formatPrice = (price) => {
  if (!price && price !== 0) return ""
  return new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(price)
}

const getImage = (image) => {
  if (!image) return "https://via.placeholder.com/400"
  if (image.startsWith('http') || image.startsWith('/')) return image
  return "/images/" + image
}

onMounted(() => {
  fetchFavorites()
})
</script>

<style scoped>
.product-card { cursor: pointer; transition: transform .25s ease, box-shadow .25s ease; }
.product-card:hover { transform: translateY(-4px); box-shadow: 0 8px 16px rgba(0,0,0,.08) !important; }
.product-img { height: 180px; object-fit: contain; }
.delete-btn { z-index: 10; opacity: 0.8; transition: .2s; }
.delete-btn:hover { opacity: 1; transform: scale(1.1); }
.fs-8 { font-size: 12px; }
</style>