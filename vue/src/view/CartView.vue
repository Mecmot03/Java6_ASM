<template>
  <div class="container py-4">
    <!-- TIÊU ĐỀ GIỎ HÀNG -->
    <div class="d-flex align-items-center justify-content-between mb-4">
      <h3 class="fw-bold text-dark mb-0 d-flex align-items-center gap-2">
        <i class="bi bi-cart3 text-warning"></i> Giỏ hàng của bạn
      </h3>
      <span class="badge bg-warning text-dark px-3 py-2 rounded-pill fs-7 fw-bold">
        {{ cartItems.length }} sản phẩm
      </span>
    </div>

    <!-- TRẠNG THÁI GIỎ HÀNG TRỐNG -->
    <div v-if="cartItems.length === 0" class="text-center py-5 bg-white rounded-4 shadow-sm border">
      <i class="bi bi-cart-x display-1 text-muted opacity-25 d-block mb-3"></i>
      <h5 class="fw-bold text-dark mb-2">Giỏ hàng đang trống!</h5>
      <p class="text-secondary mb-4">Hãy chọn thêm sản phẩm yêu thích để tiến hành mua sắm nhé.</p>
      <router-link to="/" class="btn btn-warning rounded-pill px-4 py-2 fw-bold text-dark">
        <i class="bi bi-arrow-left me-1"></i> Tiếp tục mua sắm
      </router-link>
    </div>

    <!-- DANH SÁCH SẢN PHẨM & TỔNG TIỀN -->
    <div v-else class="row g-4">
      <!-- CỘT TRÁI: DANH SÁCH SẢN PHẨM -->
      <div class="col-lg-8">
        <div class="card border-0 shadow-sm rounded-4 overflow-hidden mb-3">
          <div class="card-body p-0">
            <div class="table-responsive">
              <table class="table align-middle mb-0">
                <thead class="bg-light">
                  <tr>
                    <th scope="col" class="ps-4 py-3 text-secondary text-uppercase small">Sản phẩm</th>
                    <th scope="col" class="py-3 text-secondary text-uppercase small text-center">Đơn giá</th>
                    <th scope="col" class="py-3 text-secondary text-uppercase small text-center">Số lượng</th>
                    <th scope="col" class="py-3 text-secondary text-uppercase small text-center">Thành tiền</th>
                    <th scope="col" class="pe-4 py-3 text-end">
                      <button class="btn btn-link text-danger p-0 border-0 fs-7 fw-bold text-decoration-none" @click="clearCart">
                        Xóa tất cả
                      </button>
                    </th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="item in cartItems" :key="item.id">
                    <!-- Ảnh & Tên sản phẩm -->
                    <td class="ps-4 py-3">
                      <div class="d-flex align-items-center gap-3">
                        <img :src="'/images/' + item.productImage" :alt="item.productName" class="cart-product-img rounded-3 border p-1" />
                        <div>
                          <h6 class="fw-bold text-dark mb-1 text-truncate" style="max-width: 220px;" :title="item.productName">
                            {{ item.productName }}
                          </h6>
                          <span class="small text-muted">Mã SP: #{{ item.productId }}</span>
                        </div>
                      </div>
                    </td>

                    <!-- Đơn giá -->
                    <td class="text-center fw-medium text-secondary">
                      {{ formatPrice(item.price) }}
                    </td>

                    <!-- Bộ tăng giảm số lượng -->
                    <td class="text-center">
                      <div class="quantity-control d-inline-flex align-items-center border rounded-pill bg-light p-1">
                        <button class="btn btn-sm btn-light rounded-circle shadow-none p-0 qty-btn" @click="updateQuantity(item, item.quantity - 1)">
                          <i class="bi bi-dash"></i>
                        </button>
                        <span class="px-3 fw-bold small">{{ item.quantity }}</span>
                        <button class="btn btn-sm btn-light rounded-circle shadow-none p-0 qty-btn" @click="updateQuantity(item, item.quantity + 1)">
                          <i class="bi bi-plus"></i>
                        </button>
                      </div>
                    </td>

                    <!-- Thành tiền -->
                    <td class="text-center fw-bold text-danger">
                      {{ formatPrice(item.subTotal) }}
                    </td>

                    <!-- Nút xóa -->
                    <td class="pe-4 text-end">
                      <button class="btn btn-light btn-sm rounded-circle text-muted" title="Xóa sản phẩm" @click="removeItem(item.id)">
                        <i class="bi bi-trash"></i>
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>

        <router-link to="/" class="text-decoration-none text-dark fw-bold small d-inline-flex align-items-center gap-1">
          <i class="bi bi-arrow-left"></i> Tiếp tục chọn sản phẩm khác
        </router-link>
      </div>

      <!-- CỘT PHẢI: TÓM TẮT ĐƠN HÀNG -->
      <div class="col-lg-4">
        <div class="card border-0 shadow-sm rounded-4 p-4 sticky-top" style="top: 100px;">
          <h5 class="fw-bold text-dark mb-3">Tóm tắt đơn hàng</h5>
          
          <div class="d-flex justify-content-between mb-2 text-secondary">
            <span>Tạm tính ({{ totalQuantity }} sản phẩm):</span>
            <span class="fw-medium text-dark">{{ formatPrice(totalAmount) }}</span>
          </div>

          <div class="d-flex justify-content-between mb-3 text-secondary">
            <span>Phí vận chuyển:</span>
            <span class="text-success fw-bold">Miễn phí</span>
          </div>

          <hr class="my-3 border-light-subtle">

          <div class="d-flex justify-content-between mb-4">
            <span class="fw-bold text-dark fs-5">Tổng tiền:</span>
            <div class="text-end">
              <span class="fw-bold text-danger fs-4 d-block">{{ formatPrice(totalAmount) }}</span>
              <small class="text-muted fs-8">(Đã bao gồm VAT)</small>
            </div>
          </div>

          <button class="btn btn-warning btn-lg w-100 rounded-pill fw-bold text-dark shadow-sm py-3 mb-2" @click="checkout">
            TIẾN HÀNH ĐẶT HÀNG
          </button>
          
          <p class="text-center small text-muted mb-0 mt-2">
            <i class="bi bi-shield-check text-success me-1"></i> Bảo mật & An toàn thanh toán
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()
const cartItems = ref([])

// Lấy UserId động từ localStorage (Nếu đã đăng nhập lấy id thật, chưa thì mặc định id = 1)
const getUserId = () => {
  const userStorage = localStorage.getItem('user')
  if (userStorage) {
    try {
      const user = JSON.parse(userStorage)
      return user.id || 1
    } catch (e) {
      return 1
    }
  }
  return 1
}

// Hàm phát sự kiện đồng bộ lại Badge số lượng giỏ hàng trên Navbar Header
const notifyCartUpdate = () => {
  window.dispatchEvent(new CustomEvent('cart-updated'))
}

// Tải danh sách giỏ hàng
const fetchCart = async () => {
  try {
    const userId = getUserId()
    const res = await axios.get(`http://localhost:8080/api/cart?userId=${userId}`)
    cartItems.value = res.data
    notifyCartUpdate()
  } catch (err) {
    console.error("Lỗi khi tải giỏ hàng:", err)
  }
}

// Cập nhật số lượng item
const updateQuantity = async (item, newQty) => {
  if (newQty < 1) {
    await removeItem(item.id)
    return
  }
  try {
    const userId = getUserId()
    await axios.put(`http://localhost:8080/api/cart/update/${item.id}?userId=${userId}&quantity=${newQty}`)
    await fetchCart()
  } catch (err) {
    console.error("Lỗi cập nhật số lượng:", err)
  }
}

// Xóa 1 sản phẩm
const removeItem = async (cartItemId) => {
  if (confirm("Bạn có chắc muốn xóa sản phẩm này khỏi giỏ hàng?")) {
    try {
      const userId = getUserId()
      await axios.delete(`http://localhost:8080/api/cart/remove/${cartItemId}?userId=${userId}`)
      await fetchCart()
    } catch (err) {
      console.error("Lỗi xóa sản phẩm:", err)
    }
  }
}

// Xóa toàn bộ giỏ hàng
const clearCart = async () => {
  if (confirm("Bạn có chắc muốn xóa toàn bộ giỏ hàng?")) {
    try {
      const userId = getUserId()
      await axios.delete(`http://localhost:8080/api/cart/clear?userId=${userId}`)
      cartItems.value = []
      notifyCartUpdate()
    } catch (err) {
      console.error("Lỗi làm sạch giỏ hàng:", err)
    }
  }
}

// Tính tổng tiền & số lượng
const totalAmount = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + (item.subTotal || 0), 0)
})

const totalQuantity = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + (item.quantity || 0), 0)
})

const formatPrice = (price) => {
  if (!price && price !== 0) return '0 đ'
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price)
}

// Tiến hành đặt hàng
const checkout = () => {
  const token = localStorage.getItem('token')
  if (!token) {
    if (confirm("Bạn cần đăng nhập để tiến hành đặt hàng. Đăng nhập ngay?")) {
      router.push('/login')
    }
  } else {
    router.push('/checkout')
  }
}

onMounted(() => {
  fetchCart()
})
</script>

<style scoped>
.cart-product-img {
  width: 64px;
  height: 64px;
  object-fit: contain;
  background-color: #fafafa;
}

.qty-btn {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
}

.quantity-control {
  user-select: none;
}

.fs-7 { font-size: 12px; }
.fs-8 { font-size: 11px; }
</style>