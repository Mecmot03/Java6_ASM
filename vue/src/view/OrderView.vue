<template>
  <div class="container py-4">
    <!-- TIÊU ĐỀ TRANG -->
    <div class="d-flex align-items-center justify-content-between mb-4">
      <h3 class="fw-bold text-dark mb-0 d-flex align-items-center gap-2">
        <i class="bi bi-box-seam text-warning"></i> Quản Lý Đơn Hàng (Dành cho Nhân Viên)
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
      <p class="mt-2 text-muted">Đang tải danh sách đơn hàng toàn hệ thống...</p>
    </div>

    <!-- NẾU KHÔNG CÓ ĐƠN HÀNG NÀO -->
    <div v-else-if="orders.length === 0" class="card border-0 shadow-sm rounded-4 p-5 text-center">
      <i class="bi bi-inbox display-1 text-muted opacity-25 d-block mb-3"></i>
      <h5 class="fw-bold text-dark mb-1">Chưa có đơn hàng nào</h5>
      <p class="text-muted small mb-0">Không tìm thấy đơn hàng nào ở trạng thái này.</p>
    </div>

    <!-- DANH SÁCH ĐƠN HÀNG -->
    <div v-else class="d-flex flex-column gap-4">
      <div v-for="order in orders" :key="order.id" class="card border-0 shadow-sm rounded-4 p-4">
        
        <!-- HEADER ĐƠN HÀNG -->
        <div class="d-flex justify-content-between align-items-center border-bottom pb-3 mb-3">
          <div class="d-flex align-items-center gap-3 flex-wrap">
            <span class="fw-bold text-dark fs-5">Mã đơn hàng: #{{ order.id }}</span>
            <span class="badge rounded-pill border border-warning text-dark bg-warning bg-opacity-10 px-3 py-2">
              <i class="bi bi-person-badge me-1 text-warning"></i>
              Khách hàng #{{ order.userId || order.user?.id || 'N/A' }}
            </span>
            <small class="text-muted border-start ps-3">
              <i class="bi bi-clock me-1"></i>{{ formatDate(order.orderDate || order.createdDate) }}
            </small>
          </div>
          <span class="badge rounded-pill px-3 py-2 fw-bold" :class="getStatusBadgeClass(order.status)">
            {{ getStatusText(order.status) }}
          </span>
        </div>

        <!-- THÔNG TIN KHÁCH HÀNG -->
        <div class="customer-info-card mb-3 p-3 rounded-4 border bg-light small">
          <div class="fw-bold text-dark mb-2 d-flex align-items-center gap-2">
            <i class="bi bi-person-lines-fill text-warning"></i> Thông tin giao hàng
          </div>
          <div class="row g-2">
            <div class="col-md-4">
              <i class="bi bi-person me-1 text-secondary"></i>
              <strong>Người nhận:</strong> {{ order.receiverName || order.fullname || order.user?.fullname || 'Chưa cập nhật' }}
            </div>
            <div class="col-md-4">
              <i class="bi bi-telephone me-1 text-secondary"></i>
              <strong>Số điện thoại:</strong> {{ order.receiverPhone || order.phone || order.user?.phone || 'Chưa cập nhật' }}
            </div>
            <div class="col-md-4">
              <i class="bi bi-credit-card me-1 text-secondary"></i>
              <strong>Thanh toán:</strong> {{ (order.paymentMethod === 'COD' || !order.paymentMethod) ? 'Thanh toán khi nhận (COD)' : 'Chuyển khoản' }}
            </div>
            <div class="col-12 mt-1">
              <i class="bi bi-geo-alt me-1 text-secondary"></i>
              <strong>Địa chỉ:</strong> {{ order.shippingAddress || order.address || 'Chưa cập nhật' }}
            </div>
          </div>
        </div>

        <!-- DANH SÁCH SẢN PHẨM TRONG ĐƠN HÀNG -->
        <div 
          class="order-items-list border-top border-bottom py-2 mb-3" 
          v-if="(order.items || order.orderItems) && (order.items || order.orderItems).length > 0"
        >
          <div 
            v-for="item in (order.items || order.orderItems)" 
            :key="item.id" 
            class="d-flex align-items-center justify-content-between py-2"
          >
            <div class="d-flex align-items-center gap-3">
              <img 
                :src="getProductImage(item)" 
                class="order-item-img rounded-3 border p-1" 
                alt="Product" 
                @error="(e) => e.target.src = 'https://via.placeholder.com/80?text=No+Image'"
              />
              <div>
                <h6 class="fw-bold text-dark mb-1">
                  {{ getProductName(item) }}
                </h6>
                <small class="text-muted">Số lượng: x{{ item.quantity }}</small>
              </div>
            </div>
            <div class="fw-bold text-dark">
              {{ formatPrice((item.price || item.product?.price || 0) * item.quantity) }}
            </div>
          </div>
        </div>

        <div v-else class="py-2 text-muted small border-top border-bottom mb-3">
          <i class="bi bi-bag-check me-1"></i> Đơn hàng bao gồm các sản phẩm đã được xác nhận.
        </div>

        <!-- FOOTER ĐƠN HÀNG (THAO TÁC CỦA NHÂN VIÊN) -->
        <div class="d-flex justify-content-between align-items-center pt-2">
          <div v-if="order.status === 'PENDING'" class="d-flex align-items-center gap-2">
            <button class="btn btn-warning btn-sm rounded-pill px-3 fw-bold text-dark shadow-sm" @click="handleConfirmOrder(order.id)">
              <i class="bi bi-check-circle me-1"></i> Xác nhận đơn hàng
            </button>
            <button class="btn btn-outline-secondary btn-sm rounded-pill px-3" @click="handleCancelOrder(order.id)">
              <i class="bi bi-x-circle me-1"></i> Hủy đơn hàng
            </button>
          </div>

          <div v-else-if="order.status === 'PROCESSING'" class="d-flex align-items-center gap-2">
            <button class="btn btn-primary btn-sm rounded-pill px-3 fw-bold shadow-sm" @click="handleShipOrder(order.id)">
              <i class="bi bi-truck me-1"></i> Chuyển sang chờ giao hàng
            </button>
            <button class="btn btn-outline-secondary btn-sm rounded-pill px-3" @click="handleCancelOrder(order.id)">
              <i class="bi bi-x-circle me-1"></i> Hủy đơn hàng
            </button>
          </div>

          <div v-else-if="order.status === 'SHIPPING'" class="d-flex align-items-center gap-2">
            <button class="btn btn-success btn-sm rounded-pill px-3 fw-bold shadow-sm" @click="handleDeliverOrder(order.id)">
              <i class="bi bi-box-arrow-in-down-left me-1"></i> Xác nhận đã giao
            </button>
          </div>

          <div class="ms-auto text-end">
            <span class="text-muted small me-2">Tổng thanh toán:</span>
            <span class="fw-bold text-danger fs-4">{{ formatPrice(order.totalAmount || order.totalPrice || 0) }}</span>
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
import { confirmDialog } from '../utils/dialog'
import { notify } from '../utils/notify'

const route = useRoute()
const router = useRouter()

const orders = ref([])
const loading = ref(true)
const currentStatus = ref(route.query.status || 'PENDING')
const productMap = ref({})

const tabs = [
  { label: 'Chờ xác nhận', status: 'PENDING', icon: 'bi bi-clock' },
  { label: 'Chờ lấy hàng', status: 'PROCESSING', icon: 'bi bi-box-seam' },
  { label: 'Chờ giao hàng', status: 'SHIPPING', icon: 'bi bi-truck' },
  { label: 'Đã giao', status: 'DELIVERED', icon: 'bi bi-check-circle' },
  { label: 'Đã hủy', status: 'CANCELLED', icon: 'bi bi-x-circle' }
]

const getUserFromStorage = () => {
  const userStorage = localStorage.getItem('user')
  try {
    return userStorage ? JSON.parse(userStorage) : null
  } catch (e) {
    return null
  }
}

// 🛑 CHỈ CHO PHÉP ROLE_STAFF (NHÂN VIÊN) TRUY CẬP
const isStaffUser = () => {
  const user = getUserFromStorage()
  if (!user) return false
  
  const roles = user.roles || (user.role ? [user.role] : [])
  const userStr = JSON.stringify(user).toUpperCase()

  return (
    roles.includes('ROLE_STAFF') || 
    roles.includes('STAFF') ||
    userStr.includes('ROLE_STAFF')
  )
}

const getProductName = (item) => {
  if (item.productName) return item.productName
  if (item.product?.name) return item.product.name
  const pId = item.productId || item.product?.id || item.id
  if (pId && productMap.value[pId]?.name) {
    return productMap.value[pId].name
  }
  return `Sản phẩm #${pId}`
}

const getProductImage = (item) => {
  const imgName = item.productImage || item.product?.image || item.image
  if (imgName) {
    if (imgName.startsWith('http') || imgName.startsWith('/')) return imgName
    return `/images/${imgName}`
  }
  
  const pId = item.productId || item.product?.id || item.id
  if (pId && productMap.value[pId]?.image) {
    const mappedImg = productMap.value[pId].image
    if (mappedImg.startsWith('http') || mappedImg.startsWith('/')) return mappedImg
    return `/images/${mappedImg}`
  }
  
  return 'https://via.placeholder.com/80?text=No+Image'
}

const getAuthHeaders = () => {
  const token = localStorage.getItem('token')
  return token ? { headers: { Authorization: `Bearer ${token}` } } : {}
}

const fetchOrders = async () => {
  loading.value = true
  const user = getUserFromStorage()

  if (!user) {
    router.push('/login')
    return
  }

  // 🔴 Nếu không phải STAFF (Admin hoặc User thường) thì đuổi về ngay
  if (!isStaffUser()) {
    notify("Bạn không có quyền truy cập trang Quản lý đơn hàng dành cho Nhân viên!", 'danger')
    router.push(user.roles?.includes('ROLE_ADMIN') || user.role === 'ROLE_ADMIN' ? '/admin' : '/')
    return
  }

  try {
    const query = new URLSearchParams({ status: currentStatus.value })
    
    // 1. Tải danh sách đơn hàng cho Staff
    const res = await axios.get(`/api/orders?${query.toString()}`, getAuthHeaders())
    orders.value = res.data || []

    // 2. Tải danh sách sản phẩm để Map tên/hình
    try {
      const prodRes = await axios.get('/api/products')
      const pList = prodRes.data.content || prodRes.data || []
      const map = {}
      pList.forEach(p => { map[p.id] = p })
      productMap.value = map
    } catch (e) {
      console.warn("Chưa thể tải danh sách sản phẩm:", e)
    }

  } catch (err) {
    console.error("Lỗi tải đơn hàng:", err)
    notify("Không thể tải danh sách đơn hàng.", 'danger')
  } finally {
    loading.value = false
  }
}

const changeTab = (status) => {
  currentStatus.value = status
  router.push({ path: '/orders', query: { status } })
}

const switchToStatusTab = (status) => {
  if (currentStatus.value === status && route.query.status === status) {
    fetchOrders()
    return
  }
  changeTab(status)
}

const handleConfirmOrder = async (orderId) => {
  if (!(await confirmDialog("Xác nhận đơn này sang trạng thái chờ lấy hàng?"))) return

  try {
    await axios.put(`/api/orders/${orderId}/confirm`, {}, getAuthHeaders())
    notify("Đơn hàng đã chuyển sang trạng thái chờ lấy hàng.", 'success')
    switchToStatusTab('PROCESSING')
  } catch (err) {
    notify(err.response?.data?.message || err.response?.data || "Không thể xác nhận đơn hàng.", 'danger')
  }
}

const handleShipOrder = async (orderId) => {
  if (!(await confirmDialog("Xác nhận đơn này sang trạng thái chờ giao hàng?"))) return

  try {
    await axios.put(`/api/orders/${orderId}/ship`, {}, getAuthHeaders())
    notify("Đơn hàng đã chuyển sang chờ giao hàng.", 'success')
    switchToStatusTab('SHIPPING')
  } catch (err) {
    notify(err.response?.data?.message || err.response?.data || "Không thể cập nhật trạng thái.", 'danger')
  }
}

const handleDeliverOrder = async (orderId) => {
  if (!(await confirmDialog("Xác nhận đơn này đã giao thành công?"))) return

  try {
    await axios.put(`/api/orders/${orderId}/deliver`, {}, getAuthHeaders())
    notify("Đơn hàng đã được xác nhận giao thành công.", 'success')
    switchToStatusTab('DELIVERED')
  } catch (err) {
    notify(err.response?.data?.message || err.response?.data || "Không thể cập nhật trạng thái.", 'danger')
  }
}

const handleCancelOrder = async (orderId) => {
  if (!(await confirmDialog("Bạn có chắc chắn muốn hủy đơn hàng này không?"))) return

  try {
    await axios.put(`/api/orders/${orderId}/cancel`, {}, getAuthHeaders())
    notify("Đã hủy đơn hàng thành công!", 'success')
    switchToStatusTab('CANCELLED')
  } catch (err) {
    notify(err.response?.data?.message || err.response?.data || "Không thể hủy đơn hàng.", 'danger')
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
  return isNaN(date.getTime()) ? dateStr : date.toLocaleString('vi-VN')
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