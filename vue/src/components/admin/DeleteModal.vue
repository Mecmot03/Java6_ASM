<template>

    <div
        v-if="show"
        class="modal fade show d-block"
        tabindex="-1"
    >

        <div class="modal-dialog modal-dialog-centered">

            <div class="modal-content">

                <div class="modal-header bg-danger text-white">

                    <h5 class="modal-title">

                        Xác nhận xóa

                    </h5>

                    <button
                        class="btn-close btn-close-white"
                        @click="$emit('close')"
                    ></button>

                </div>

                <div class="modal-body">

                    <p class="mb-0">

                        Bạn có chắc chắn muốn xóa sản phẩm này không?

                    </p>

                </div>

                <div class="modal-footer">

                    <button
                        class="btn btn-secondary"
                        @click="$emit('close')"
                    >

                        Hủy

                    </button>

                    <button
                        class="btn btn-danger"
                        @click="$emit('confirm')"
                    >

                        Xóa

                    </button>

                </div>

            </div>

        </div>

    </div>

    <div
        v-if="show"
        class="modal-backdrop fade show"
    ></div>

</template>

<script setup>

defineProps({

    show:{

        type:Boolean,

        default:false

    }

})

defineEmits([

    "confirm",

    "close"

])

</script>