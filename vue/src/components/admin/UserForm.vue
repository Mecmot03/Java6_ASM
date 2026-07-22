<template>

    <div class="card shadow border-0 mb-4">

        <div class="card-header bg-primary text-white">

            <h5 class="mb-0">

                {{ form.id ? "Cập nhật User" : "Thêm User" }}

            </h5>

        </div>

        <div class="card-body">

            <form @submit.prevent="save">

                <div class="row">

                    <div class="col-md-6 mb-3">

                        <label class="form-label fw-semibold">

                            Họ và tên

                        </label>

                        <input

                            v-model="form.fullName"

                            class="form-control"

                            placeholder="Nhập họ tên"

                            required

                        >

                    </div>

                    <div class="col-md-6 mb-3">

                        <label class="form-label fw-semibold">

                            Email

                        </label>

                        <input

                            type="email"

                            v-model="form.email"

                            class="form-control"

                            placeholder="Nhập email"

                            required

                        >

                    </div>

                    <div class="col-md-6 mb-3">

                        <label class="form-label fw-semibold">

                            Mật khẩu

                        </label>

                        <input

                            type="password"

                            v-model="form.password"

                            class="form-control"

                            :placeholder="form.id ? 'Để trống nếu không đổi' : 'Nhập mật khẩu'"

                        >

                    </div>

                    <div class="col-md-6 mb-3">

                        <label class="form-label fw-semibold">

                            Số điện thoại

                        </label>

                        <input

                            v-model="form.phone"

                            class="form-control"

                            placeholder="Nhập số điện thoại"

                        >

                    </div>

                    <div class="col-12 mb-3">

                        <label class="form-label fw-semibold">

                            Địa chỉ

                        </label>

                        <input

                            v-model="form.address"

                            class="form-control"

                            placeholder="Nhập địa chỉ"

                        >

                    </div>

                    <div class="col-md-6 mb-3">

                        <label class="form-label fw-semibold">

                            Phân quyền

                        </label>

                        <select

                            v-model="form.role"

                            class="form-select"

                        >

                            <option value="ROLE_USER">

                                USER

                            </option>

                            <option value="ROLE_ADMIN">

                                ADMIN

                            </option>

                        </select>

                    </div>

                </div>

                <hr>

                <button

                    class="btn btn-success me-2"

                    type="submit"

                >

                    <i class="bi bi-check-circle-fill me-1"></i>

                    Lưu

                </button>

                <button

                    type="button"

                    class="btn btn-secondary"

                    @click="cancel"

                >

                    <i class="bi bi-x-circle-fill me-1"></i>

                    Hủy

                </button>

            </form>

        </div>

    </div>

</template>

<script setup>

import { reactive, watch } from 'vue'

const props = defineProps({

    user:{

        type:Object,

        default:()=>({})

    }

})

const emit = defineEmits([

    'save',

    'cancel'

])

const emptyForm={

    id:null,

    fullName:'',

    email:'',

    password:'',

    phone:'',

    address:'',

    role:'ROLE_USER'

}

const form=reactive({

    ...emptyForm

})

watch(

    ()=>props.user,

    (value)=>{

        Object.assign(form,emptyForm,value)

    },

    {

        immediate:true

    }

)

const save=()=>{

    emit('save',{...form})

}

const cancel=()=>{

    Object.assign(form,emptyForm)

    emit('cancel')

}

</script>

<style scoped>

.card{

    border-radius:15px;

}

.card-header{

    font-weight:bold;

}

.form-control,

.form-select{

    border-radius:10px;

}

button{

    border-radius:10px;

}

</style>