<template>
  <div class="container py-4">
    <!-- TIÊU ĐỀ TRANG -->
    <div class="d-flex align-items-center justify-content-between mb-4">
      <h3 class="fw-bold text-dark mb-0 d-flex align-items-center gap-2">
        <i class="bi bi-box-seam text-warning"></i> Quản Lý Đơn Hàng
      </h3>
      <router-link to="/" class="btn btn-outline-dark btn-sm rounded-pill px-3">
        <i class="bi bi-house me-1"></i> Về trang chủ
      </router-link>
    </div>

    <!-- THANH TAB TRẠNG THÁI ĐƠN HÀNG -->
    <div class="card border-0 shadow-sm rounded-4 mb-4 overflow-hidden">
      <div class="d-flex border-bottom bg-white overflow-auto text-nowrap">
        <button 
          v-for="tab in tabs" 
          :key="tab.status" 
          class="btn tab-btn px-4 py-3 fw-bold rounded-0 border-0 flex-fill text-secondary"
          :class="{ 'active-tab': currentStatus === tab.status }"
          @click="changeTab(tab.status)"
        >
          <i :class="tab.icon" class="me-1"></i> {{ tab.label }}
        </button>
      </div>
    </div>

    <!-- TRẠNG THÁI LOADING -->
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-warning" role="status"></div>
      <p class="mt-2 text-muted">Đang tải danh sách đơn hàng...</p>
    </div>

    <!-- NẾU KHÔNG CÓ ĐƠN HÀNG NÀO -->
    <div v-else-if="orders.length === 0" class="card border-0 shadow-sm rounded-4 p-5 text-center">
      <i class="bi bi-inbox display-1 text-muted opacity-25 d-block mb-3"></i>
      <h5 class="fw-bold text-dark mb-1">Chưa có đơn hàng nào</h5>
      <p class="text-muted small mb-3">Bạn chưa có đơn hàng nào thuộc trạng thái này.</p>
      <router-link to="/" class="btn btn-warning rounded-pill px-4 py-2 fw-bold text-dark mx-auto">
        Khám phá sản phẩm ngay
      </router-link>
    </div>

    <!-- DANH SÁCH ĐƠN HÀNG -->
    <div v-else class="d-flex flex-column gap-4">
      <div v-for="order in orders" :key="order.id" class="card border-0 shadow-sm rounded-4 p-4">
        
        <!-- HEADER ĐƠN HÀNG -->
        <div class="d-flex justify-content-between align-items-center border-bottom pb-3 mb-3">
          <div class="d-flex align-items-center gap-3">
            <span class="fw-bold text-dark fs-5">Mã đơn hàng: #{{ order.id }}</span>
            <small class="text-muted border-start ps-3">
              <i class="bi bi-clock me-1"></i>{{ formatDate(order.orderDate) }}
            </small>
          </div>
          <span class="badge rounded-pill px-3 py-2 fw-bold" :class="getStatusBadgeClass(order.status)">
            {{ getStatusText(order.status) }}
          </span>
        </div>

        <!-- THÔNG TIN NGƯỜI NHẬN -->
        <div class="row g-2 mb-3 bg-light p-3 rounded-3 small">
          <div class="col-md-4">
            <i class="bi bi-person me-1 text-secondary"></i>
            <strong>Người nhận:</strong> {{ order.receiverName }}
          </div>
          <div class="col-md-4">
            <i class="bi bi-telephone me-1 text-secondary"></i>
            <strong>Số điện thoại:</strong> {{ order.receiverPhone }}
          </div>
          <div class="col-md-4">
            <i class="bi bi-credit-card me-1 text-secondary"></i>
            <strong>Thanh toán:</strong> {{ order.paymentMethod === 'COD' ? 'Thanh toán khi nhận (COD)' : 'Chuyển khoản' }}
          </div>
          <div class="col-12 mt-1">
            <i class="bi bi-geo-alt me-1 text-secondary"></i>
            <strong>Địa chỉ:</strong> {{ order.shippingAddress }}
          </div>
        </div>

        <!-- DANH SÁCH SẢN PHẨM TRONG ĐƠN HÀNG -->
        <div class="order-items-list border-top border-bottom py-2 mb-3" v-if="order.items && order.items.length > 0">
          <div v-for="item in order.items" :key="item.id" class="d-flex align-items-center justify-content-between py-2">
            <div class="d-flex align-items-center gap-3">
              <img :src="'/images/' + (item.productImage || 'choetechpd.jpg')" class="order-item-img rounded-3 border p-1" alt="Product" />
              <div>
                <h6 class="fw-bold text-dark mb-1">{{ item.productName || 'Sản phẩm #' + item.productId }}</h6>
                <small class="text-muted">Số lượng: x{{ item.quantity }}</small>
              </div>
            </div>
            <div class="fw-bold text-dark">
              {{ formatPrice(item.price * item.quantity) }}
            </div>
          </div>
        </div>

        <!-- NẾU CHƯA CÓ ITEMS (HIỂN THỊ TẠM THÔNG BÁO SỐ LƯỢNG) -->
        <div v-else class="py-2 text-muted small border-top border-bottom mb-3">
          <i class="bi bi-bag-check me-1"></i> Đơn hàng bao gồm các sản phẩm đã được xác nhận.
        </div>

        <!-- FOOTER ĐƠN HÀNG -->
        <div class="d-flex justify-content-between align-items-center pt-2">
          <button class="btn btn-outline-secondary btn-sm rounded-pill px-3" @click="handleCancelOrder(order.id)" v-if="order.status === 'PENDING'">
            Hủy đơn hàng
          </button>
          <div class="ms-auto text-end">
            <span class="text-muted small me-2">Tổng thanh toán:</span>
            <span class="fw-bold text-danger fs-4">{{ formatPrice(order.totalAmount) }}</span>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const router = useRouter()

const orders = ref([])
const loading = ref(true)
const currentStatus = ref(route.query.status || 'PENDING')

const tabs = [
  { label: 'Chờ xác nhận', status: 'PENDING', icon: 'bi bi-clock' },
  { label: 'Chờ lấy hàng', status: 'PROCESSING', icon: 'bi bi-box-seam' },
  { label: 'Chờ giao hàng', status: 'SHIPPING', icon: 'bi bi-truck' },
  { label: 'Đã giao', status: 'DELIVERED', icon: 'bi bi-check-circle' },
  { label: 'Đã hủy', status: 'CANCELLED', icon: 'bi bi-x-circle' }
]

const getUserFromStorage = () => {
  const userStorage = localStorage.getItem('user')
  return userStorage ? JSON.parse(userStorage) : null
}

const fetchOrders = async () => {
  loading.value = true
  const user = getUserFromStorage()
  if (!user) {
    router.push('/login')
    return
  }

  try {
    const res = await axios.get(`http://localhost:8080/api/orders?userId=${user.id}&status=${currentStatus.value}`)
    orders.value = res.data
  } catch (err) {
    console.error("Lỗi tải đơn hàng:", err)
  } finally {
    loading.value = false
  }
}

const changeTab = (status) => {
  currentStatus.value = status
  router.push({ path: '/orders', query: { status } })
}

const handleCancelOrder = async (orderId) => {
  if (confirm("Bạn có chắc chắn muốn hủy đơn hàng này không?")) {
    try {
      await axios.put(`http://localhost:8080/api/orders/${orderId}/cancel`)
      alert("Đã hủy đơn hàng thành công!")
      fetchOrders()
    } catch (err) {
      alert("Không thể hủy đơn hàng lúc này.")
    }
  }
}

watch(() => route.query.status, (newStatus) => {
  if (newStatus) {
    currentStatus.value = newStatus
    fetchOrders()
  }
})

const formatPrice = (price) => {
  if (!price && price !== 0) return '0 đ'
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price)
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('vi-VN')
}

const getStatusBadgeClass = (status) => {
  switch (status) {
    case 'PENDING': return 'bg-warning text-dark'
    case 'PROCESSING': return 'bg-info text-white'
    case 'SHIPPING': return 'bg-primary text-white'
    case 'DELIVERED': return 'bg-success text-white'
    case 'CANCELLED': return 'bg-danger text-white'
    default: return 'bg-secondary text-white'
  }
}

const getStatusText = (status) => {
  switch (status) {
    case 'PENDING': return 'Chờ xác nhận'
    case 'PROCESSING': return 'Chờ lấy hàng'
    case 'SHIPPING': return 'Đang giao hàng'
    case 'DELIVERED': return 'Đã giao hàng'
    case 'CANCELLED': return 'Đã hủy'
    default: return status
  }
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.order-item-img {
  width: 56px;
  height: 56px;
  object-fit: contain;
  background-color: #fafafa;
}

.tab-btn {
  color: #6c757d;
  background-color: transparent;
  transition: all 0.2s ease-in-out;
}

.tab-btn:hover {
  background-color: #f8f9fa;
  color: #000;
}

.active-tab {
  color: #000 !important;
  border-bottom: 3px solid #ffc107 !important;
  background-color: #fffdf0 !important;
}
</style>