<template>

    <aside class="admin-sidebar">

        <div class="top-section">

            <!-- Logo -->

            <div class="logo">

                <i class="bi bi-speedometer2"></i>

                <h3>ADMIN</h3>

                <small>Thế Giới Điện Tử</small>

            </div>

            <!-- Menu -->

            <ul class="menu">

                <li>

                    <router-link to="/admin/users">

                        <i class="bi bi-people-fill"></i>

                        <span>Quản lý User</span>

                    </router-link>

                </li>

                <li>

                    <router-link to="/admin/products">

                        <i class="bi bi-phone-fill"></i>

                        <span>Quản lý sản phẩm</span>

                    </router-link>

                </li>

                <li>

                    <router-link to="/admin/categories">

                        <i class="bi bi-tags-fill"></i>

                        <span>Quản lý thể loại</span>

                    </router-link>

                </li>

            </ul>

        </div>

        <!-- Footer -->

        <div class="sidebar-footer">

            <router-link
                to="/"
                class="home-btn"
            >

                <i class="bi bi-house-door-fill"></i>

                <span>Trang chủ</span>

            </router-link>

        </div>

    </aside>

</template>

<script setup>

</script>

<style scoped>

.admin-sidebar{

    width:260px;

    height:100vh;

    position:fixed;

    top:0;

    left:0;

    background:#1e293b;

    color:white;

    padding:25px 20px;

    box-shadow:2px 0 10px rgba(0,0,0,.1);

    display:flex;

    flex-direction:column;

}

.top-section{

    flex:1;

}

/* ========================= */
/* Logo */
/* ========================= */

.logo{

    text-align:center;

    margin-bottom:35px;

}

.logo i{

    font-size:42px;

    color:#38bdf8;

}

.logo h3{

    margin-top:10px;

    margin-bottom:0;

    font-weight:bold;

}

.logo small{

    color:#cbd5e1;

}

/* ========================= */
/* Menu */
/* ========================= */

.menu{

    list-style:none;

    padding:0;

    margin-top:30px;

}

.menu li{

    margin-bottom:15px;

}

.menu a{

    display:flex;

    align-items:center;

    gap:12px;

    color:white;

    text-decoration:none;

    padding:14px;

    border-radius:10px;

    transition:.3s;

    font-size:15px;

}

.menu a:hover{

    background:#334155;

    transform:translateX(5px);

}

.router-link-active{

    background:#2563eb;

}

.menu i{

    font-size:18px;

}

/* ========================= */
/* Footer */
/* ========================= */

.sidebar-footer{

    border-top:1px solid rgba(255,255,255,.15);

    padding-top:20px;

}

.home-btn{

    display:flex;

    align-items:center;

    justify-content:center;

    gap:10px;

    width:100%;

    padding:14px;

    background:#dc3545;

    color:white;

    text-decoration:none;

    border-radius:10px;

    font-weight:600;

    transition:.3s;

}

.home-btn:hover{

    background:#bb2d3b;

    color:white;

    transform:none;

}

</style>