<template>
  <div v-if="show">
    <div class="modal fade show d-block" tabindex="-1">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow">
          <div class="modal-header bg-danger text-white">
            <h5 class="modal-title fw-bold">
              <i class="bi bi-exclamation-triangle-fill me-2"></i>
              {{ title }}
            </h5>
            <button
              type="button"
              class="btn-close btn-close-white"
              @click="$emit('close')"
            ></button>
          </div>

          <div class="modal-body py-4">
            <p class="mb-0 fs-6">
              {{ message }}
            </p>
          </div>

          <div class="modal-footer bg-light">
            <button
              type="button"
              class="btn btn-secondary px-4"
              @click="$emit('close')"
            >
              Hủy
            </button>
            <button
              type="button"
              class="btn btn-danger px-4 fw-bold"
              @click="$emit('confirm')"
            >
              Xóa
            </button>
          </div>
        </div>
      </div>
    </div>
    <div class="modal-backdrop fade show"></div>
  </div>
</template>

<script setup>
defineProps({
  show: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: 'Xác nhận xóa'
  },
  message: {
    type: String,
    default: 'Bạn có chắc chắn muốn xóa mục này không? Hành động này không thể hoàn tác.'
  }
})

defineEmits(['confirm', 'close'])
</script>