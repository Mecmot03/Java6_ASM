// import { createRouter, createWebHistory } from 'vue-router'

// import Home from '../view/Home.vue'
// import ProductDetail from '../view/ProductDetail.vue'

// const routes = [
//     {
//         path: '/',
//         name: 'Home',
//         component: Home
//     },
//     {
//         path: '/product/:id',
//         name: 'ProductDetail',
//         component: ProductDetail,
//         props: true
//     }
// ]

// const router = createRouter({
//     history: createWebHistory(),
//     routes
// })

// export default router















import { createRouter, createWebHistory } from 'vue-router'

// =========================
// User
// =========================
import Home from '../view/Home.vue'
import ProductDetail from '../view/ProductDetail.vue'
import Login from '../view/Login.vue'       // 👈 THÊM DÒNG NÀY
import Register from '../view/Register.vue' // 👈 THÊM DÒNG NÀY
import FlashSale from '../view/FlashSale.vue' // 1. Import component Flash Sale

// =========================
// Admin
// =========================
import ProductManagement from '../view/admin/ProductManagement.vue'
// import ProductCreate from '../view/admin/ProductCreate.vue'
// import ProductEdit from '../view/admin/ProductEdit.vue'

const routes = [

    // =========================
    // User
    // =========================

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

    // =========================
    // Admin Product
    // =========================

    {
        path: '/admin/products',
        name: 'ProductManagement',
        component: ProductManagement
    },

    // {
    //     path: '/admin/products/create',
    //     name: 'ProductCreate',
    //     component: ProductCreate
    // },

    // {
    //     path: '/admin/products/edit/:id',
    //     name: 'ProductEdit',
    //     component: ProductEdit,
    //     props: true
    // }

]

const router = createRouter({

    history: createWebHistory(),

    routes

})

export default router