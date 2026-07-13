<template>
  <nav class="navbar navbar-expand-lg navbar-dark bg-dark shadow">
    <div class="container">
      <!-- Logo / Tên thương hiệu -->
      <a class="navbar-brand fw-bold text-warning" href="#">
        <i class="bi bi-laptop me-2"></i>THE GIOI DIEN TU
      </a>
      
      <button class="navbar-expand-lg navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
        <span class="navbar-toggler-icon"></span>
      </button>

      <div class="collapse navbar-collapse" id="navbarNav">
        <!-- Menu điều hướng chính -->
        <ul class="navbar-nav me-auto mb-2 mb-lg-0">
          <li class="nav-item">
            <a class="nav-link active" href="#"><i class="bi bi-house-door me-1"></i> Trang chủ</a>
          </li>
          <li class="nav-item">
            <a class="nav-link" href="#"><i class="bi bi-grid me-1"></i> Danh mục</a>
          </li>
          <li class="nav-item">
            <a class="nav-link" href="#"><i class="bi bi-box-seam me-1"></i> Sản phẩm</a>
          </li>
        </ul>

        <!-- Thanh tìm kiếm & Giỏ hàng / Tài khoản -->
        <div class="d-flex align-items-center gap-3">
          <form class="d-flex" role="search">
            <input class="form-control form-control-sm me-2" type="search" placeholder="Tìm sản phẩm..." aria-label="Search">
            <button class="btn btn-outline-warning btn-sm" type="submit"><i class="bi bi-search"></i></button>
          </form>

          <!-- Giỏ hàng -->
          <a href="#" class="btn btn-outline-light btn-sm position-relative">
            <i class="bi bi-cart3 fs-5"></i>
            <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">0</span>
          </a>

          <!-- Tài khoản (Sau này bấm vào sẽ ra trang Sign In của bạn) -->
          <a href="#" class="text-light text-decoration-none d-flex align-items-center gap-1 ms-2">
            <i class="bi bi-person-circle fs-4 text-warning"></i>
            <span class="d-none d-md-inline small">Tài khoản</span>
          </a>
        </div>
      </div>
    </div>
  </nav>
</template>

<script>
export default {
  name: 'NavbarComponent'
}
</script>

<style scoped>
/* Bạn có thể tùy chỉnh thêm CSS riêng ở đây nếu muốn */
.nav-link {
  transition: color 0.3s ease;
}
.nav-link:hover {
  color: #ffc107 !important; /* Đổi màu vàng khi hover giống màu hệ thống */
}
</style>