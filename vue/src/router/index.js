import { createRouter, createWebHistory } from 'vue-router'

// =========================
// User Views
// =========================
import Home from '../view/Home.vue'
import ProductDetail from '../view/ProductDetail.vue'
import Login from '../view/Login.vue'
import Register from '../view/Register.vue'
import FlashSale from '../view/FlashSale.vue'

// =========================
// Admin Views
// =========================
import ProductManagement from '../view/admin/ProductManagement.vue'
import CategoryManagement from '../view/admin/CategoryManagement.vue'
// Bỏ comment 2 dòng dưới nếu bạn đã tạo file ProductCreate.vue và ProductEdit.vue
// import ProductCreate from '../view/admin/ProductCreate.vue'
// import ProductEdit from '../view/admin/ProductEdit.vue'

const routes = [
    // =========================
    // User Routes
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
    {
        path: '/login',
        name: 'Login',
        component: Login
    },
    {
        path: '/register',
        name: 'Register',
        component: Register
    },
    {
        path: '/flashsale',
        name: 'FlashSale',
        component: FlashSale
    },

    // =========================
    // Admin Routes
    // =========================
    {
        path: '/admin/products',
        name: 'ProductManagement',
        component: ProductManagement
    },
    {
        path: '/admin/categories',
        name: 'CategoryManagement',
        component: CategoryManagement
    }
    // {
    //   path: '/admin/products/create',
    //   name: 'ProductCreate',
    //   component: ProductCreate
    // },
    // {
    //   path: '/admin/products/edit/:id',
    //   name: 'ProductEdit',
    //   component: ProductEdit,
    //   props: true
    // }
]

// const router = createRouter({
//     history: createWebHistory(),
//     routes
// })


const router = createRouter({

    history: createWebHistory(),

    routes,

    scrollBehavior() {

        return {

            top: 0,

            behavior: "smooth"

        }

    }

})


export default router