<template>
  <div class="d-flex flex-column min-vh-100 w-100">
    <!-- HEADER -->
    <header class="tgdd-header w-100">
      <div class="main-nav py-2">
        <div class="container d-flex align-items-center justify-content-between gap-3">

          <router-link to="/" class="logo text-decoration-none">
            <span class="logo-icon">⚡</span>
            <span class="logo-text">thegioidientu</span>
          </router-link>

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
          <div class="nav-utilities d-flex align-items-center gap-4">
            <router-link to="/login" class="nav-item-link">
              <i class="bi bi-person-circle"></i>
              <span>Đăng nhập</span>
            </router-link>

            <a href="#" class="nav-item-link position-relative">
              <i class="bi bi-cart3"></i>
              <span>Giỏ hàng</span>
              <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger"
                v-if="cartCount > 0">
                {{ cartCount }}
              </span>
            </a>

            <div class="location-box d-none d-lg-flex align-items-center ms-2">
              <i class="bi bi-geo-alt-fill me-1"></i>
              <select class="form-select form-select-sm bg-transparent border-0 fw-bold p-0 text-dark"
                style="padding-right: 24px !important;">
                <option>Hồ Chí Minh</option>
                <option>Hà Nội</option>
                <option>Đà Nẵng</option>
              </select>
            </div>
          </div>

        </div>
      </div>

      <!-- DANH MỤC -->
      <nav class="categories-nav d-none d-md-block py-2">
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

    <!-- FOOTER -->
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
      </div>
    </footer>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue' // 👈 ĐÃ THÊM computed VÀO ĐÂY
import { useRouter, useRoute } from 'vue-router' // 👈 ĐÃ THÊM useRoute VÀO ĐÂY
import axios from 'axios'

const router = useRouter()
const route = useRoute() // 👈 ĐÃ KHAI BÁO route

const searchQuery = ref('')
const cartCount = ref(0)
const menuItems = ref([])

// Kiểm tra trang hiện tại có phải /flashsale không
const isHomePage = computed(() => route.path === '/')
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

// Lấy danh mục từ DB
const fetchCategories = async () => {
  try {
    const response = await axios.get('http://localhost:8080/api/categories')
    menuItems.value = response.data.map(cat => ({
      id: cat.id,
      name: cat.name,
      icon: iconMap[cat.name] || 'bi bi-grid',
      hasSub: false
    }))
  } catch (error) {
    console.error("Lỗi khi tải danh mục:", error)
    menuItems.value = []
  }
}

// Hàm lấy số lượng sản phẩm trong giỏ hàng hiện tại
const fetchCartCount = async () => {
  try {
    const response = await axios.get('http://localhost:8080/api/cart/count')
    cartCount.value = response.data.count || 0
  } catch (error) {
    console.warn("Chưa lấy được số lượng giỏ hàng:", error.message)
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
  if (searchQuery.value.trim()) {
    router.push({ path: '/', query: { keyword: searchQuery.value.trim() } })
  } else {
    router.push('/')
  }
}

onMounted(() => {
  fetchCategories()
  fetchCartCount()
})

const companyLinks = ref([
  { text: 'Giới thiệu công ty (MWG)', url: '#' },
  { text: 'Tuyển dụng', url: '#' },
  { text: 'Gửi góp ý, khiếu nại', url: '#' },
  { text: 'Tìm siêu thị Thế Giới Di Động', url: '#' },
  { text: 'Xem bản điện thoại', url: '#' }
])

const policyLinks = ref([
  { text: 'Chính sách bảo hành', url: '#' },
  { text: 'Chính sách đổi trả', url: '#' },
  { text: 'Giao hàng & Thanh toán', url: '#' },
  { text: 'Hướng dẫn mua trả góp', url: '#' },
  { text: 'Chính sách bảo mật thông tin', url: '#' }
])
</script>

<style scoped>
/* ==================== STYLE CỦA HEADER ==================== */
.tgdd-header {
  background-color: #ffd400;
  font-family: Arial, sans-serif;
  font-size: 14px;
}

.main-nav {
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
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
  font-style: normal;
  font-weight: 900;
  background: linear-gradient(45deg, #000000);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
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

.location-box {
  background-color: rgba(255, 255, 255, 0.4);
  padding: 6px 12px;
  border-radius: 20px;
  white-space: nowrap;
}

.location-box select {
  cursor: pointer;
  min-width: 135px;
  text-align: left;
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

/* Banner 2 bên lề */
.side-banner {
  position: fixed;
  top: 170px;
  z-index: 99;
  width: 140px;
  transition: all 0.3s ease;
}

.side-banner-left {
  left: 20px; 
}

.side-banner-right {
  right: 20px; 
}

.side-banner img {
  width: 100%;
  height: auto;
  transition: transform 0.25s ease;
}

.side-banner:hover img {
  transform: scale(1.03);
}

/* FOOTER */
.tgdd-footer {
  font-family: Arial, sans-serif;
  font-size: 13px;
  color: #4a4a4a;
}

.tgdd-footer h6 {
  font-size: 14px;
  letter-spacing: 0.5px;
}

.footer-links li {
  margin-bottom: 8px;
}

.footer-links a {
  color: #2f80ed;
  text-decoration: none;
  transition: color 0.2s ease-in-out;
}

.footer-links a:hover {
  color: #e51f27;
  text-decoration: underline;
}

.social-icon {
  width: 32px;
  height: 32px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  color: #fff;
  font-size: 16px;
  text-decoration: none;
  transition: transform 0.2s;
}

.social-icon:hover {
  transform: scale(1.1);
}

.facebook {
  background-color: #1877f2;
}

.youtube {
  background-color: #ff0000;
}

.tiktok {
  background-color: #000000;
}
</style>