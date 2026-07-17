<template>
    <div class="container mt-4">

        <div class="row">

            <!-- Ảnh sản phẩm -->
            <div class="col-md-5">

                <!-- Ảnh lớn -->
                <div class="card shadow-sm mb-3">

                    <img
                        :src="getImage(product.images[selectedImage])"
                        class="img-fluid p-4 main-image"
                    >

                </div>

                <!-- Ảnh nhỏ -->
                <div class="d-flex justify-content-between">

                    <img
                        v-for="(image,index) in product.images"
                        :key="index"
                        :src="getImage(image)"
                        class="thumbnail"
                        :class="{active:index===selectedImage}"
                        @click="selectedImage=index"
                    >

                </div>

            </div>

            <!-- Thông tin -->

            <div class="col-md-7">

                <h2 class="fw-bold">

                    {{ product.name }}

                </h2>

                <hr>

                <p><strong>Thương hiệu:</strong> {{ product.brand }}</p>

                <p><strong>Danh mục:</strong> {{ product.category }}</p>

                <h2 class="text-danger fw-bold">

                    {{ formatPrice(product.price) }}

                </h2>

                <p>

                    <strong>Số lượng còn:</strong>

                    {{ product.quantity }}

                </p>

                <p>

                    <strong>Giảm giá:</strong>

                    {{ product.discount }}

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


            <!--  -->
            <hr class="my-5">

<h3 class="fw-bold mb-4">

Các sản phẩm liên quan

</h3>

<div class="row">

    <div

        class="col-lg-3 col-md-4"

        v-for="item in relatedProducts"

        :key="item.id"

    >

        <div

            class="card shadow-sm h-100 product-card"

            @click="viewProduct(item.id)"

        >

            <img

                :src="getImage(item.images[0])"

                class="card-img-top p-3"

                style="height:220px;object-fit:contain"

            >

            <div class="card-body">

                <h6>

                    {{ item.name }}

                </h6>

                <h5 class="text-danger">

                    {{ formatPrice(item.price) }}

                </h5>

            </div>

        </div>

    </div>

</div>
            <!--  -->
        </div>

    </div>
</template>

<script setup>

import { ref, computed } from "vue"
import { useRoute } from "vue-router"
// 
import { useRouter } from "vue-router"
// 

const route = useRoute()
// 
const router = useRouter()
// 
const buyQuantity = ref(1)

const selectedImage = ref(0)

const products = ref([

{

id:1,

name:"iPhone 16 Pro",

brand:"Apple",

category:"Điện thoại",

price:32990000,

quantity:20,

discount:"10%",

description:"Điện thoại Apple cao cấp với chip A18 Pro, camera 48MP và màn hình Super Retina XDR.",

images:[
"iphone16_1.jpg",
"iphone16_2.jpg",
"iphone16_3.jpg",
"iphone16_4.jpg"
]

},

{

id:2,

name:"Samsung Galaxy S26",

brand:"Samsung",

category:"Điện thoại",

price:26990000,

quantity:30,

discount:"20%",

description:"Galaxy S26 với AI thông minh, màn hình Dynamic AMOLED và camera zoom quang học.",

images:[
"s26_1.jpg",
"s26_2.jpg",
"s26_3.jpg",
"s26_4.jpg"
]

},

{

id:3,

name:"MacBook Air M5",

brand:"Apple",

category:"Laptop",

price:32990000,

quantity:15,

discount:"Không",

description:"Laptop Apple M5 siêu mỏng nhẹ, pin hơn 18 giờ.",

images:[
"macbook_1.jpg",
"macbook_2.jpg",
"macbook_3.jpg",
"macbook_4.jpg"
]

},

{

id:4,

name:"Asus ROG Zephyrus",

brand:"Asus",

category:"Laptop Gaming",

price:39990000,

quantity:10,

discount:"Không",

description:"Laptop Gaming RTX mới nhất, màn hình 240Hz.",

images:[
"rog_1.jpg",
"rog_2.jpg",
"rog_3.jpg",
"rog_4.jpg"
]

},

{

id:5,

name:"AirPods Pro 3",

brand:"Apple",

category:"Tai nghe",

price:6990000,

quantity:50,

discount:"Không",

description:"Tai nghe chống ồn chủ động, âm thanh không gian.",

images:[
"airpods_1.jpg",
"airpods_2.jpg",
"airpods_3.jpg",
"airpods_4.jpg"
]

}

])

const product = computed(() => {

    const id = Number(route.params.id)

    return products.value.find(
        p => p.id === id
    ) || products.value[0]

})


// 
const relatedProducts = computed(() => {

    return products.value.filter(p =>

        p.category === product.value.category

        &&

        p.id !== product.value.id

    )

})

const viewProduct=(id)=>{

    selectedImage.value=0

    buyQuantity.value=1

    router.push("/product/"+id)

}

// 

const formatPrice=(price)=>{

return new Intl.NumberFormat(

"vi-VN",

{

style:"currency",

currency:"VND"

}

).format(price)

}

const getImage=(image)=>{

return "/images/"+image

}

const increase=()=>{

buyQuantity.value++

}

const decrease=()=>{

if(buyQuantity.value>1){

buyQuantity.value--

}

}

const addToCart=()=>{

alert(

"Đã thêm "+buyQuantity.value+" sản phẩm vào giỏ hàng."

)

}

</script>

<!--  -->
<!--  -->
<!--  -->
<style scoped>

/* =========================
   Ảnh lớn
========================= */

.main-image{
    width:100%;
    height:450px;
    object-fit:contain;
    transition:0.3s;
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

    line-height:1.7;

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
   Card ảnh
========================= */

.card{

    border-radius:12px;

    overflow:hidden;

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

/*  */
.product-card{

    cursor:pointer;

    transition:.3s;

}

.product-card:hover{

    transform:translateY(-6px);

    box-shadow:0 10px 20px rgba(0,0,0,.15);

}
/*  */

</style>