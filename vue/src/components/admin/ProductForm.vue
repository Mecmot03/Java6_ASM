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

                    <!-- Tên -->

                    <div class="col-md-6 mb-3">

                        <label class="form-label">

                            Tên sản phẩm

                        </label>

                        <input

                            v-model="form.name"

                            class="form-control"

                            required

                        >

                    </div>

                    <!-- Thương hiệu -->

                    <div class="col-md-6 mb-3">

                        <label class="form-label">

                            Thương hiệu

                        </label>

                        <input

                            v-model="form.brand"

                            class="form-control"

                        >

                    </div>

                    <!-- Giá -->

                    <div class="col-md-4 mb-3">

                        <label class="form-label">

                            Giá

                        </label>

                        <input

                            type="number"

                            v-model="form.price"

                            class="form-control"

                        >

                    </div>

                    <!-- Số lượng -->

                    <div class="col-md-4 mb-3">

                        <label class="form-label">

                            Số lượng

                        </label>

                        <input

                            type="number"

                            v-model="form.quantity"

                            class="form-control"

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

                    <div class="col-md-12 mb-3">

                        <label class="form-label">

                            Tên ảnh

                        </label>

                        <input

                            v-model="form.image"

                            class="form-control"

                            placeholder="iphone16.jpg"

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

                        <i class="bi bi-floppy"></i>

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

    computed

} from "vue"

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

    },

    {

        immediate:true

    }

)

const isEdit = computed(()=>{

    return form.id != null

})

const saveProduct=()=>{

    emit("save",{

        ...form

    })

}

const reset=()=>{

    emit("cancel")

}

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

.btn-success{
    background:#0d6efd;
    border:none;
    border-radius:10px;
    padding:10px 24px;
    font-weight:600;
}

.btn-success:hover{
    background:#0b5ed7;
}

.btn-secondary{
    border-radius:10px;
    padding:10px 24px;
}

.row>.col-md-4,
.row>.col-md-6,
.row>.col-md-12,
.row>.col-12{
    margin-bottom:8px;
}

</style>