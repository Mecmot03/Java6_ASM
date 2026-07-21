import { createRouter, createWebHistory } from 'vue-router'

import Home from '../view/Home.vue'
import ProductDetail from '../view/ProductDetail.vue'
import Login from '../view/Login.vue'       // 👈 THÊM DÒNG NÀY
import Register from '../view/Register.vue' // 👈 THÊM DÒNG NÀY
import FlashSale from '../view/FlashSale.vue' // 1. Import component Flash Sale

const routes = [
    {
        path: '/',
        name: 'Home',
        component: Home
    },
    {
        path: '/product/:id',
        name: 'ProductDetail',
        component: ProductDetail,
        props: true
    },
    {
        path: '/login',                    // 👈 THÊM ROUTE ĐĂNG NHẬP
        name: 'Login',
        component: Login
    },
    {
        path: '/flashsale', // 2. Đường dẫn Flash Sale
        name: 'FlashSale',
        component: FlashSale
    },
    {
        path: '/register',                 // 👈 THÊM ROUTE ĐĂNG KÝ
        name: 'Register',
        component: Register
    }

]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router