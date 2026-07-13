<template>
  <div id="app" class="d-flex flex-column min-vh-100 bg-light">
    <!-- 1. Gắn Thanh Nav -->
    <NavbarComponent />

    <!-- 2. Phần nội dung chính (Main Content) -->
    <main class="container flex-grow-1 my-4">
      <div class="row">
        <!-- Thanh menu bên trái (Sidebar danh mục tĩnh để làm ASM1) -->
        <aside class="col-lg-3 mb-4">
          <div class="card shadow-sm">
            <div class="card-header bg-warning text-dark fw-bold">
              <i class="bi bi-list-ul me-2"></i>Danh Mục Sản Phẩm
            </div>
            <ul class="list-group list-group-flush">
              <li class="list-group-item list-group-item-action">Điện thoại</li>
              <li class="list-group-item list-group-item-action">Laptop</li>
              <li class="list-group-item list-group-item-action">Máy tính bảng</li>
              <li class="list-group-item list-group-item-action">Tai nghe</li>
              <li class="list-group-item list-group-item-action">Đồng hồ thông minh</li>
            </ul>
          </div>
        </aside>

        <!-- Khu vực hiển thị nội dung chính -->
        <section class="col-lg-9">
          <div class="p-5 bg-white rounded shadow-sm text-center">
            <h2 class="text-dark fw-bold mb-3">Chào mừng đến với Thế Giới Điện Tử!</h2>
            <p class="text-muted">Giao diện tĩnh phục vụ báo cáo giai đoạn ASM1.</p>
            <hr class="my-4">
            <button class="btn btn-warning fw-bold text-dark px-4 shadow-sm">Xem Sản Phẩm Ngay</button>
          </div>
        </section>
      </div>
    </main>

    <!-- 3. Chân trang (Footer) -->
    <footer class="bg-dark text-light py-3 mt-auto border-top border-warning border-3">
      <div class="container text-center">
        <small>&copy; 2026 - Dự án ASM1 Java 6 - Phát triển bởi Nhóm Hội Nướng</small>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

// 1. Tạo một mảng rỗng để hứng dữ liệu từ Spring Boot đổ về
const categories = ref([])

// 2. Hàm gọi API từ Backend Spring Boot
const loadCategories = async () => {
  try {
    const response = await fetch('http://localhost:8080/api/categories')
    if (response.ok) {
      // Gán dữ liệu động từ DB vào mảng categories
      categories.value = await response.json()
    } else {
      console.error('Lỗi lấy dữ liệu từ server:', response.status)
    }
  } catch (error) {
    console.error('Không thể kết nối đến Spring Boot Backend:', error)
  }
}

// 3. Tự động chạy hàm loadCategories ngay khi trang web Vue vừa tải xong
onMounted(() => {
  loadCategories()
})
</script>

<style>
/* Cấu hình phông chữ toàn trang cho đẹp */
body {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}
</style>