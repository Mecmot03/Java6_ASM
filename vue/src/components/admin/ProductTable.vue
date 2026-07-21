<template>

    <div class="card shadow-sm border-0">

        <div class="card-body p-0">

            <table class="table table-hover align-middle mb-0">

                <thead class="table-dark">

                    <tr>

                        <th width="60">ID</th>

                        <th width="90">Ảnh</th>

                        <th>Tên sản phẩm</th>

                        <th>Danh mục</th>

                        <th>Hãng</th>

                        <th>Giá</th>

                        <th>SL</th>

                        <th>Trạng thái</th>

                        <th width="170" class="text-center">

                            Thao tác

                        </th>

                    </tr>

                </thead>

                <tbody>

                    <tr

                        v-for="product in products"

                        :key="product.id"

                    >

                        <td>

                            {{ product.id }}

                        </td>

                        <td>

                            <img

                                :src="getImage(product.image)"

                                class="product-image"

                            >

                        </td>

                        <td>

                            <strong>

                                {{ product.name }}

                            </strong>

                        </td>

                        <td>

                            {{ product.category?.name }}

                        </td>

                        <td>

                            {{ product.brand }}

                        </td>

                        <td class="text-danger fw-bold">

                            {{ formatPrice(product.price) }}

                        </td>

                        <td>

                            {{ product.quantity }}

                        </td>

                        <td>

                            <span

                                v-if="product.status"

                                class="badge bg-success"

                            >

                                Đang bán

                            </span>

                            <span

                                v-else

                                class="badge bg-danger"

                            >

                                Ngừng bán

                            </span>

                        </td>

                        <td class="text-center">

                            <!-- Sửa -->

                            <button

                                class="btn btn-warning btn-sm me-2"

                                @click="$emit('edit', product)"

                            >

                                <i class="bi bi-pencil-square"></i>

                            </button>

                            <!-- Xóa -->

                            <button

                                class="btn btn-danger btn-sm"

                                @click="openDeleteModal(product.id)"

                            >

                                <i class="bi bi-trash"></i>

                            </button>

                        </td>

                    </tr>

                    <tr

                        v-if="products.length==0"

                    >

                        <td

                            colspan="9"

                            class="text-center text-muted py-4"

                        >

                            Không có dữ liệu

                        </td>

                    </tr>

                </tbody>

            </table>

        </div>

    </div>

    <!-- Modal -->

    <DeleteModal

        :show="showDelete"

        @confirm="confirmDelete"

        @close="closeDelete"

    />

</template>

<script setup>

import { ref } from "vue"

import DeleteModal from "./DeleteModal.vue"

defineProps({

    products:{

        type:Array,

        default:()=>[]

    }

})

const emit = defineEmits([

    "edit",

    "delete"

])

const showDelete = ref(false)

const deleteId = ref(null)

const openDeleteModal=(id)=>{

    deleteId.value=id

    showDelete.value=true

}

const confirmDelete=()=>{

    emit("delete",deleteId.value)

    showDelete.value=false

}

const closeDelete=()=>{

    showDelete.value=false

}

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

    if(!image){

        return "https://via.placeholder.com/80"

    }

    return "/images/"+image

}

</script>

<style scoped>

.product-image{

    width:70px;

    height:70px;

    object-fit:contain;

}

.table td{

    vertical-align:middle;

}

.table tbody tr:hover{

    background:#f8f9fa;

}

.card{

    border-radius:12px;

    overflow:hidden;

}

.btn{

    min-width:40px;

}

</style>