<template>

    <div class="container-fluid">

        <!-- Tiêu đề -->

        <div class="d-flex justify-content-between align-items-center mb-4">

            <h2 class="fw-bold">

                Quản lý User

            </h2>

            <button
                class="btn btn-success"
                @click="newUser"
            >

                <i class="bi bi-plus-circle me-2"></i>

                Thêm User

            </button>

        </div>

        <!-- Form -->

        <UserForm
            :user="selectedUser"
            @save="saveUser"
            @cancel="cancelEdit"
        />

        <!-- Thanh tìm kiếm -->

        <div class="card shadow-sm border-0 mb-4">

            <div class="card-body">

                <div class="row">

                    <div class="col-md-6">

                        <input
                            v-model="keyword"
                            class="form-control"
                            placeholder="Nhập họ tên hoặc email..."
                        >

                    </div>

                    <div class="col-md-2">

                        <button
                            class="btn btn-primary w-100"
                            @click="searchUser"
                        >

                            <i class="bi bi-search"></i>

                            Tìm

                        </button>

                    </div>

                    <div class="col-md-2">

                        <button
                            class="btn btn-secondary w-100"
                            @click="loadUsers"
                        >

                            Làm mới

                        </button>

                    </div>

                </div>

            </div>

        </div>

        <!-- Danh sách User -->

        <div v-if="loadError" class="alert alert-danger mb-3">
            {{ loadError }}
        </div>

        <UserTable
            :users="users"
            @edit="editUser"
            @delete="deleteUser"
            @changeStatus="changeStatus"
        />

    </div>

</template>

<script setup>

import { ref, onMounted } from 'vue'
import { confirmDialog } from '../../utils/dialog'
import { notify } from '../../utils/notify'

import UserService from '../../services/UserService'

import UserTable from '../../components/admin/UserTable.vue'

import UserForm from '../../components/admin/UserForm.vue'

const users = ref([])

const keyword = ref('')

const selectedUser = ref({})

const loadError = ref('')

const loadUsers = async () => {
    loadError.value = ''

    try {

        users.value = await UserService.getAllUsers()

    }

    catch (error) {

        users.value = []

        loadError.value = error.response?.data?.message || error.response?.data || 'Không tải được danh sách User!'

        notify(loadError.value, 'danger')

    }

}

const searchUser = () => {

    if (keyword.value.trim() === '') {

        loadUsers()

        return

    }

    const key = keyword.value.toLowerCase()

    users.value = users.value.filter(user =>

        user.fullName.toLowerCase().includes(key)

        ||

        user.email.toLowerCase().includes(key)

    )

}

const newUser = () => {

    selectedUser.value = {}

}

const editUser = (user) => {

    selectedUser.value = { ...user }

}

const cancelEdit = () => {

    selectedUser.value = {}

}

const saveUser = async (user) => {

    try {

        if (user.id) {

            await UserService.updateUser(user.id, user)

        }

        else {

            await UserService.createUser(user)

        }

        selectedUser.value = {}

        loadUsers()

    }

    catch (error) {

        notify(error.response?.data?.message || "Không thể lưu User!", 'danger')

    }

}

const deleteUser = async (id) => {
    if (!(await confirmDialog("Bạn chắc chắn muốn xóa User?"))) {
        return
    }

    try {

        await UserService.deleteUser(id)

        loadUsers()

    }

    catch (error) {

        notify(error.response?.data?.message || "Xóa User thất bại!", 'danger')

    }

}

const changeStatus = async (id) => {

    try {

        await UserService.changeStatus(id)

        loadUsers()

    }

    catch (error) {

        notify(error.response?.data?.message || "Không đổi được trạng thái!", 'danger')

    }

}

onMounted(() => {

    loadUsers()

})

</script>

<style scoped>

.card{

    border-radius:12px;

}

.container-fluid{

    padding:0;

}

</style>