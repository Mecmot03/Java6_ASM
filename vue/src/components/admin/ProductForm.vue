<template>

    <div class="card product-card shadow-lg border-0 mb-4">

        <div class="card-header product-header">

            <h5 class="mb-0">

                {{ isEdit ? "Cập nhật sản phẩm" : "Thêm sản phẩm" }}

            </h5>

        </div>

        <div class="card-body product-body">

            <form @submit.prevent="saveProduct">

                <div class="row">

                    <!-- ID -->

                    <div class="col-md-2 mb-3">

                        <label class="form-label">

                            ID

                        </label>

                        <input
                            class="form-control"
                            v-model="form.id"
                            readonly
                        >

                    </div>

                    <!-- Tên sản phẩm -->

                    <div class="col-md-10 mb-3">

                        <label class="form-label">

                            Tên sản phẩm

                        </label>

                        <input
                            v-model="form.name"
                            class="form-control"
                            required
                        >

                    </div>

                    <!-- Danh mục -->

                    <div class="col-md-6 mb-3">

                        <label class="form-label">

                            Danh mục

                        </label>

                        <select
                            class="form-select"
                            v-model="form.category"
                        >

                            <option
                                :value="null"
                            >

                                -- Chọn danh mục --

                            </option>

                            <option
                                v-for="item in categories"
                                :key="item.id"
                                :value="item"
                            >

                                {{ item.name }}

                            </option>

                        </select>

                    </div>

                    <!-- Thương hiệu -->

                    <div class="col-md-6 mb-3">

                        <label class="form-label">

                            Hãng

                        </label>

                        <select
                            class="form-select"
                            v-model="form.brand"
                        >

                            <option
                                disabled
                                value=""
                            >

                                -- Chọn thương hiệu --

                            </option>

                            <option
                                v-for="brand in brands"
                                :key="brand"
                                :value="brand"
                            >

                                {{ brand }}

                            </option>

                        </select>

                    </div>

                    <!-- Giá -->

                    <div class="col-md-4 mb-3">

                        <label class="form-label">

                            Giá

                        </label>

                        <input
                            type="number"
                            class="form-control"
                            v-model="form.price"
                        >

                    </div>

                    <!-- Số lượng -->

                    <div class="col-md-4 mb-3">

                        <label class="form-label">

                            Số lượng

                        </label>

                        <input
                            type="number"
                            class="form-control"
                            v-model="form.quantity"
                        >

                    </div>

                    <!-- Trạng thái -->

                    <div class="col-md-4 mb-3">

                        <label class="form-label">

                            Trạng thái

                        </label>

                        <select
                            class="form-select"
                            v-model="form.status"
                        >

                            <option :value="true">

                                Đang bán

                            </option>

                            <option :value="false">

                                Ngừng bán

                            </option>

                        </select>

                    </div>

                    <!-- Ảnh -->

                    <div class="col-md-6 mb-3">

                        <label class="form-label">

                            Chọn ảnh

                        </label>

                        <input
                            type="file"
                            class="form-control"
                            accept="image/*"
                            @change="chooseImage"
                        >

                    </div>

                    <!-- Tên file -->

                    <div class="col-md-6 mb-3">

                        <label class="form-label">

                            Tên ảnh

                        </label>

                        <input
                            class="form-control"
                            v-model="form.image"
                            readonly
                        >

                    </div>

                    <!-- Xem trước -->

                    <div
                        class="col-md-12 mb-3"
                        v-if="previewImage"
                    >

                        <label class="form-label">

                            Xem trước

                        </label>

                        <br>

                        <img
                            :src="previewImage"
                            class="preview-image"
                        >

                    </div>

                    <!-- Mô tả -->

                    <div class="col-12 mb-3">

                        <label class="form-label">

                            Mô tả

                        </label>

                        <textarea
                            rows="5"
                            class="form-control"
                            v-model="form.description"
                        ></textarea>

                    </div>

                </div>

                <div class="text-end">

                    <button
                        type="button"
                        class="btn btn-secondary me-2"
                        @click="reset"
                    >

                        Hủy

                    </button>

                    <button
                        class="btn btn-success"
                        type="submit"
                    >

                        <i class="bi bi-floppy me-1"></i>

                        {{ isEdit ? "Cập nhật" : "Thêm sản phẩm" }}

                    </button>

                </div>

            </form>

        </div>

    </div>

</template>

<script setup>

import {

    reactive,
    watch,
    computed,
    ref,
    onMounted

} from "vue"

import CategoryService from "../../services/CategoryService"

const props = defineProps({

    product:{

        type:Object,

        default:()=>({})

    }

})

const emit = defineEmits([

    "save",

    "cancel"

])

// =========================
// Danh mục
// =========================

const categories = ref([])

const loadCategories = async()=>{

    try{

        categories.value = await CategoryService.getAllCategories()

    }

    catch(error){

        console.log(error)

    }

}

// =========================
// Danh sách thương hiệu
// =========================

const brands = [

    "Apple",

    "Samsung",

    "Xiaomi",

    "Oppo",

    "Vivo",

    "Asus",

    "Acer",

    "Dell",

    "HP",

    "Lenovo",

    "MSI",

    "Logitech",

    "Razer",

    "Corsair",

    "SteelSeries",

    "HyperX",

    "Kingston",

    "Baseus",

    "Anker",

    "UGREEN"

]

// =========================
// Form
// =========================

const form = reactive({

    id:null,

    name:"",

    brand:"",

    price:0,

    quantity:0,

    image:"",

    description:"",

    status:true,

    category:null,

    discountId:null

})

// =========================
// Preview ảnh
// =========================

const previewImage = ref("")

const chooseImage=(event)=>{

    const file = event.target.files[0]

    if(!file){

        return

    }

    form.image=file.name

    previewImage.value=URL.createObjectURL(file)

}

// =========================
// Watch dữ liệu
// =========================

watch(

    ()=>props.product,

    (value)=>{

        Object.assign(form,{

            id:value?.id ?? null,

            name:value?.name ?? "",

            brand:value?.brand ?? "",

            price:value?.price ?? 0,

            quantity:value?.quantity ?? 0,

            image:value?.image ?? "",

            description:value?.description ?? "",

            status:value?.status ?? true,

            category:value?.category ?? null,

            discountId:value?.discountId ?? null

        })

        if(value?.image){

            previewImage.value="/images/"+value.image

        }

        else{

            previewImage.value=""

        }

    },

    {

        immediate:true

    }

)

// =========================
// Edit hay thêm
// =========================

const isEdit = computed(()=>{

    return form.id!=null

})

// =========================
// Lưu
// =========================

const saveProduct=()=>{

    emit("save",{

        ...form

    })

}

// =========================
// Reset
// =========================

const reset=()=>{

    emit("cancel")

}

// =========================

onMounted(()=>{

    loadCategories()

})

</script>

<style scoped>

.product-card{

    border-radius:18px;

    overflow:hidden;

    background:#ffffff;

    border:none;

    box-shadow:0 8px 30px rgba(0,0,0,.08)!important;

}

.product-header{

    background:linear-gradient(90deg,#0d6efd,#3b82f6);

    color:white;

    padding:18px 25px;

}

.product-body{

    background:#f8f9fc;

    padding:30px;

}

.form-label{

    font-weight:600;

    color:#444;

    margin-bottom:8px;

}

.form-control,
.form-select{

    border-radius:10px;

    border:1px solid #d9dce3;

    height:46px;

    transition:.25s;

}

textarea.form-control{

    height:140px;

    resize:none;

}

.form-control:focus,
.form-select:focus{

    border-color:#0d6efd;

    box-shadow:0 0 0 .18rem rgba(13,110,253,.15);

}

.preview-image{

    width:180px;

    height:180px;

    object-fit:contain;

    border:1px solid #dee2e6;

    border-radius:12px;

    background:#fff;

    padding:10px;

    box-shadow:0 3px 12px rgba(0,0,0,.08);

}

.btn-success{

    background:#0d6efd;

    border:none;

    border-radius:10px;

    padding:10px 24px;

    font-weight:600;

    transition:.25s;

}

.btn-success:hover{

    background:#0b5ed7;

    transform:translateY(-2px);

}

.btn-secondary{

    border-radius:10px;

    padding:10px 24px;

    transition:.25s;

}

.btn-secondary:hover{

    transform:translateY(-2px);

}

.row>.col-md-2,
.row>.col-md-4,
.row>.col-md-6,
.row>.col-md-12,
.row>.col-12{

    margin-bottom:8px;

}

@media(max-width:768px){

    .product-body{

        padding:20px;

    }

    .preview-image{

        width:130px;

        height:130px;

    }

}

</style>