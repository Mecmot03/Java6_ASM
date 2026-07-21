<template>
  <div class="card shadow-sm">
    <div class="card-header bg-primary text-white">
      <h5 class="mb-0">
        {{ isEdit ? 'Cập nhật sản phẩm' : 'Thêm sản phẩm' }}
      </h5>
    </div>

    <div class="card-body">

      <form @submit.prevent="saveProduct">

        <div class="row">

          <!-- Tên -->
          <div class="col-md-6 mb-3">
            <label class="form-label">Tên sản phẩm</label>
            <input
              type="text"
              class="form-control"
              v-model="form.name"
              required
            >
          </div>

          <!-- Thương hiệu -->
          <div class="col-md-6 mb-3">
            <label class="form-label">Thương hiệu</label>
            <input
              type="text"
              class="form-control"
              v-model="form.brand"
            >
          </div>

          <!-- Giá -->
          <div class="col-md-6 mb-3">
            <label class="form-label">Giá</label>
            <input
              type="number"
              class="form-control"
              v-model="form.price"
            >
          </div>

          <!-- Số lượng -->
          <div class="col-md-6 mb-3">
            <label class="form-label">Số lượng</label>
            <input
              type="number"
              class="form-control"
              v-model="form.quantity"
            >
          </div>

          <!-- Ảnh -->
          <div class="col-md-6 mb-3">
            <label class="form-label">Tên ảnh</label>
            <input
              type="text"
              class="form-control"
              v-model="form.image"
              placeholder="iphone16.jpg"
            >
          </div>

          <!-- Trạng thái -->
          <div class="col-md-6 mb-3">
            <label class="form-label">Trạng thái</label>

            <select
              class="form-select"
              v-model="form.status"
            >
              <option :value="true">Đang bán</option>
              <option :value="false">Ngừng bán</option>
            </select>

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
            @click="$emit('cancel')"
          >
            Hủy
          </button>

          <button
            class="btn btn-success"
            type="submit"
          >
            {{ isEdit ? 'Cập nhật' : 'Lưu sản phẩm' }}
          </button>

        </div>

      </form>

    </div>

  </div>
</template>

<script setup>

import { reactive, watch, computed } from 'vue'

const props = defineProps({

  product: {
    type: Object,
    default: () => ({})
  }

})

const emit = defineEmits([
  'save',
  'cancel'
])

const form = reactive({

  id: null,
  name: '',
  brand: '',
  price: 0,
  quantity: 0,
  image: '',
  description: '',
  status: true

})

watch(

  () => props.product,

  (value) => {

    Object.assign(form, {

      id: value?.id ?? null,
      name: value?.name ?? '',
      brand: value?.brand ?? '',
      price: value?.price ?? 0,
      quantity: value?.quantity ?? 0,
      image: value?.image ?? '',
      description: value?.description ?? '',
      status: value?.status ?? true

    })

  },

  {
    immediate: true
  }

)

const isEdit = computed(() => form.id != null)

const saveProduct = () => {

  emit('save', {

    ...form

  })

}

</script>

<style scoped>

.card{

    border-radius:12px;

}

textarea{

    resize:none;

}

</style>