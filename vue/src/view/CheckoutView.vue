<template>
  <div class="container py-4">
    <!-- TIÊU ĐỀ TRANG -->
    <div class="d-flex align-items-center justify-content-between mb-4">
      <h3 class="fw-bold text-dark mb-0 d-flex align-items-center gap-2">
        <i class="bi bi-credit-card-2-front text-warning"></i> Tiến Hành Đặt Hàng
      </h3>
      <router-link to="/cart" class="btn btn-outline-dark btn-sm rounded-pill px-3">
        <i class="bi bi-arrow-left me-1"></i> Quay lại giỏ hàng
      </router-link>
    </div>

    <!-- NẾU GIỎ HÀNG TRỐNG -->
    <div v-if="cartItems.length === 0 && !loading" class="text-center py-5 bg-white rounded-4 shadow-sm border">
      <i class="bi bi-cart-x display-1 text-muted opacity-25 d-block mb-3"></i>
      <h5 class="fw-bold text-dark mb-2">Chưa có sản phẩm nào để thanh toán!</h5>
      <router-link to="/" class="btn btn-warning rounded-pill px-4 py-2 fw-bold text-dark mt-2">
        Mua sắm ngay
      </router-link>
    </div>

    <!-- NỘI DUNG THANH TOÁN -->
    <form v-else @submit.prevent="handlePlaceOrder" class="row g-4">
      
      <!-- CỘT TRÁI: THÔNG TIN GIAO HÀNG & THANH TOÁN -->
      <div class="col-lg-7">
        <!-- 1. THÔNG TIN KHÁCH HÀNG -->
        <div class="card border-0 shadow-sm rounded-4 p-4 mb-4">
          <h5 class="fw-bold text-dark mb-3 d-flex align-items-center gap-2">
            <i class="bi bi-geo-alt text-danger"></i> Thông tin nhận hàng
          </h5>

          <div class="row g-3">
            <div class="col-md-6">
              <label class="form-label small fw-bold text-secondary">Họ và tên người nhận</label>
              <input 
                type="text" 
                class="form-control rounded-3" 
                placeholder="Nhập họ tên" 
                v-model="orderForm.fullName" 
                required 
              />
            </div>

            <div class="col-md-6">
              <label class="form-label small fw-bold text-secondary">Số điện thoại</label>
              <input 
                type="tel" 
                class="form-control rounded-3" 
                placeholder="Ví dụ: 0912345678" 
                v-model="orderForm.phone" 
                required 
              />
            </div>

            <div class="col-12">
              <label class="form-label small fw-bold text-secondary">Địa chỉ email (nhận thông báo)</label>
              <input 
                type="email" 
                class="form-control rounded-3" 
                placeholder="name@example.com" 
                v-model="orderForm.email" 
                required 
              />
            </div>

            <div class="col-12">
              <label class="form-label small fw-bold text-secondary">Địa chỉ giao hàng chi tiết</label>
              <input 
                type="text" 
                class="form-control rounded-3" 
                placeholder="Số nhà, tên đường, Phường/Xã, Quận/Huyện, Tỉnh/Thành" 
                v-model="orderForm.address" 
                required 
              />
            </div>

            <div class="col-12">
              <label class="form-label small fw-bold text-secondary">Ghi chú đơn hàng (Tùy chọn)</label>
              <textarea 
                class="form-control rounded-3" 
                rows="2" 
                placeholder="Giao giờ hành chính, gọi trước khi giao..." 
                v-model="orderForm.note"
              ></textarea>
            </div>
          </div>
        </div>

        <!-- 2. PHƯƠNG THỨC THANH TOÁN -->
        <div class="card border-0 shadow-sm rounded-4 p-4">
          <h5 class="fw-bold text-dark mb-3 d-flex align-items-center gap-2">
            <i class="bi bi-wallet2 text-warning"></i> Phương thức thanh toán
          </h5>

          <div class="d-flex flex-column gap-2">
            <!-- COD -->
            <label class="payment-option d-flex align-items-center justify-content-between p-3 border rounded-3 cursor-pointer" :class="{ 'active-border': orderForm.paymentMethod === 'COD' }">
              <div class="d-flex align-items-center gap-3">
                <input type="radio" name="payment" value="COD" v-model="orderForm.paymentMethod" />
                <div>
                  <h6 class="mb-0 fw-bold text-dark">Thanh toán khi nhận hàng (COD)</h6>
                  <small class="text-muted">Thanh toán bằng tiền mặt cho nhân viên giao hàng</small>
                </div>
              </div>
              <i class="bi bi-cash-stack fs-4 text-success"></i>
            </label>

            <!-- VNPAY / CHUYỂN KHOẢN -->
            <label class="payment-option d-flex align-items-center justify-content-between p-3 border rounded-3 cursor-pointer" :class="{ 'active-border': orderForm.paymentMethod === 'BANK' }">
              <div class="d-flex align-items-center gap-3">
                <input type="radio" name="payment" value="BANK" v-model="orderForm.paymentMethod" />
                <div>
                  <h6 class="mb-0 fw-bold text-dark">Chuyển khoản ngân hàng / QR Code</h6>
                  <small class="text-muted">Quét mã QR qua ứng dụng ngân hàng</small>
                </div>
              </div>
              <i class="bi bi-qr-code-scan fs-4 text-primary"></i>
            </label>
          </div>
        </div>
      </div>

      <!-- CỘT PHẢI: TÓM TẮT SẢN PHẨM & TỔNG TIỀN -->
      <div class="col-lg-5">
        <div class="card border-0 shadow-sm rounded-4 p-4 sticky-top" style="top: 100px;">
          <h5 class="fw-bold text-dark mb-3">Sản phẩm thanh toán ({{ totalQuantity }})</h5>

          <!-- DANH SÁCH MÓN HÀNG -->
          <div class="checkout-items-list mb-3 pe-1 overflow-auto" style="max-height: 280px;">
            <div v-for="item in cartItems" :key="item.id" class="d-flex align-items-center justify-content-between py-2 border-bottom">
              <div class="d-flex align-items-center gap-3">
                <img :src="'/images/' + item.productImage" :alt="item.productName" class="checkout-img rounded-3 border p-1" />
                <div>
                  <h6 class="fw-bold text-dark mb-0 text-truncate" style="max-width: 180px;" :title="item.productName">
                    {{ item.productName }}
                  </h6>
                  <small class="text-muted">x{{ item.quantity }}</small>
                </div>
              </div>
              <span class="fw-bold text-dark small">{{ formatPrice(item.subTotal) }}</span>
            </div>
          </div>

          <!-- TỔNG CỘNG -->
          <div class="d-flex justify-content-between mb-2 text-secondary">
            <span>Tạm tính:</span>
            <span class="fw-medium text-dark">{{ formatPrice(totalAmount) }}</span>
          </div>

          <div class="d-flex justify-content-between mb-3 text-secondary">
            <span>Phí vận chuyển:</span>
            <span class="text-success fw-bold">Miễn phí</span>
          </div>

          <hr class="my-2 border-light-subtle">

          <div class="d-flex justify-content-between mb-4 align-items-center">
            <span class="fw-bold text-dark fs-5">Tổng thanh toán:</span>
            <span class="fw-bold text-danger fs-3">{{ formatPrice(totalAmount) }}</span>
          </div>

          <!-- NÚT XÁC NHẬN ĐẶT HÀNG -->
          <button 
            type="submit" 
            class="btn btn-warning btn-lg w-100 rounded-pill fw-bold text-dark shadow-sm py-3"
            :disabled="submitting"
          >
            <span v-if="submitting" class="spinner-border spinner-border-sm me-2"></span>
            <i v-else class="bi bi-check2-circle me-1"></i> XÁC NHẬN ĐẶT HÀNG
          </button>

          <p class="text-center small text-muted mb-0 mt-3 fs-8">
            <i class="bi bi-shield-check text-success me-1"></i> Bằng việc đặt hàng, bạn đồng ý với Điều khoản dịch vụ của chúng tôi.
          </p>
        </div>
      </div>

    </form>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()
const cartItems = ref([])
const loading = ref(true)
const submitting = ref(false)

const orderForm = ref({
  fullName: '',
  phone: '',
  email: '',
  address: '',
  note: '',
  paymentMethod: 'COD'
})

// Lấy thông tin user hiện tại
const getUserFromStorage = () => {
  const userStorage = localStorage.getItem('user')
  if (userStorage) {
    try {
      return JSON.parse(userStorage)
    } catch (e) {
      return null
    }
  }
  return null
}

// Tải dữ liệu giỏ hàng & điền thông tin mặc định
const fetchCheckoutData = async () => {
  loading.value = true
  const user = getUserFromStorage()
  
  if (!user) {
    alert("Vui lòng đăng nhập để tiến hành đặt hàng!")
    router.push('/login')
    return
  }

  // Tự điền thông tin sẵn có từ tài khoản người dùng
  orderForm.value.fullName = user.fullName || user.username || ''
  orderForm.value.email = user.email || ''
  orderForm.value.phone = user.phone || ''
  orderForm.value.address = user.address || ''

  try {
    const res = await axios.get(`http://localhost:8080/api/cart?userId=${user.id}`)
    cartItems.value = res.data
  } catch (err) {
    console.error("Lỗi khi tải thông tin thanh toán:", err)
  } finally {
    loading.value = false
  }
}

// Tính toán tổng tiền
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

// Xử lý nút ĐẶT HÀNG
const handlePlaceOrder = async () => {
  const user = getUserFromStorage()
  if (!user) return

  submitting.value = true

  const orderPayload = {
    userId: user.id,
    receiverName: orderForm.value.fullName,
    receiverPhone: orderForm.value.phone,
    receiverEmail: orderForm.value.email,
    shippingAddress: orderForm.value.address,
    note: orderForm.value.note,
    paymentMethod: orderForm.value.paymentMethod,
    totalAmount: totalAmount.value,
    items: cartItems.value
  }

  try {
    // Gọi API lưu Đơn hàng vào Database
    await axios.post('http://localhost:8080/api/orders/create', orderPayload)

    // Làm sạch giỏ hàng sau khi đặt thành công
    await axios.delete(`http://localhost:8080/api/cart/clear?userId=${user.id}`)
    
    // Cập nhật lại Badge giỏ hàng trên Header
    window.dispatchEvent(new CustomEvent('cart-updated'))

    alert("🎉 Đặt hàng thành công! Đơn hàng của bạn đang chờ xác nhận.")
    
    // Chuyển hướng sang trang Xem đơn hàng vừa đặt
    router.push('/orders?status=PENDING')
  } catch (err) {
    console.error("Lỗi khi tạo đơn hàng:", err)
    alert(err.response?.data?.message || "Đặt hàng thất bại. Vui lòng thử lại sau!")
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  fetchCheckoutData()
})
</script>

<style scoped>
.checkout-img {
  width: 48px;
  height: 48px;
  object-fit: contain;
  background-color: #fafafa;
}

.payment-option {
  transition: all 0.2s ease-in-out;
}

.payment-option:hover,
.active-border {
  border-color: #ffd400 !important;
  background-color: #fffdf0;
}

.cursor-pointer {
  cursor: pointer;
}

.fs-8 {
  font-size: 11px;
}
</style>