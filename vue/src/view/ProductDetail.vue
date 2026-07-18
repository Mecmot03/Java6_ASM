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

    const fileName = product.value.image

    const dot = fileName.lastIndexOf(".")

    const ext = fileName.substring(dot)

    const name = fileName.substring(0, dot)

    return [

        name + ext,

        name + "_2" + ext,

        name + "_3" + ext,

        name + "_4" + ext

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