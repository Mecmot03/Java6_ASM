<template>

<div class="container mt-4">

    <div class="row">

        <!-- ================= ẢNH SẢN PHẨM ================= -->

        <div class="col-lg-5">

            <!-- Ảnh lớn -->

            <div class="card shadow-sm mb-3">

                <img
                    :src="getImage(displayImages[selectedImage])"
                    class="img-fluid p-4 main-image"
                    :alt="product.name"
                >

            </div>

            <!-- 4 ảnh nhỏ -->

            <div class="d-flex justify-content-between">

                <img

                    v-for="(image,index) in displayImages"

                    :key="index"

                    :src="getImage(image)"

                    class="thumbnail"

                    :class="{active:index===selectedImage}"

                    @click="selectedImage=index"

                >

            </div>

        </div>

        <!-- ================= THÔNG TIN ================= -->

        <div class="col-lg-7">

            <h2 class="fw-bold">

                {{ product.name }}

            </h2>

            <hr>

            <p>

                <strong>Thương hiệu:</strong>

                {{ product.brand }}

            </p>

            <p>

                <strong>Danh mục:</strong>

                {{ product.category?.name }}

            </p>

            <h2 class="text-danger fw-bold">

                {{ formatPrice(product.price) }}

            </h2>

            <p>

                <strong>Số lượng còn:</strong>

                {{ product.quantity }}

            </p>

            <hr>

            <h5>Mô tả sản phẩm</h5>

            <p class="text-secondary">

                {{ product.description }}

            </p>

            <!-- Chọn số lượng -->

            <div class="d-flex align-items-center mt-4">

                <button
                    class="btn btn-outline-secondary"
                    @click="decrease"
                >
                    -
                </button>

                <input
                    class="form-control mx-2 text-center"
                    style="width:70px"
                    v-model="buyQuantity"
                    readonly
                >

                <button
                    class="btn btn-outline-secondary"
                    @click="increase"
                >
                    +
                </button>

            </div>

            <!-- Button -->

            <div class="mt-4">

                <button
                    class="btn btn-warning btn-lg me-3"
                    @click="addToCart"
                >

                    <i class="bi bi-cart-plus"></i>

                    Thêm vào giỏ

                </button>

                <button
                    class="btn btn-outline-danger btn-lg"
                >

                    ❤ Yêu thích

                </button>

            </div>

        </div>

    </div>

    <!-- ================= SẢN PHẨM LIÊN QUAN ================= -->

    <hr class="my-5">

    <h3 class="fw-bold mb-4">

        Các sản phẩm liên quan

    </h3>

    <div class="row g-4">

        <div

            class="col-lg-3 col-md-4 col-sm-6"

            v-for="item in relatedProducts"

            :key="item.id"

        >

            <div

                class="card h-100 shadow-sm product-card"

                @click="viewProduct(item.id)"

            >

                <div
                    class="p-3 text-center"
                >

                    <img

                        :src="getImage(item.image)"

                        class="card-img-top"

                        style="height:220px;object-fit:contain"

                    >

                </div>

                <div class="card-body">

                    <h6 class="fw-bold">

                        {{ item.name }}

                    </h6>

                    <p class="text-muted mb-2">

                        {{ item.brand }}

                    </p>

                    <h5 class="text-danger">

                        {{ formatPrice(item.price) }}

                    </h5>

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

const route = useRoute()
const router = useRouter()

const product = ref({})
const products = ref([])

const selectedImage = ref(0)
const buyQuantity = ref(1)


const productImages = {

    "iphone16.jpg": [
        "iphone16.jpg",
        "iphone16_2.jpg",
        "iphone16_3.jpg",
        "iphone16_4.jpg"
    ],

    "s26.jpg": [
        "s26.jpg",
        "s26_2.jpg",
        "s26_3.jpg",
        "s26_4.jpg"
    ],

    "macbook.jpg": [
        "macbook.jpg",
        "macbook_2.jpg",
        "macbook_3.jpg",
        "macbook_4.jpg"
    ],

    "rog.jpg": [
        "rog.jpg",
        "rog_2.jpg",
        "rog_3.jpg",
        "rog_4.jpg"
    ],

    "airpods.jpg": [
        "airpods.jpg",
        "airpods_2.jpg",
        "airpods_3.jpg",
        "airpods_4.jpg"
    ],

    "iphone16_black.jpg": [
        "iphone16_black.jpg",
        "iphone16_black_2.jpg",
        "iphone16_black_3.jpg",
        "iphone16_black_4.jpg"
    ],

    "iphone16_plus.jpg": [
        "iphone16_plus.jpg",
        "iphone16_plus_2.jpg",
        "iphone16_plus_3.jpg",
        "iphone16_plus_4.jpg"
    ],

    "a57.jpg": [
        "a57.jpg",
        "a57_2.jpg",
        "a57_3.jpg",
        "a57_4.jpg"
    ],

    "xiaomi16.jpg": [
        "xiaomi16.jpg",
        "xiaomi16_2.jpg",
        "xiaomi16_3.jpg",
        "xiaomi16_4.jpg"
    ],

    "reno16.jpg": [
        "reno16.jpg",
        "reno16_2.jpg",
        "reno16_3.jpg",
        "reno16_4.jpg"
    ],

    "xps15.jpg": [
        "xps15.jpg",
        "xps15_2.jpg",
        "xps15_3.jpg",
        "xps15_4.jpg"
    ],

    "hppavilion15.jpg": [
        "hppavilion15.jpg",
        "hppavilion15_2.jpg",
        "hppavilion15_3.jpg",
        "hppavilion15_4.jpg"
    ],

    "legion5.jpg": [
        "legion5.jpg",
        "legion5_2.jpg",
        "legion5_3.jpg",
        "legion5_4.jpg"
    ],

    "katana17.jpg": [
        "katana17.jpg",
        "katana17_2.jpg",
        "katana17_3.jpg",
        "katana17_4.jpg"
    ],

    "nitrov.jpg": [
        "nitrov.jpg",
        "nitrov_2.jpg",
        "nitrov_3.jpg",
        "nitrov_4.jpg"
    ],

    "ipadairm3.jpg": [
        "ipadairm3.jpg",
        "ipadairm3_2.jpg",
        "ipadairm3_3.jpg",
        "ipadairm3_4.jpg"
    ],

    "ipadprom4.jpg": [
        "ipadprom4.jpg",
        "ipadprom4_2.jpg",
        "ipadprom4_3.jpg",
        "ipadprom4_4.jpg"
    ],

    "tabs11.jpg": [
        "tabs11.jpg",
        "tabs11_2.jpg",
        "tabs11_3.jpg",
        "tabs11_4.jpg"
    ],

    "pad8.jpg": [
        "pad8.jpg",
        "pad8_2.jpg",
        "pad8_3.jpg",
        "pad8_4.jpg"
    ],

    "tabp12.jpg": [
        "tabp12.jpg",
        "tabp12_2.jpg",
        "tabp12_3.jpg",
        "tabp12_4.jpg"
    ],

    "sonyxm6.jpg": [
        "sonyxm6.jpg",
        "sonyxm6_2.jpg",
        "sonyxm6_3.jpg",
        "sonyxm6_4.jpg"
    ],

    "buds4pro.jpg": [
        "buds4pro.jpg",
        "buds4pro_2.jpg",
        "buds4pro_3.jpg",
        "buds4pro_4.jpg"
    ],

    "jbl770.jpg": [
        "jbl770.jpg",
        "jbl770_2.jpg",
        "jbl770_3.jpg",
        "jbl770_4.jpg"
    ],

    "gprox.jpg": [
        "gprox.jpg",
        "gprox_2.jpg",
        "gprox_3.jpg",
        "gprox_4.jpg"
    ],

    "blacksharkv2.jpg": [
        "blacksharkv2.jpg",
        "blacksharkv2_2.jpg",
        "blacksharkv2_3.jpg",
        "blacksharkv2_4.jpg"
    ],

    "watch11.jpg": [
        "watch11.jpg",
        "watch11_2.jpg",
        "watch11_3.jpg",
        "watch11_4.jpg"
    ],

    "watch8.jpg": [
        "watch8.jpg",
        "watch8_2.jpg",
        "watch8_3.jpg",
        "watch8_4.jpg"
    ],

    "garmin975.jpg": [
        "garmin975.jpg",
        "garmin975_2.jpg",
        "garmin975_3.jpg",
        "garmin975_4.jpg"
    ],

    "gt6.jpg": [
        "gt6.jpg",
        "gt6_2.jpg",
        "gt6_3.jpg",
        "gt6_4.jpg"
    ],

    "balance2.jpg": [
        "balance2.jpg",
        "balance2_2.jpg",
        "balance2_3.jpg",
        "balance2_4.jpg"
    ],

    "k8pro.jpg": [
        "k8pro.jpg",
        "k8pro_2.jpg",
        "k8pro_3.jpg",
        "k8pro_4.jpg"
    ],

    "5075b.jpg": [
        "075b.jpg",
        "075b_2.jpg",
        "075b_3.jpg",
        "075b_4.jpg"
    ],

    "mxkeys.jpg": [
        "mxkeys.jpg",
        "mxkeys_2.jpg",
        "mxkeys_3.jpg",
        "mxkeys_4.jpg"
    ],

    "blackwidowv4.jpg": [
        "blackwidowv4.jpg",
        "blackwidowv4_2.jpg",
        "blackwidowv4_3.jpg",
        "blackwidowv4_4.jpg"
    ],

    "rk84.jpg": [
        "rk84.jpg",
        "rk84_2.jpg",
        "rk84_3.jpg",
        "rk84_4.jpg"
    ],

    "gprox2.jpg": [
        "gprox2.jpg",
        "gprox2_2.jpg",
        "gprox2_3.jpg",
        "gprox2_4.jpg"
    ],

    "viperv3.jpg": [
        "viperv3.jpg",
        "viperv3_2.jpg",
        "viperv3_3.jpg",
        "viperv3_4.jpg"
    ],

    "mxmaster3s.jpg": [
        "mxmaster3s.jpg",
        "mxmaster3s_2.jpg",
        "mxmaster3s_3.jpg",
        "mxmaster3s_4.jpg"
    ],

    "x2h.jpg": [
        "x2h.jpg",
        "x2h_2.jpg",
        "x2h_3.jpg",
        "x2h_4.jpg"
    ],

    "attacksharkx6.jpg": [
        "attacksharkx6.jpg",
        "attacksharkx6_2.jpg",
        "attacksharkx6_3.jpg",
        "attacksharkx6_4.jpg"
    ],

    "anker100w.jpg": [
        "anker100w.jpg",
        "anker100w_2.jpg",
        "anker100w_3.jpg",
        "anker100w_4.jpg"
    ],

    "ugreen100w.jpg": [
        "ugreen100w.jpg",
        "ugreen100w_2.jpg",
        "ugreen100w_3.jpg",
        "ugreen100w_4.jpg"
    ],

    "baseuspd.jpg": [
        "baseuspd.jpg",
        "baseuspd_2.jpg",
        "baseuspd_3.jpg",
        "baseuspd_4.jpg"
    ],

    "applecable.jpg": [
        "applecable.jpg",
        "applecable_2.jpg",
        "applecable_3.jpg",
        "applecable_4.jpg"
    ],

    "samsungcable.jpg": [
        "samsungcable.jpg",
        "samsungcable_2.jpg",
        "samsungcable_3.jpg",
        "samsungcable_4.jpg"
    ],

    "x1carbon14.jpg": [
        "x1carbon14.jpg",
        "x1carbon14_2.jpg",
        "x1carbon14_3.jpg",
        "x1carbon14_4.jpg"
    ],

    "zenbook14.jpg": [
        "zenbook14.jpg",
        "zenbook14_2.jpg",
        "zenbook14_3.jpg",
        "zenbook14_4.jpg"
    ],

    "omen16.jpg": [
        "omen16.jpg",
        "omen16_2.jpg",
        "omen16_3.jpg",
        "omen16_4.jpg"
    ],

    "ipadmini8.jpg": [
        "ipadmini8.jpg",
        "ipadmini8_2.jpg",
        "ipadmini8_3.jpg",
        "ipadmini8_4.jpg"
    ],

    "matepad13.jpg": [
        "matepad13.jpg",
        "matepad13_2.jpg",
        "matepad13_3.jpg",
        "matepad13_4.jpg"
    ],

    "honorpadx10.jpg": [
        "honorpadx10.jpg",
        "honorpadx10_2.jpg",
        "honorpadx10_3.jpg",
        "honorpadx10_4.jpg"
    ],

    "redmipadpro.jpg": [
        "redmipadpro.jpg",
        "redmipadpro_2.jpg",
        "redmipadpro_3.jpg",
        "redmipadpro_4.jpg"
    ],

    "oppopad4.jpg": [
        "oppopad4.jpg",
        "oppopad4_2.jpg",
        "oppopad4_3.jpg",
        "oppopad4_4.jpg"
    ]

}

/* ===============================
   Lấy chi tiết sản phẩm
================================ */

const fetchProduct = async () => {

    try {

        const response = await axios.get(

            `http://localhost:8080/api/products/${route.params.id}`

        )

        product.value = response.data

        selectedImage.value = 0

        buyQuantity.value = 1

    }

    catch (error) {

        console.error("Lỗi tải sản phẩm:", error)

    }

}

/* ===============================
   Lấy toàn bộ sản phẩm
================================ */

const fetchProducts = async () => {

    try {

        const response = await axios.get(

            "http://localhost:8080/api/products"

        )

        products.value = response.data

    }

    catch (error) {

        console.error("Lỗi tải danh sách:", error)

    }

}

/* ===============================
   Sinh 4 ảnh
================================ */

const displayImages = computed(() => {

    if (!product.value.image) {

        return []

    }

    return productImages[product.value.image] || [

        product.value.image

    ]

})

/* ===============================
   Sản phẩm liên quan
================================ */

const relatedProducts = computed(() => {

    if (!product.value.category) {

        return []

    }

    return products.value.filter(item =>

        item.category?.id === product.value.category.id

        &&

        item.id !== product.value.id

    )

})

/* ===============================
   Chuyển sản phẩm
================================ */

const viewProduct = (id) => {

    router.push("/product/" + id)

}

/* ===============================
   Theo dõi URL
================================ */

watch(

    () => route.params.id,

    () => {

        fetchProduct()

    }

)

/* ===============================
   Format giá
================================ */

const formatPrice = (price) => {

    if (!price) return ""

    return new Intl.NumberFormat(

        "vi-VN",

        {

            style: "currency",

            currency: "VND"

        }

    ).format(price)

}

/* ===============================
   Đường dẫn ảnh
================================ */

const getImage = (image) => {

    if (!image) {

        return "https://via.placeholder.com/400"

    }

    return "/images/" + image

}

/* ===============================
   Quantity
================================ */

const increase = () => {

    buyQuantity.value++

}

const decrease = () => {

    if (buyQuantity.value > 1) {

        buyQuantity.value--

    }

}

/* ===============================
   Cart
================================ */

const addToCart = () => {

    alert(

        "Đã thêm " +

        buyQuantity.value +

        " sản phẩm vào giỏ hàng."

    )

}

/* ===============================
   Mounted
================================ */

onMounted(() => {

    fetchProducts()

    fetchProduct()

})

</script>

<style scoped>

/* =========================
   Ảnh lớn
========================= */

.main-image{

    width:100%;
    height:450px;
    object-fit:contain;
    transition:.3s;
    cursor:pointer;

}

.main-image:hover{

    transform:scale(1.03);

}

/* =========================
   Ảnh nhỏ
========================= */

.thumbnail{

    width:80px;
    height:80px;
    object-fit:contain;

    background:#fff;

    border:2px solid #ddd;

    border-radius:8px;

    padding:6px;

    cursor:pointer;

    transition:.25s;

}

.thumbnail:hover{

    border-color:#ffc107;

    transform:scale(1.08);

}

.active{

    border:3px solid #dc3545;

}

/* =========================
   Tiêu đề
========================= */

h2{

    color:#222;

    font-weight:700;

}

/* =========================
   Giá
========================= */

.text-danger{

    font-size:32px;

}

/* =========================
   Mô tả
========================= */

.text-secondary{

    line-height:1.8;

}

/* =========================
   Input số lượng
========================= */

input{

    font-weight:bold;

}

/* =========================
   Button
========================= */

.btn-warning{

    font-weight:bold;

    transition:.25s;

}

.btn-warning:hover{

    transform:translateY(-2px);

}

.btn-outline-danger{

    transition:.25s;

}

.btn-outline-danger:hover{

    transform:translateY(-2px);

}

/* =========================
   Card
========================= */

.card{

    border-radius:12px;

    overflow:hidden;

}

/* =========================
   Product liên quan
========================= */

.product-card{

    cursor:pointer;

    transition:.3s;

}

.product-card:hover{

    transform:translateY(-6px);

    box-shadow:0 10px 20px rgba(0,0,0,.15);

}

.product-card img{

    transition:.3s;

}

.product-card:hover img{

    transform:scale(1.05);

}

/* =========================
   Responsive
========================= */

@media(max-width:768px){

    .main-image{

        height:300px;

    }

    .thumbnail{

        width:60px;

        height:60px;

    }

    .text-danger{

        font-size:26px;

    }

}

</style>