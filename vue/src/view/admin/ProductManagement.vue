<template>

   <div class="container-fluid product-page">

        <!-- Tiêu đề -->

        <div class="d-flex justify-content-between align-items-center mb-4">

            <h2 class="fw-bold">

                Quản lý sản phẩm

            </h2>

            <!-- <button
    class="btn btn-success"
    @click="newProduct"
>

                <i class="bi bi-plus-circle me-2"></i>

                Thêm sản phẩm

            </button> -->

        </div>



        

        <!-- Bảng -->

        <ProductForm
    :product="selectedProduct"
    @save="saveProduct"
    @cancel="cancelEdit"
/>


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


        <!-- <ProductTable
            :products="products"
            @delete="deleteProduct"
        /> -->

        <ProductTable
    :products="products"
    @edit="editProduct"
    @delete="deleteProduct"
/>



    </div>

</template>

<script setup>

import { ref, onMounted } from 'vue'

import ProductService from '../../services/ProductService'

import ProductTable from '../../components/admin/ProductTable.vue'

import ProductForm from '../../components/admin/ProductForm.vue'

const products = ref([])

const keyword = ref('')

const selectedProduct = ref({})

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


const newProduct = () => {

    selectedProduct.value = {}

}

const editProduct = (product) => {

    selectedProduct.value = { ...product }

}

const cancelEdit = () => {

    selectedProduct.value = {}

}

const saveProduct = async(product)=>{

    try{

        if(product.id){

            await ProductService.updateProduct(product.id,product)

        }else{

            await ProductService.createProduct(product)

        }

        selectedProduct.value={}

        loadProducts()

    }

    catch(error){

        alert("Lưu sản phẩm thất bại!")

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

.product-page{
    background:#f5f6fa;
    min-height:100vh;
    padding:25px;
}

</style>