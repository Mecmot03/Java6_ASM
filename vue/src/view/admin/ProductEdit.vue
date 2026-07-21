<template>
  <div class="container py-4">

    <ProductForm
      title="Cập nhật sản phẩm"
      :modelValue="product"
      @save="updateProduct"
    />

  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'

import ProductForm from '../../components/admin/ProductForm.vue'
import ProductService from '../../services/ProductService'

const router = useRouter()
const route = useRoute()

const product = reactive({
  id: null,
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

const loadProduct = async () => {

  try {

    const response = await ProductService.getById(route.params.id)

    Object.assign(product, response.data)

  }
  catch(error){

    console.error(error)

    alert("Không tải được sản phẩm!")

  }

}

const updateProduct = async () => {

  try{

    await ProductService.update(product.id, product)

    alert("Cập nhật thành công!")

    router.push('/admin/products')

  }
  catch(error){

    console.error(error)

    alert("Cập nhật thất bại!")

  }

}

onMounted(() => {

  loadProduct()

})
</script>