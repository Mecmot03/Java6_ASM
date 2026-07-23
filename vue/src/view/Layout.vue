<template>
  <div class="d-flex flex-column min-vh-100 w-100">
    <!-- HEADER -->
    <header class="tgdd-header w-100 sticky-header" :class="{ 'header-hidden': isHeaderHidden }">
      <!-- 1. MAIN NAV (Logo, Ô tìm kiếm, Utility) -->
      <div class="main-nav py-2">
        <div class="container d-flex align-items-center justify-content-between gap-3">
          <a href="#" class="logo text-decoration-none" @click.prevent="goHome">
            <span class="logo-icon">⚡</span>
            <span class="logo-text">thegioidientu</span>
          </a>

          <div class="search-box flex-grow-1 mx-3" style="max-width: 450px;">
            <div class="input-group">
              <span class="input-group-text bg-white border-end-0 rounded-start-pill text-muted">
                <i class="bi bi-search"></i>
              </span>
              <input type="text" class="form-control border-start-0 rounded-end-pill ps-0"
                placeholder="Mua OPPO Reno16 Series..." v-model="searchQuery" @keyup.enter="handleSearch" />
            </div>
          </div>

          <!-- Utilities -->
          <div class="nav-utilities d-flex align-items-center gap-3">

            <!-- 1. CHƯA ĐĂNG NHẬP -->
            <router-link v-if="!currentUser" to="/login" class="nav-item-link">
              <i class="bi bi-person-circle"></i>
              <span>Đăng nhập</span>
            </router-link>

            <!-- 2. ĐÃ ĐĂNG NHẬP: HIỂN THỊ TÊN USER -->
            <button
              v-else
              type="button"
              class="nav-item-link text-dark fw-bold nav-user-btn"
              :title="isAdmin ? 'Vào trang quản trị' : 'Thông tin tài khoản'"
              @click="handleUserArea"
            >
              <i class="bi bi-person-circle fs-5"></i>
              <span class="text-truncate" style="max-width: 100px;"
                :title="currentUser.fullName || currentUser.username">
                {{ currentUser.fullName || currentUser.username }}
              </span>
            </button>
            <router-link
              v-if="currentUser && !isAdmin"
              to="/order-history?status=PENDING"
              class="nav-item-link nav-history-link ms-1"
              title="Lịch sử đặt hàng"
            >
              <i class="bi bi-clock-history"></i>
              <span>Lịch sử đặt hàng</span>
            </router-link>

            <!-- 3. ICON GIỎ HÀNG -->
            <router-link to="/cart" class="nav-item-link position-relative">
              <i class="bi bi-cart3"></i>
              <span>Giỏ hàng</span>
              <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger"
                v-if="cartCount > 0">
                {{ cartCount }}
              </span>
            </router-link>

            <!-- 4. DROPDOWN THEO DÕI ĐƠN HÀNG -->
            <div v-if="isAdmin" class="dropdown">
              <button class="btn btn-link nav-item-link text-dark text-decoration-none p-0 border-0 dropdown-toggle"
                type="button" data-bs-toggle="dropdown" aria-expanded="false">
                <i class="bi bi-box-seam"></i>
                <span>Đơn hàng</span>
              </button>
              <ul class="dropdown-menu dropdown-menu-end shadow-lg border-0 rounded-3 mt-2 p-2"
                style="min-width: 200px;">
                <li class="dropdown-header text-uppercase fw-bold fs-8 text-secondary">Trạng thái đơn hàng</li>
                <li>
                  <router-link to="/orders?status=PENDING" class="dropdown-item rounded-2 py-1 small">
                    <i class="bi bi-clock me-2 text-warning"></i>Chờ xác nhận
                  </router-link>
                </li>
                <li>
                  <router-link to="/orders?status=PROCESSING" class="dropdown-item rounded-2 py-1 small">
                    <i class="bi bi-box-seam me-2 text-info"></i>Chờ lấy hàng
                  </router-link>
                </li>
                <li>
                  <router-link to="/orders?status=SHIPPING" class="dropdown-item rounded-2 py-1 small">
                    <i class="bi bi-truck me-2 text-primary"></i>Chờ giao hàng
                  </router-link>
                </li>
                <li>
                  <router-link to="/orders?status=DELIVERED" class="dropdown-item rounded-2 py-1 small">
                    <i class="bi bi-check-circle me-2 text-success"></i>Đã giao
                  </router-link>
                </li>
                <li>
                  <router-link to="/orders?status=CANCELLED" class="dropdown-item rounded-2 py-1 small">
                    <i class="bi bi-x-circle me-2 text-danger"></i>Đã hủy
                  </router-link>
                </li>
              </ul>
            </div>

            <!-- 5. NÚT ĐĂNG XUẤT -->
            <button v-if="currentUser" class="btn btn-link nav-item-link text-danger text-decoration-none p-0 border-0"
              @click="handleLogout" title="Đăng xuất">
              <i class="bi bi-box-arrow-right"></i>
              <span>Đăng xuất</span>
            </button>

          </div>
        </div>
      </div>

      <!-- 2. DANH MỤC -->
      <nav class="categories-nav d-none d-md-block py-2 border-top border-dark border-opacity-10">
        <div class="container d-flex justify-content-between align-items-center text-nowrap overflow-x-auto gap-2">
          <a href="#" class="category-item" @click.prevent="selectCategory(null)">
            <i class="bi bi-grid-fill"></i>
            <span>Tất cả</span>
          </a>

          <a v-for="menu in menuItems" :key="menu.id" href="#" class="category-item"
            @click.prevent="selectCategory(menu.id)">
            <img v-if="menu.img" :src="menu.img" :alt="menu.name" class="category-icon-img" />
            <i v-else :class="menu.icon"></i>
            <span>{{ menu.name }}</span>
            <i v-if="menu.hasSub" class="bi bi-chevron-down ms-1 small-arrow"></i>
          </a>
        </div>
      </nav>
    </header>

    <!-- BANNER 2 BÊN -->
    <div v-if="isHomePage" class="side-banner side-banner-left d-none d-xl-block">
      <router-link to="/flashsale">
        <img src="/images/mi.png" alt="Banner Trai" class="img-fluid rounded-3 shadow-sm" />
      </router-link>
    </div>

    <div v-if="isHomePage" class="side-banner side-banner-right d-none d-xl-block">
      <router-link to="/flashsale">
        <img src="/images/ni.png" alt="Banner Phai" class="img-fluid rounded-3 shadow-sm" />
      </router-link>
    </div>

    <!-- MAIN CONTENT -->
    <main class="flex-grow-1 my-4">
      <slot />
    </main>

    <div
      v-if="toastVisible"
      class="app-toast position-fixed end-0 top-0 m-4 shadow-lg rounded-4 px-4 py-3"
      :class="toastClasses"
      role="status"
      aria-live="polite"
    >
      <div class="d-flex align-items-start gap-3">
        <i :class="toastIcon" class="fs-5 mt-1"></i>
        <div class="flex-grow-1">
          <div class="fw-bold mb-1">Thông báo</div>
          <div class="small">{{ toastMessage }}</div>
        </div>
        <button type="button" class="btn-close btn-close-white ms-2" aria-label="Đóng" @click="hideToast"></button>
      </div>
    </div>

    <div
      v-if="confirmVisible"
      class="app-confirm-backdrop position-fixed top-0 start-0 w-100 h-100 d-flex align-items-center justify-content-center"
      @click.self="cancelConfirm"
    >
      <div class="app-confirm-modal bg-white shadow-lg rounded-4 p-4 mx-3">
        <div class="d-flex align-items-start gap-3 mb-3">
          <div class="app-confirm-icon rounded-circle d-flex align-items-center justify-content-center">
            <i class="bi bi-question-circle-fill fs-4"></i>
          </div>
          <div class="flex-grow-1">
            <h5 class="fw-bold mb-1">{{ confirmTitle }}</h5>
            <p class="mb-0 text-secondary">{{ confirmMessage }}</p>
          </div>
        </div>

        <div class="d-flex justify-content-end gap-2">
          <button type="button" class="btn btn-outline-secondary rounded-pill px-4" @click="cancelConfirm">
            Hủy
          </button>
          <button type="button" class="btn btn-warning rounded-pill px-4 fw-bold text-dark" @click="acceptConfirm">
            Đồng ý
          </button>
        </div>
      </div>
    </div>

    <!-- FOOTER ĐẦY ĐỦ -->
    <footer class="tgdd-footer bg-white border-top mt-5 pt-4 pb-3 w-100">
      <div class="container">
        <div class="row g-4">

          <div class="col-12 col-sm-6 col-md-3">
            <h6 class="fw-bold text-dark text-uppercase mb-3">Tổng đài hỗ trợ</h6>
            <ul class="list-unstyled footer-links">
              <li>Gọi mua: <a href="tel:1900232460" class="fw-bold text-primary text-decoration-none">1900 232 460</a>
                (8:00 - 21:30)</li>
              <li>Khiếu nại: <a href="tel:18001062" class="fw-bold text-primary text-decoration-none">1800 1062</a>
                (8:00 - 21:30)</li>
              <li>Bảo hành: <a href="tel:1900232464" class="fw-bold text-primary text-decoration-none">1900 232 464</a>
                (8:00 - 21:00)</li>
            </ul>

            <div class="mt-3">
              <h6 class="fw-bold text-dark text-sm mb-2">Kết nối với chúng tôi</h6>
              <div class="d-flex gap-2">
                <a href="#" class="social-icon facebook"><i class="bi bi-facebook"></i></a>
                <a href="#" class="social-icon youtube"><i class="bi bi-youtube"></i></a>
                <a href="#" class="social-icon tiktok"><i class="bi bi-tiktok"></i></a>
              </div>
            </div>
          </div>

          <div class="col-12 col-sm-6 col-md-3">
            <h6 class="fw-bold text-dark text-uppercase mb-3">Về công ty</h6>
            <ul class="list-unstyled footer-links">
              <li v-for="(link, index) in companyLinks" :key="index">
                <a :href="link.url">{{ link.text }}</a>
              </li>
            </ul>
          </div>

          <div class="col-12 col-sm-6 col-md-3">
            <h6 class="fw-bold text-dark text-uppercase mb-3">Chính sách & Nhóm sản phẩm</h6>
            <ul class="list-unstyled footer-links">
              <li v-for="(link, index) in policyLinks" :key="index">
                <a :href="link.url">{{ link.text }}</a>
              </li>
            </ul>
          </div>

          <div class="col-12 col-sm-6 col-md-3">
            <h6 class="fw-bold text-dark text-uppercase mb-3">Hệ thống cửa hàng</h6>
            <ul class="list-unstyled footer-links mb-3">
              <li><a href="#">Xem 3.000 siêu thị toàn quốc</a></li>
              <li><a href="#">Chính sách giao hàng, lắp đặt</a></li>
              <li><a href="#">Tìm cửa hàng gần nhất</a></li>
            </ul>

            <div class="d-flex flex-wrap gap-2 align-items-center mt-2">
              <span class="badge bg-primary px-2 py-1 text-uppercase small">Bộ công thương</span>
              <span class="badge bg-success px-2 py-1 text-uppercase small">Đã thông báo</span>
              <span class="badge bg-dark px-2 py-1 text-uppercase small">DMCA Protected</span>
            </div>
          </div>

        </div>

        <!-- BẢN QUYỀN DƯỚI CÙNG -->
        <div class="border-top mt-4 pt-3 text-center text-muted fs-7">
          <p class="mb-0">© 2026 thegioidientu. All rights reserved.</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import axios from 'axios'
import { getGuestCartCount } from '../utils/cart'
import { confirmDialog, resolveConfirmDialog } from '../utils/dialog'

const router = useRouter()
const route = useRoute()

const searchQuery = ref('')
const cartCount = ref(0)
const menuItems = ref([])
const currentUser = ref(null)
const toastVisible = ref(false)
const toastMessage = ref('')
const toastType = ref('info')
const confirmVisible = ref(false)
const confirmTitle = ref('')
const confirmMessage = ref('')
const confirmId = ref(null)
let toastTimer = null

const isHeaderHidden = ref(false)
let lastScrollPosition = 0
let ticking = false

// Danh sách link cho Footer
const companyLinks = ref([
  { text: 'Giới thiệu công ty (MWG.vn)', url: '#' },
  { text: 'Tuyển dụng', url: '#' },
  { text: 'Gửi góp ý, khiếu nại', url: '#' },
  { text: 'Tìm siêu thị (3.000 shop)', url: '#' }
])

const policyLinks = ref([
  { text: 'Chính sách bảo hành', url: '#' },
  { text: 'Chính sách đổi trả', url: '#' },
  { text: 'Giao hàng & Lắp đặt', url: '#' },
  { text: 'Hướng dẫn mua trả góp', url: '#' }
])

const isHomePage = computed(() => route.path === '/')
const isAdmin = computed(() => {
  const role = currentUser.value?.role
  return role === 'ROLE_ADMIN' || role === 'ADMIN'
})

const toastClasses = computed(() => {
  const map = {
    success: 'bg-success text-white',
    warning: 'bg-warning text-dark',
    danger: 'bg-danger text-white',
    info: 'bg-dark text-white',
  }

  return map[toastType.value] || map.info
})

const toastIcon = computed(() => {
  const map = {
    success: 'bi bi-check-circle-fill',
    warning: 'bi bi-exclamation-triangle-fill',
    danger: 'bi bi-x-circle-fill',
    info: 'bi bi-info-circle-fill',
  }

  return map[toastType.value] || map.info
})

const hideToast = () => {
  toastVisible.value = false

  if (toastTimer) {
    clearTimeout(toastTimer)
    toastTimer = null
  }
}

const showToast = (message, type = 'info') => {
  toastMessage.value = message
  toastType.value = type
  toastVisible.value = true

  if (toastTimer) {
    clearTimeout(toastTimer)
  }

  toastTimer = setTimeout(() => {
    toastVisible.value = false
    toastTimer = null
  }, 2600)
}

const showConfirm = (event) => {
  const detail = event?.detail || {}
  confirmId.value = detail.id ?? null
  confirmTitle.value = detail.title || 'Xác nhận'
  confirmMessage.value = detail.message || ''
  confirmVisible.value = true
}

const closeConfirm = (confirmed) => {
  if (confirmId.value != null) {
    resolveConfirmDialog(confirmId.value, confirmed)
  }

  confirmVisible.value = false
  confirmId.value = null
  confirmTitle.value = ''
  confirmMessage.value = ''
}

const acceptConfirm = () => closeConfirm(true)
const cancelConfirm = () => closeConfirm(false)

const goHome = () => {
  searchQuery.value = ''
  router.push('/')
}

const handleUserArea = () => {
  if (isAdmin.value) {
    router.push('/admin/users')
    return
  }

  router.push('/user-info')
}

const iconMap = {
  "Điện thoại": "bi bi-phone",
  "Laptop": "bi bi-laptop",
  "Máy tính bảng": "bi bi-tablet",
  "Tai nghe": "bi bi-headphones",
  "Đồng hồ thông minh": "bi bi-watch",
  "Bàn phím": "bi bi-keyboard",
  "Chuột": "bi bi-mouse",
  "Cáp sạc": "bi bi-usb-plug"
}

const isAuthError = (error) => {
  const status = error?.response?.status
  return status === 401 || status === 403
}

// Kiểm tra thông tin User đã đăng nhập
const checkUserLogin = () => {
  const userStorage = localStorage.getItem('user')
  if (userStorage) {
    try {
      currentUser.value = JSON.parse(userStorage)
    } catch (e) {
      currentUser.value = null
    }
  } else {
    currentUser.value = null
  }
}

// Xử lý Đăng xuất
const handleLogout = () => {
  confirmDialog("Bạn có chắc chắn muốn đăng xuất khỏi tài khoản?").then((confirmed) => {
    if (!confirmed) {
      return
    }

    localStorage.removeItem('token')
    localStorage.removeItem('user')
    currentUser.value = null
    fetchCartCount() // Reset giỏ hàng về 0 khi đăng xuất
    showToast("Đã đăng xuất thành công!", 'success')
    router.push('/login')
  })
}

// Xử lý sự kiện cuộn mượt
const handleScroll = () => {
  if (!ticking) {
    window.requestAnimationFrame(() => {
      const currentScroll = window.scrollY || document.documentElement.scrollTop

      if (currentScroll <= 80) {
        isHeaderHidden.value = false
      } else {
        const diff = currentScroll - lastScrollPosition
        if (Math.abs(diff) > 8) {
          isHeaderHidden.value = diff > 0
          lastScrollPosition = currentScroll
        }
      }
      ticking = false
    })
    ticking = true
  }
}

const fetchCategories = async () => {
  try {
    const response = await axios.get('/api/categories')
    menuItems.value = response.data.map(cat => ({
      id: cat.id || cat.Id,
      name: cat.name || cat.Name,
      icon: iconMap[cat.name || cat.Name] || 'bi bi-grid',
      hasSub: false
    }))
  } catch (error) {
    menuItems.value = []
    if (!isAuthError(error)) {
      console.error("Lỗi khi tải danh mục:", error)
    }
  }
}

// Lấy số lượng giỏ hàng thực tế từ API (Chỉ gọi khi ĐÃ ĐĂNG NHẬP)
const fetchCartCount = async () => {
  try {
    const userStorage = localStorage.getItem('user')
    if (!userStorage) {
      cartCount.value = getGuestCartCount()
      return
    }

    const userId = JSON.parse(userStorage)?.id
    if (!userId) {
      cartCount.value = 0
      return
    }

    const response = await axios.get(`/api/cart/count?userId=${userId}`)
    cartCount.value = response.data.count || 0

  } catch (error) {
    if (!isAuthError(error)) {
      console.error("Lỗi khi lấy số lượng giỏ hàng:", error)
    }
    cartCount.value = 0
  }
}

const selectCategory = (categoryId) => {
  searchQuery.value = ''
  if (categoryId) {
    router.push({ path: '/', query: { categoryId: categoryId } })
  } else {
    router.push('/')
  }
}

const handleSearch = () => {
  const queryText = searchQuery.value ? searchQuery.value.trim() : ''
  if (queryText) {
    router.push({ path: '/', query: { keyword: queryText } })
  } else {
    router.push('/')
  }
}

// Lắng nghe Event từ các Component khác
const handleCartUpdated = () => {
  fetchCartCount()
}

const handleUserLoggedIn = () => {
  checkUserLogin()
  fetchCartCount()
}

const handleUserUpdated = () => {
  checkUserLogin()
}

const handleAppNotify = (event) => {
  const detail = event?.detail || {}
  showToast(detail.message || 'Đã cập nhật', detail.type || 'info')
}

const handleAppConfirm = (event) => {
  showConfirm(event)
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll, { passive: true })
  window.addEventListener('cart-updated', handleCartUpdated)
  window.addEventListener('user-logged-in', handleUserLoggedIn)
  window.addEventListener('user-updated', handleUserUpdated)
  window.addEventListener('app-notify', handleAppNotify)
  window.addEventListener('app-confirm', handleAppConfirm)

  checkUserLogin()
  fetchCategories()
  fetchCartCount()
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
  window.removeEventListener('cart-updated', handleCartUpdated)
  window.removeEventListener('user-logged-in', handleUserLoggedIn)
  window.removeEventListener('user-updated', handleUserUpdated)
  window.removeEventListener('app-notify', handleAppNotify)
  window.removeEventListener('app-confirm', handleAppConfirm)

  if (toastTimer) {
    clearTimeout(toastTimer)
  }
})
</script>

<style scoped>
.sticky-header {
  background-color: #ffd400;
  font-family: Arial, sans-serif;
  font-size: 14px;
  position: sticky;
  top: 0;
  z-index: 1000;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
  will-change: transform;
  transition: transform 0.28s cubic-bezier(0.16, 1, 0.3, 1);
}

.sticky-header.header-hidden {
  transform: translateY(-58px);
}

.app-toast {
  z-index: 2000;
  min-width: 320px;
  max-width: 420px;
  border: 0;
}

.app-confirm-backdrop {
  z-index: 2100;
  background: rgba(15, 23, 42, 0.45);
  backdrop-filter: blur(2px);
}

.app-confirm-modal {
  width: min(92vw, 440px);
}

.app-confirm-icon {
  width: 44px;
  height: 44px;
  background: rgba(255, 212, 0, 0.18);
  color: #b07f00;
  flex: 0 0 auto;
}

.logo {
  display: flex;
  align-items: center;
  font-weight: bold;
  color: #000;
}

.logo-icon {
  background-color: #000;
  color: #ffd400;
  border-radius: 8px;
  padding: 4px 8px;
  font-size: 18px;
  margin-right: 5px;
}

.logo-text {
  font-size: 23px;
  font-family: 'Arial Black', sans-serif;
  font-weight: 900;
  color: #000;
  letter-spacing: -1px;
  display: inline-block;
  transform: skewX(-15deg);
}

.search-box input:focus {
  box-shadow: none;
  border-color: #dee2e6;
}

.nav-item-link {
  color: #000;
  text-decoration: none;
  display: flex;
  flex-direction: column;
  align-items: center;
  font-size: 12px;
  font-weight: 500;
}

.nav-item-link i {
  font-size: 18px;
}

button.nav-item-link {
  background: transparent;
  border: none;
  cursor: pointer;
}

button.nav-item-link:hover {
  opacity: 0.8;
}

.nav-history-link {
  color: #000;
}

.nav-history-link:hover {
  opacity: 0.8;
}

.nav-history-link i {
  font-size: 18px;
}

.nav-user-btn {
  background: transparent;
  border: none;
  padding: 0;
}

.cursor-default {
  cursor: default;
}

.dropdown-toggle::after {
  display: none !important;
}

.category-item {
  color: #000;
  text-decoration: none;
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 500;
  padding: 4px 6px;
  border-radius: 4px;
  transition: all 0.2s;
}

.category-item:hover {
  background-color: rgba(0, 0, 0, 0.05);
}

.category-item i {
  font-size: 16px;
}

.small-arrow {
  font-size: 10px;
}

.categories-nav div::-webkit-scrollbar {
  display: none;
}

.categories-nav div {
  -ms-overflow-style: none;
  scrollbar-width: none;
}

.side-banner {
  position: fixed;
  top: 130px;
  z-index: 99;
  width: calc((100vw - 1230px) / 2);
  min-width: 100px;
  max-width: 140px;
}

.side-banner-left {
  left: max(10px, calc((100vw - 1200px) / 2 - 230px));
}

.side-banner-right {
  right: max(10px, calc((100vw - 1200px) / 2 - 230px));
}

.side-banner img {
  width: 100%;
  height: auto;
  object-fit: contain;
}

@media (max-width: 1400px) {
  .side-banner {
    display: none !important;
  }
}

.fs-7 {
  font-size: 12px;
}

.fs-8 {
  font-size: 11px;
}

.dropdown-item:active {
  background-color: #ffd400;
  color: #000;
}

/* CSS CHO FOOTER */
.footer-links {
  font-size: 13px;
}

.footer-links li {
  margin-bottom: 8px;
}

.footer-links a {
  color: #4a4a4a;
  text-decoration: none;
  transition: color 0.2s;
}

.footer-links a:hover {
  color: #007bff;
}

.social-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  color: #fff;
  text-decoration: none;
}

.social-icon.facebook {
  background-color: #1877f2;
}

.social-icon.youtube {
  background-color: #ff0000;
}

.social-icon.tiktok {
  background-color: #000000;
}
</style>