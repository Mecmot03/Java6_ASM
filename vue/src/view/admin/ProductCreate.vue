<template>
  <div class="container py-4">

    <ProductForm
      title="Thêm sản phẩm"
      :modelValue="product"
      @save="saveProduct"
    />

  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'

import ProductForm from '../../components/admin/ProductForm.vue'
import ProductService from '../../services/ProductService'

const router = useRouter()

const product = reactive({
  name: '',
  brand: '',
  price: 0,
  quantity: 0,
  image: '',
  description: '',
  category: null,
  discountId: null,
  status: true
})

const saveProduct = async () => {

  try {

    await ProductService.create(product)

    alert("Thêm sản phẩm thành công!")

    router.push('/admin/products')

  } catch (error) {

    console.error(error)

    alert("Không thể thêm sản phẩm!")

  }

}
</script>