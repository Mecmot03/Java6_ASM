<template>

    <div class="card shadow-sm border-0">

        <div class="card-header bg-dark text-white">

            <h5 class="mb-0">

                Danh sách User

            </h5>

        </div>

        <div class="table-responsive">

            <table class="table table-hover align-middle mb-0">

                <thead class="table-light">

                    <tr>

                        <th>ID</th>

                        <th>Họ tên</th>

                        <th>Email</th>

                        <th>SĐT</th>

                        <th>Quyền</th>

                        <th>Trạng thái</th>

                        <th class="text-center">

                            Thao tác

                        </th>

                    </tr>

                </thead>

                <tbody>

                    <tr

                        v-for="user in users"

                        :key="user.id"

                    >

                        <td>

                            {{ user.id }}

                        </td>

                        <td>

                            <strong>

                                {{ user.fullName }}

                            </strong>

                        </td>

                        <td>

                            {{ user.email }}

                        </td>

                        <td>

                            {{ user.phone }}

                        </td>

                        <td>

                            <span

                                class="badge"

                                :class="user.role=='ROLE_ADMIN'
                                    ?'bg-danger'
                                    :'bg-primary'"

                            >

                                {{ user.role }}

                            </span>

                        </td>

                        <td>

                            <span

                                class="badge"

                                :class="user.enabled
                                    ?'bg-success'
                                    :'bg-secondary'"

                            >

                                {{ user.enabled ? "Hoạt động" : "Đã khóa" }}

                            </span>

                        </td>

                        <td class="text-center">

                            <button

                                class="btn btn-warning btn-sm me-2"

                                @click="$emit('edit',user)"

                            >

                                <i class="bi bi-pencil-square"></i>

                            </button>

                            <button

                                class="btn btn-danger btn-sm me-2"

                                @click="$emit('delete',user.id)"

                            >

                                <i class="bi bi-trash"></i>

                            </button>

                            <button

                                class="btn btn-secondary btn-sm"

                                @click="$emit('changeStatus',user.id)"

                            >

                                <i

                                    class="bi"

                                    :class="user.enabled
                                        ?'bi-lock-fill'
                                        :'bi-unlock-fill'"

                                ></i>

                            </button>

                        </td>

                    </tr>

                    <tr v-if="users.length==0">

                        <td

                            colspan="7"

                            class="text-center py-5 text-muted"

                        >

                            <i class="bi bi-inbox fs-2"></i>

                            <br>

                            Chưa có dữ liệu User

                        </td>

                    </tr>

                </tbody>

            </table>

        </div>

    </div>

</template>

<script setup>

defineProps({

    users:{

        type:Array,

        default:()=>[]

    }

})

defineEmits([

    "edit",

    "delete",

    "changeStatus"

])

</script>

<style scoped>

.card{

    border-radius:15px;

}

.card-header{

    font-weight:bold;

}

table{

    font-size:14px;

}

th{

    white-space:nowrap;

}

td{

    vertical-align:middle;

}

.badge{

    font-size:13px;

    padding:8px 12px;

}

button{

    border-radius:8px;

}

.table tbody tr:hover{

    background:#f8f9fa;

}

</style>