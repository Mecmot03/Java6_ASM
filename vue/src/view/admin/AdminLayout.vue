<template>

    <div class="admin-layout">

        <!-- Sidebar -->
        <AdminSidebar />

        <!-- Nội dung -->
        <div class="main-content">

            <!-- Topbar -->
            <div class="topbar">

                <div>

                    <h4 class="mb-0 fw-bold">
                        Trang quản trị
                    </h4>

                    <small class="text-muted">
                        Hệ thống quản lý Thế Giới Điện Tử
                    </small>

                </div>

                <div class="admin-user">

                    <i class="bi bi-person-circle"></i>

                    <span>Xin chào Admin</span>

                </div>

            </div>

            <!-- Nội dung -->
            <div class="content">

                <router-view />

            </div>

        </div>

    </div>

</template>

<script setup>

import AdminSidebar from '../../components/admin/AdminSidebar.vue'

</script>

<style scoped>

.admin-layout{

    display:flex;

    min-height:100vh;

    background:#f5f6fa;

}

/* ========================= */
/* Main */
/* ========================= */

.main-content{

    flex:1;

    margin-left:260px;

}

/* ========================= */
/* Topbar */
/* ========================= */

.topbar{

    height:75px;

    background:white;

    display:flex;

    justify-content:space-between;

    align-items:center;

    padding:0 30px;

    box-shadow:0 2px 10px rgba(0,0,0,.08);

}

/* ========================= */
/* Admin */
/* ========================= */

.admin-user{

    display:flex;

    align-items:center;

    gap:10px;

    font-weight:600;

}

.admin-user i{

    font-size:28px;

    color:#2563eb;

}

/* ========================= */
/* Content */
/* ========================= */

.content{

    padding:30px;

}

</style>