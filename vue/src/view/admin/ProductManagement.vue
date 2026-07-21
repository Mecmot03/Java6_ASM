<template>

    <div class="container-fluid">

        <!-- Tiêu đề -->

        <div class="d-flex justify-content-between align-items-center mb-4">

            <h2 class="fw-bold">

                Quản lý sản phẩm

            </h2>

            <router-link
                to="/admin/products/create"
                class="btn btn-success"
            >

                <i class="bi bi-plus-circle me-2"></i>

                Thêm sản phẩm

            </router-link>

        </div>

        <!-- Thanh tìm kiếm -->

        <div class="card shadow-sm border-0 mb-4">

            <div class="card-body">

                <div class="row">

                    <div class="col-md-6">

                        <input
                            v-model="keyword"
                            type="text"
                            class="form-control"
                            placeholder="Nhập tên sản phẩm..."
                        >

                    </div>

                    <div class="col-md-2">

                        <button
                            class="btn btn-primary w-100"
                            @click="searchProduct"
                        >

                            <i class="bi bi-search"></i>

                            Tìm

                        </button>

                    </div>

                    <div class="col-md-2">

                        <button
                            class="btn btn-secondary w-100"
                            @click="loadProducts"
                        >

                            Làm mới

                        </button>

                    </div>

                </div>

            </div>

        </div>

        <!-- Bảng -->

        <ProductTable
            :products="products"
            @delete="deleteProduct"
        />

    </div>

</template>

<script setup>

import { ref, onMounted } from 'vue'

import ProductService from '../../services/ProductService'

import ProductTable from '../../components/admin/ProductTable.vue'

const products = ref([])

const keyword = ref('')

const loadProducts = async () => {

    products.value = await ProductService.getAllProducts()

}

const searchProduct = async () => {

    if (keyword.value.trim() === '') {

        loadProducts()

        return

    }

    products.value = products.value.filter(product =>

        product.name.toLowerCase().includes(

            keyword.value.toLowerCase()

        )

    )

}

const deleteProduct = async (id) => {

    if (!confirm("Bạn có chắc muốn xóa sản phẩm này?")) {

        return

    }

    try {

        await ProductService.deleteProduct(id)

        loadProducts()

    }

    catch(error){

        alert("Xóa thất bại!")

    }

}

onMounted(() => {

    loadProducts()

})

</script>

<style scoped>

.card{

    border-radius:12px;

}

</style>