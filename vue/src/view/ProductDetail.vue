<template>
  <div class="container py-4">
    <!-- BREADCRUMB -->
    <nav class="mb-4 small" aria-label="breadcrumb">
      <ol class="breadcrumb mb-0">
        <li class="breadcrumb-item"><router-link to="/" class="text-decoration-none text-muted">Trang chủ</router-link></li>
        <li class="breadcrumb-item"><span class="text-muted">{{ product.category?.name || 'Sản phẩm' }}</span></li>
        <li class="breadcrumb-item active fw-bold text-dark" aria-current="page">{{ product.name }}</li>
      </ol>
    </nav>

    <!-- ================= KHỐI SẢN PHẨM CHÍNH ================= -->
    <div class="card border-0 shadow-sm rounded-4 p-4 mb-5 bg-white">
      <div class="row g-4">
        <!-- ẢNH SẢN PHẨM (TRÁI) -->
        <div class="col-lg-5">
          <div class="main-image-box rounded-4 border p-3 mb-3 text-center position-relative overflow-hidden bg-light">
            <span v-if="product.discount" class="badge bg-danger position-absolute top-0 start-0 m-3 px-3 py-2 rounded-pill fs-7">
              Giảm {{ product.discount.discountPercent }}%
            </span>
            <img
              :src="getImage(displayImages[selectedImage])"
              class="img-fluid main-image"
              :alt="product.name"
              @error="(e) => e.target.src = getImage(product.image)"
            >
          </div>

          <div class="d-flex gap-2 justify-content-center overflow-auto py-1">
            <img
              v-for="(image, index) in displayImages"
              :key="index"
              :src="getImage(image)"
              class="thumbnail rounded-3"
              :class="{ 'active-thumb': index === selectedImage }"
              @click="selectedImage = index"
              @error="(e) => e.target.src = getImage(product.image)"
            >
          </div>
        </div>

        <!-- THÔNG TIN & THAO TÁC (PHẢI) -->
        <div class="col-lg-7 d-flex flex-column justify-content-between">
          <div>
            <div class="d-flex align-items-center gap-2 mb-2">
              <span class="badge bg-warning text-dark fw-bold px-2 py-1">Chính hãng</span>
              <span class="text-muted small">Thương hiệu: <strong class="text-dark">{{ product.brand || 'Khác' }}</strong></span>
            </div>

            <h2 class="fw-bold text-dark mb-2 fs-3">{{ product.name }}</h2>

            <!-- Đánh giá nhanh -->
            <div class="d-flex align-items-center gap-2 mb-3">
              <div class="text-warning small">
                <i class="bi bi-star-fill me-1"></i>
                <span class="fw-bold text-dark">{{ reviewSummary.averageRating }}</span>
              </div>
              <span class="text-muted small">({{ reviewSummary.totalReviews }} đánh giá)</span>
              <span class="text-muted small">|</span>
              <span class="text-muted small">Mã SP: <strong>#{{ product.id }}</strong></span>
            </div>

            <!-- Khối Giá -->
            <div class="p-3 rounded-4 bg-light d-flex align-items-baseline gap-3 mb-3">
              <h2 class="text-danger fw-bold mb-0 fs-2">{{ formatPrice(calculatedPrice) }}</h2>
              <span v-if="product.discount" class="text-muted text-decoration-line-through fs-6">
                {{ formatPrice(product.price) }}
              </span>
            </div>

            <!-- Tình trạng kho -->
            <div class="mb-4 small">
              <span class="text-secondary">Tình trạng: </span>
              <span v-if="product.quantity > 0" class="badge bg-success-subtle text-success fw-bold px-2 py-1">
                <i class="bi bi-check-circle me-1"></i>Còn {{ product.quantity }} sản phẩm
              </span>
              <span v-else class="badge bg-danger-subtle text-danger fw-bold px-2 py-1">Hết hàng</span>
            </div>

            <!-- Số lượng mua -->
            <div class="d-flex align-items-center gap-3 mb-4">
              <span class="fw-bold small text-dark">Số lượng:</span>
              <div class="quantity-control d-inline-flex align-items-center border rounded-pill bg-light p-1">
                <button class="btn btn-sm btn-white rounded-circle shadow-none qty-btn" @click="decrease">-</button>
                <span class="px-3 fw-bold small">{{ buyQuantity }}</span>
                <button class="btn btn-sm btn-white rounded-circle shadow-none qty-btn" @click="increase">+</button>
              </div>
            </div>

            <!-- Nút mua & Yêu thích -->
            <div class="d-flex gap-3 mb-4">
              <button class="btn btn-warning rounded-pill fw-bold text-dark px-4 py-2 shadow-sm" @click="addToCart">
                <i class="bi bi-cart-plus me-2"></i>Thêm vào giỏ hàng
              </button>
              <button
                class="btn btn-lg rounded-pill px-4"
                :class="isFavorite ? 'btn-danger' : 'btn-outline-secondary'"
                @click="toggleFavorite"
              >
                <i class="bi" :class="isFavorite ? 'bi-heart-fill' : 'bi-heart'"></i>
              </button>
            </div>
          </div>

          <!-- KHỐI CAM KẾT / CHÍNH SÁCH BÁN HÀNG -->
          <div class="row g-2 border-top pt-3 text-secondary small">
            <div class="col-6 col-md-3 d-flex align-items-center gap-2">
              <i class="bi bi-truck fs-5 text-warning"></i>
              <span>Giao hàng toàn quốc</span>
            </div>
            <div class="col-6 col-md-3 d-flex align-items-center gap-2">
              <i class="bi bi-shield-check fs-5 text-warning"></i>
              <span>Bảo hành 12 tháng</span>
            </div>
            <div class="col-6 col-md-3 d-flex align-items-center gap-2">
              <i class="bi bi-arrow-counterclockwise fs-5 text-warning"></i>
              <span>Lỗi 1 đổi 1 30 ngày</span>
            </div>
            <div class="col-6 col-md-3 d-flex align-items-center gap-2">
              <i class="bi bi-headset fs-5 text-warning"></i>
              <span>Hỗ trợ 24/7</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- ================= KHỐI TABS THÔNG TIN & ĐÁNH GIÁ ================= -->
    <div class="card border-0 shadow-sm rounded-4 overflow-hidden mb-5 bg-white">
      <div class="border-bottom bg-light px-4 pt-3">
        <ul class="nav nav-tabs border-0 gap-3">
          <li class="nav-item">
            <button
              class="nav-link custom-tab fw-bold pb-3 border-0"
              :class="{ active: activeTab === 'desc' }"
              @click="activeTab = 'desc'"
            >
              Mô tả chi tiết
            </button>
          </li>
          <li class="nav-item">
            <button
              class="nav-link custom-tab fw-bold pb-3 border-0"
              :class="{ active: activeTab === 'reviews' }"
              @click="activeTab = 'reviews'"
            >
              Đánh giá & Bình luận ({{ reviewSummary.totalReviews }})
            </button>
          </li>
        </ul>
      </div>

      <div class="card-body p-4">
        <!-- TAB 1: MÔ TẢ -->
        <div v-if="activeTab === 'desc'" class="lh-lg text-secondary">
          <h5 class="fw-bold text-dark mb-3">Đặc điểm nổi bật</h5>
          <p>{{ product.description || 'Chưa có mô tả chi tiết cho sản phẩm này.' }}</p>
          <div class="text-muted small mt-4">
            <i class="bi bi-calendar-event me-1"></i> Ngày đăng sản phẩm:
            <strong>{{ formatDate(product.createdAt) }}</strong>
          </div>
        </div>

        <!-- TAB 2: ĐÁNH GIÁ & BÌNH LUẬN -->
        <div v-if="activeTab === 'reviews'">
          <div class="row g-4 bg-light rounded-4 p-4 mb-4 align-items-center">
            
            <!-- 1. Điểm trung bình từ Backend -->
            <div class="col-lg-3 text-center border-end-lg">
              <h1 class="display-3 fw-bold text-dark mb-0">{{ reviewSummary.averageRating }}</h1>
              <div class="text-warning my-1 fs-5">
                <i v-for="s in 5" :key="s" class="bi" :class="s <= Math.round(reviewSummary.averageRating) ? 'bi-star-fill' : 'bi-star text-muted'"></i>
              </div>
              <span class="text-muted small">Dựa trên <strong>{{ reviewSummary.totalReviews }}</strong> đánh giá</span>
            </div>

            <!-- 2. Cột thanh tiến trình 1 - 5 sao -->
            <div class="col-lg-5">
              <div v-for="star in [5, 4, 3, 2, 1]" :key="star" class="d-flex align-items-center gap-2 mb-1">
                <span class="small fw-bold text-dark" style="width: 40px;">{{ star }} sao</span>
                <div class="progress flex-grow-1" style="height: 8px;">
                  <div
                    class="progress-bar bg-warning"
                    role="progressbar"
                    :style="{ width: getRatingPercent(star) + '%' }"
                  ></div>
                </div>
                <span class="small text-muted text-end" style="width: 45px;">{{ getRatingCount(star) }}</span>
              </div>
            </div>

            <!-- 3. Form gửi đánh giá -->
            <div class="col-lg-4 border-start-lg ps-lg-4">
              <h6 class="fw-bold text-dark mb-2">Đánh giá sản phẩm</h6>
              
              <!-- TRƯỜNG HỢP: Đã đánh giá rồi -->
              <div v-if="hasUserReviewed" class="alert alert-success border-0 rounded-4 p-3 mb-0 d-flex align-items-center gap-2">
                <i class="bi bi-check-circle-fill text-success fs-5"></i>
                <div>
                  <div class="fw-bold small text-dark">Bạn đã đánh giá sản phẩm này</div>
                  <div class="text-muted fs-8">Cảm ơn nhận xét của bạn giúp cộng đồng mua sắm tốt hơn!</div>
                </div>
              </div>

              <!-- TRƯỜNG HỢP: Chưa đánh giá -->
              <div v-else>
                <div class="d-flex align-items-center gap-2 mb-2">
                  <span class="small text-muted">Chọn số sao:</span>
                  <div class="text-warning cursor-pointer fs-5">
                    <i
                      v-for="star in 5"
                      :key="star"
                      class="bi me-1"
                      :class="star <= newRating ? 'bi-star-fill' : 'bi-star text-muted'"
                      @click="newRating = star"
                    ></i>
                  </div>
                </div>

                <div class="d-flex gap-2">
                  <input
                    v-model="newCommentContent"
                    type="text"
                    class="form-control rounded-pill px-3"
                    placeholder="Nhập nhận xét (không bắt buộc)..."
                    @keyup.enter="submitComment"
                  />
                  <button class="btn btn-warning rounded-pill px-4 fw-bold text-dark text-nowrap" @click="submitComment">
                    Gửi
                  </button>
                </div>
              </div>
            </div>

          </div>

          <!-- BỘ LỌC SAO BÌNH LUẬN -->
          <div v-if="comments.length > 0" class="d-flex align-items-center gap-2 mb-4 flex-wrap">
            <span class="fw-bold small text-dark me-2">Lọc theo:</span>
            <button
              class="btn btn-sm rounded-pill px-3"
              :class="filterStar === 0 ? 'btn-dark' : 'btn-outline-secondary'"
              @click="filterStar = 0"
            >
              Tất cả ({{ reviewSummary.totalReviews }})
            </button>
            <button
              v-for="s in [5, 4, 3, 2, 1]"
              :key="s"
              class="btn btn-sm rounded-pill px-3"
              :class="filterStar === s ? 'btn-warning text-dark fw-bold' : 'btn-outline-secondary'"
              @click="filterStar = s"
            >
              {{ s }} sao ({{ getRatingCount(s) }})
            </button>
          </div>

          <!-- Danh sách comment -->
          <div v-if="filteredComments.length === 0" class="text-center py-4 text-muted">
            <i class="bi bi-chat-left-text display-4 d-block opacity-25 mb-2"></i>
            Chưa có đánh giá nào phù hợp với bộ lọc.
          </div>

          <div v-else class="d-flex flex-column gap-3">
            <div v-for="comment in filteredComments" :key="comment.id" class="p-3 border-bottom">
              <div class="d-flex justify-content-between align-items-center mb-1">
                <div class="d-flex align-items-center gap-2">
                  <div class="avatar-circle bg-warning text-dark fw-bold small">
                    {{ comment.user?.fullName?.charAt(0) || 'U' }}
                  </div>
                  <div>
                    <h6 class="fw-bold mb-0 text-dark small">{{ comment.user?.fullName || 'Người dùng' }}</h6>
                    <small class="text-muted fs-8">{{ formatDate(comment.createdAt) }}</small>
                  </div>
                </div>

                <div class="text-warning small">
                  <i v-for="s in 5" :key="s" class="bi" :class="s <= comment.rating ? 'bi-star-fill' : 'bi-star text-muted'"></i>
                </div>
              </div>
              
              <!-- Chỉ hiển thị nội dung nếu có nhập nhận xét -->
              <p v-if="comment.content && comment.content.trim()" class="text-secondary small mb-0 ms-5">
                {{ comment.content }}
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- ================= SẢN PHẨM LIÊN QUAN ================= -->
    <div class="mb-4">
      <h4 class="fw-bold text-dark mb-4 d-flex align-items-center gap-2">
        <i class="bi bi-grid-fill text-warning"></i> Sản phẩm tương tự
      </h4>

      <div class="row g-3">
        <div v-for="item in relatedProducts" :key="item.id" class="col-lg-3 col-md-4 col-6">
          <div class="card h-100 border-0 shadow-sm rounded-4 product-card p-3" @click="viewProduct(item.id)">
            <img :src="getImage(item.image)" class="card-img-top mb-2 related-img" :alt="item.name">
            <div class="card-body p-0">
              <h6 class="fw-bold text-dark text-truncate mb-1" :title="item.name">{{ item.name }}</h6>
              <p class="text-muted fs-8 mb-2">{{ item.brand }}</p>
              <h6 class="text-danger fw-bold mb-0">{{ formatPrice(item.price) }}</h6>
            </div>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue"
import axios from "axios"
import { useRoute, useRouter } from "vue-router"
import { addGuestCartItem } from '../utils/cart'
import { notify } from '../utils/notify'
import { confirmDialog } from '../utils/dialog'

const route = useRoute()
const router = useRouter()

const product = ref({})
const products = ref([])
const comments = ref([])
const activeTab = ref('desc')

const selectedImage = ref(0)
const buyQuantity = ref(1)
const isFavorite = ref(false)

const newRating = ref(5)
const newCommentContent = ref("")
const filterStar = ref(0)

// 🔴 Biến lưu thống kê đánh giá từ Backend
const reviewSummary = ref({
  averageRating: '5.0',
  totalReviews: 0,
  count5Star: 0,
  count4Star: 0,
  count3Star: 0,
  count2Star: 0,
  count1Star: 0
})

const productImages = {
  "iphone16.jpg": ["iphone16.jpg", "iphone16_2.jpg", "iphone16_3.jpg", "iphone16_4.jpg"],
  "s26.jpg": ["s26.jpg", "s26_2.jpg", "s26_3.jpg", "s26_4.jpg"],
  "macbook.jpg": ["macbook.jpg", "macbook_2.jpg", "macbook_3.jpg", "macbook_4.jpg"],
  "rog.jpg": ["rog.jpg", "rog_2.jpg", "rog_3.jpg", "rog_4.jpg"],
  "airpods.jpg": ["airpods.jpg", "airpods_2.jpg", "airpods_3.jpg", "airpods_4.jpg"],
  "iphone16_black.jpg": ["iphone16_black.jpg", "iphone16_black_2.jpg", "iphone16_black_3.jpg", "iphone16_black_4.jpg"],
  "iphone16_plus.jpg": ["iphone16_plus.jpg", "iphone16_plus_2.jpg", "iphone16_plus_3.jpg", "iphone16_plus_4.jpg"],
  "a57.jpg": ["a57.jpg", "a57_2.jpg", "a57_3.jpg", "a57_4.jpg"],
  "xiaomi16.jpg": ["xiaomi16.jpg", "xiaomi16_2.jpg", "xiaomi16_3.jpg", "xiaomi16_4.jpg"],
  "reno16.jpg": ["reno16.jpg", "reno16_2.jpg", "reno16_3.jpg", "reno16_4.jpg"],
  "xps15.jpg": ["xps15.jpg", "xps15_2.jpg", "xps15_3.jpg", "xps15_4.jpg"],
  "hppavilion15.jpg": ["hppavilion15.jpg", "hppavilion15_2.jpg", "hppavilion15_3.jpg", "hppavilion15_4.jpg"],
  "legion5.jpg": ["legion5.jpg", "legion5_2.jpg", "legion5_3.jpg", "legion5_4.jpg"],
  "katana17.jpg": ["katana17.jpg", "katana17_2.jpg", "katana17_3.jpg", "katana17_4.jpg"],
  "nitrov.jpg": ["nitrov.jpg", "nitrov_2.jpg", "nitrov_3.jpg", "nitrov_4.jpg"],
  "ipadairm3.jpg": ["ipadairm3.jpg", "ipadairm3_2.jpg", "ipadairm3_3.jpg", "ipadairm3_4.jpg"],
  "ipadprom4.jpg": ["ipadprom4.jpg", "ipadprom4_2.jpg", "ipadprom4_3.jpg", "ipadprom4_4.jpg"],
  "tabs11.jpg": ["tabs11.jpg", "tabs11_2.jpg", "tabs11_3.jpg", "tabs11_4.jpg"],
  "pad8.jpg": ["pad8.jpg", "pad8_2.jpg", "pad8_3.jpg", "pad8_4.jpg"],
  "tabp12.jpg": ["tabp12.jpg", "tabp12_2.jpg", "tabp12_3.jpg", "tabp12_4.jpg"],
  "sonyxm6.jpg": ["sonyxm6.jpg", "sonyxm6_2.jpg", "sonyxm6_3.jpg", "sonyxm6_4.jpg"],
  "buds4pro.jpg": ["buds4pro.jpg", "buds4pro_2.jpg", "buds4pro_3.jpg", "buds4pro_4.jpg"],
  "jbl770.jpg": ["jbl770.jpg", "jbl770_2.jpg", "jbl770_3.jpg", "jbl770_4.jpg"],
  "gprox.jpg": ["gprox.jpg", "gprox_2.jpg", "gprox_3.jpg", "gprox_4.jpg"],
  "blacksharkv2.jpg": ["blacksharkv2.jpg", "blacksharkv2_2.jpg", "blacksharkv2_3.jpg", "blacksharkv2_4.jpg"],
  "watch11.jpg": ["watch11.jpg", "watch11_2.jpg", "watch11_3.jpg", "watch11_4.jpg"],
  "watch8.jpg": ["watch8.jpg", "watch8_2.jpg", "watch8_3.jpg", "watch8_4.jpg"],
  "garmin975.jpg": ["garmin975.jpg", "garmin975_2.jpg", "garmin975_3.jpg", "garmin975_4.jpg"],
  "gt6.jpg": ["gt6.jpg", "gt6_2.jpg", "gt6_3.jpg", "gt6_4.jpg"],
  "balance2.jpg": ["balance2.jpg", "balance2_2.jpg", "balance2_3.jpg", "balance2_4.jpg"],
  "k8pro.jpg": ["k8pro.jpg", "k8pro_2.jpg", "k8pro_3.jpg", "k8pro_4.jpg"],
  "5075b.jpg": ["075b.jpg", "075b_2.jpg", "075b_3.jpg", "075b_4.jpg"],
  "mxkeys.jpg": ["mxkeys.jpg", "mxkeys_2.jpg", "mxkeys_3.jpg", "mxkeys_4.jpg"],
  "blackwidowv4.jpg": ["blackwidowv4.jpg", "blackwidowv4_2.jpg", "blackwidowv4_3.jpg", "blackwidowv4_4.jpg"],
  "rk84.jpg": ["rk84.jpg", "rk84_2.jpg", "rk84_3.jpg", "rk84_4.jpg"],
  "gprox2.jpg": ["gprox2.jpg", "gprox2_2.jpg", "gprox2_3.jpg", "gprox2_4.jpg"],
  "viperv3.jpg": ["viperv3.jpg", "viperv3_2.jpg", "viperv3_3.jpg", "viperv3_4.jpg"],
  "mxmaster3s.jpg": ["mxmaster3s.jpg", "mxmaster3s_2.jpg", "mxmaster3s_3.jpg", "mxmaster3s_4.jpg"],
  "x2h.jpg": ["x2h.jpg", "x2h_2.jpg", "x2h_3.jpg", "x2h_4.jpg"],
  "attacksharkx6.jpg": ["attacksharkx6.jpg", "attacksharkx6_2.jpg", "attacksharkx6_3.jpg", "attacksharkx6_4.jpg"],
  "anker100w.jpg": ["anker100w.jpg", "anker100w_2.jpg", "anker100w_3.jpg", "anker100w_4.jpg"],
  "ugreen100w.jpg": ["ugreen100w.jpg", "ugreen100w_2.jpg", "ugreen100w_3.jpg", "ugreen100w_4.jpg"],
  "baseuspd.jpg": ["baseuspd.jpg", "baseuspd_2.jpg", "baseuspd_3.jpg", "baseuspd_4.jpg"],
  "applecable.jpg": ["applecable.jpg", "applecable_2.jpg", "applecable_3.jpg", "applecable_4.jpg"],
  "samsungcable.jpg": ["samsungcable.jpg", "samsungcable_2.jpg", "samsungcable_3.jpg", "samsungcable_4.jpg"],
  "mcdodo.jpg": ["mcdodo.jpg", "mcdodo_2.jpg", "mcdodo_3.jpg", "mcdodo_4.jpg"]
}

const getUserId = () => {
  const userStorage = localStorage.getItem('user')
  if (userStorage) {
    try { return JSON.parse(userStorage).id || null } catch { return null }
  }
  return null
}

const calculatedPrice = computed(() => {
  if (!product.value.price) return 0
  if (product.value.discount?.discountPercent) {
    return product.value.price * (1 - product.value.discount.discountPercent / 100)
  }
  return product.value.price
})

/* ================= ĐÁNH GIÁ ĐỌC TỪ BACKEND SUMMARY ================= */
const getRatingCount = (star) => {
  return reviewSummary.value[`count${star}Star`] || 0
}

const getRatingPercent = (star) => {
  if (!reviewSummary.value.totalReviews) return 0
  return Math.round((getRatingCount(star) / reviewSummary.value.totalReviews) * 100)
}

const filteredComments = computed(() => {
  if (filterStar.value === 0) return comments.value
  return comments.value.filter(c => (c.rating || 5) === filterStar.value)
})

const hasUserReviewed = computed(() => {
  const currentUserId = getUserId()
  if (!currentUserId || !comments.value.length) return false
  return comments.value.some(c => c.user?.id === currentUserId)
})

const displayImages = computed(() => {
  if (!product.value.image) return []
  return productImages[product.value.image] || [product.value.image]
})

const fetchProduct = async () => {
  try {
    const response = await axios.get(`/api/products/${route.params.id}`)
    product.value = response.data
    selectedImage.value = 0
    buyQuantity.value = 1
    fetchComments()
    fetchReviewSummary()
    checkFavoriteStatus()
  } catch (error) {
    product.value = {}
  }
}

const fetchProducts = async () => {
  try {
    const response = await axios.get("/api/products")
    products.value = response.data.content || response.data || []
  } catch (error) {
    products.value = []
  }
}

const fetchComments = async () => {
  try {
    const response = await axios.get(`/api/comments/product/${route.params.id}`)
    comments.value = response.data
  } catch (error) {
    comments.value = []
  }
}

// 🔴 Gọi API lấy thống kê từ Backend
const fetchReviewSummary = async () => {
  try {
    const response = await axios.get(`/api/comments/product/${route.params.id}/summary`)
    reviewSummary.value = response.data
  } catch (error) {
    console.error("Lỗi lấy thống kê đánh giá:", error)
  }
}

const submitComment = async () => {
  const userId = getUserId()
  if (!userId) {
    notify("Bạn cần đăng nhập để gửi nhận xét!", "warning")
    router.push('/login')
    return
  }

  if (hasUserReviewed.value) {
    notify("Bạn đã đánh giá sản phẩm này rồi!", "warning")
    return
  }

  try {
    await axios.post('/api/comments', {
      userId: userId,
      productId: product.value.id,
      content: newCommentContent.value ? newCommentContent.value.trim() : "",
      rating: newRating.value
    })
    notify("Đã gửi đánh giá thành công!", "success")
    newCommentContent.value = ""
    fetchComments()
    fetchReviewSummary() // Cập nhật lại thống kê mới
  } catch (error) {
    const errorMsg = error?.response?.data || "Không thể gửi bình luận lúc này!"
    notify(typeof errorMsg === 'string' ? errorMsg : "Bạn đã đánh giá sản phẩm này rồi!", "warning")
  }
}

const checkFavoriteStatus = async () => {
  const userId = getUserId()
  if (!userId) return
  try {
    const res = await axios.get(`/api/favorites/check?userId=${userId}&productId=${route.params.id}`)
    isFavorite.value = res.data.isFavorite
  } catch (e) {
    isFavorite.value = false
  }
}

const toggleFavorite = async () => {
  const userId = getUserId()
  if (!userId) {
    notify("Vui lòng đăng nhập để lưu sản phẩm yêu thích!", "warning")
    router.push('/login')
    return
  }

  try {
    if (isFavorite.value) {
      await axios.delete(`/api/favorites?userId=${userId}&productId=${product.value.id}`)
      isFavorite.value = false
      notify("Đã bỏ yêu thích!", "info")
    } else {
      await axios.post(`/api/favorites`, { userId, productId: product.value.id })
      isFavorite.value = true
      notify("Đã thêm vào yêu thích!", "success")
    }
  } catch (err) {
    notify("Thao tác thất bại!", "danger")
  }
}

const relatedProducts = computed(() => {
  if (!product.value.category) return []
  return products.value.filter(item => item.category?.id === product.value.category.id && item.id !== product.value.id)
})

const viewProduct = (id) => {
  router.push("/product/" + id)
}

watch(
  () => route.params.id,
  async () => {
    await fetchProduct()
    selectedImage.value = 0
    buyQuantity.value = 1
    window.scrollTo({
      top: 0,
      behavior: "smooth"
    })
  }
)

const formatPrice = (price) => {
  if (!price && price !== 0) return ""
  return new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(price)
}

const getImage = (image) => {
  if (!image) return "https://via.placeholder.com/400"
  if (image.startsWith('http') || image.startsWith('/')) return image
  return "/images/" + image
}

const increase = () => { 
  if (buyQuantity.value < (product.value.quantity || 99)) buyQuantity.value++ 
}

const decrease = () => { 
  if (buyQuantity.value > 1) buyQuantity.value-- 
}

const addToCart = async () => {
  try {
    const userStorage = localStorage.getItem('user')

    if (!userStorage) {
      addGuestCartItem(product.value, buyQuantity.value)
      window.dispatchEvent(new CustomEvent('cart-updated'))

      notify(`Đã thêm ${buyQuantity.value} sản phẩm vào giỏ hàng tạm.`, 'success')

      if (await confirmDialog('Đã thêm sản phẩm vào giỏ hàng tạm. Bạn có muốn xem giỏ hàng ngay không?')) {
        router.push('/cart')
      }
      return
    }

    const userId = JSON.parse(userStorage).id
    const productId = product.value.id || product.value.Id

    await axios.post(`/api/cart/add?userId=${userId}`, {
      productId: productId,
      quantity: buyQuantity.value
    })

    window.dispatchEvent(new CustomEvent('cart-updated'))

    if (await confirmDialog(`Đã thêm ${buyQuantity.value} sản phẩm vào giỏ hàng! Bạn có muốn đến trang Giỏ hàng ngay không?`)) {
      router.push('/cart')
    }
  } catch (error) {
    console.error("Lỗi thêm vào giỏ hàng:", error)
    notify(
      error.response?.data?.message || "Không thể thêm vào giỏ hàng. Vui lòng kiểm tra lại Backend!",
      "danger"
    )
  }
}

const formatDate = (date) => {
  if (!date) return ""
  return new Date(date).toLocaleDateString("vi-VN")
}

onMounted(() => {
  fetchProducts()
  fetchProduct()
})
</script>

<style scoped>
.main-image-box { height: 380px; display: flex; align-items: center; justify-content: center; }
.main-image { max-height: 100%; object-fit: contain; transition: transform .3s ease; }
.main-image:hover { transform: scale(1.05); }

.thumbnail { width: 70px; height: 70px; object-fit: contain; background: #fff; border: 2px solid #e9ecef; cursor: pointer; padding: 4px; transition: .2s; }
.thumbnail:hover { border-color: #ffc107; }
.active-thumb { border: 2px solid #ffc107 !important; }

.qty-btn { width: 28px; height: 28px; display: flex; align-items: center; justify-content: center; font-weight: bold; }
.avatar-circle { width: 36px; height: 36px; border-radius: 50%; display: flex; align-items: center; justify-content: center; }

.custom-tab { color: #6c757d; background: transparent; border-bottom: 3px solid transparent !important; }
.custom-tab.active { color: #212529 !important; border-bottom: 3px solid #ffc107 !important; }

.product-card { cursor: pointer; transition: transform .25s ease, box-shadow .25s ease; }
.product-card:hover { transform: translateY(-4px); box-shadow: 0 8px 16px rgba(0,0,0,.08) !important; }
.related-img { height: 160px; object-fit: contain; }

@media (min-width: 992px) {
  .border-end-lg { border-right: 1px solid #dee2e6 !important; }
  .border-start-lg { border-left: 1px solid #dee2e6 !important; }
}

.fs-7 { font-size: 13px; }
.fs-8 { font-size: 11px; }
.cursor-pointer { cursor: pointer; }
</style>