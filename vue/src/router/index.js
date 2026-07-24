import { createRouter, createWebHistory } from 'vue-router'

// =========================
// User Views
// =========================
import Home from '../view/Home.vue'
import ProductDetail from '../view/ProductDetail.vue'
import Login from '../view/Login.vue'
import Register from '../view/Register.vue'
import FlashSale from '../view/FlashSale.vue'
import CartView from '../view/CartView.vue'
import CheckoutView from '../view/CheckoutView.vue'
import OrderView from '../view/OrderView.vue'
import OrderHistory from '../view/OrderHistory.vue'
import UserInfo from '../view/UserInfo.vue'
import Layout from '../view/Layout.vue'
import AdminLayout from '../view/admin/AdminLayout.vue'

// =========================
// Admin Views
// =========================
import UserManagement from '../view/admin/UserManagement.vue'
import ProductManagement from '../view/admin/ProductManagement.vue'
import CategoryManagement from '../view/admin/CategoryManagement.vue'

const routes = [
    // =========================
    // Public layout + pages
    // =========================
    {
        path: '/',
        component: Layout,
        children: [
            {
                path: '',
                name: 'Home',
                component: Home
            },
            {
                path: 'cart',
                name: 'Cart',
                component: CartView
            },
            {
                path: 'checkout',
                name: 'Checkout',
                component: CheckoutView
            },
            {
                path: 'orders',
                name: 'Orders',
                component: OrderView,
                meta: { requiresAdmin: true }
            },
            {
                path: 'order-history',
                name: 'OrderHistory',
                component: OrderHistory,
                meta: { requiresAuth: true }
            },
            {
                path: 'user-info',
                name: 'UserInfo',
                component: UserInfo,
                meta: { requiresAuth: true }
            },
            {
                path: 'product/:id',
                name: 'ProductDetail',
                component: ProductDetail,
                props: true
            },
            {
                path: 'login',
                name: 'Login',
                component: Login
            },
            {
                path: 'register',
                name: 'Register',
                component: Register
            },
            {
                path: 'flash-sale',
                name: 'FlashSale',
                component: FlashSale,
                alias: '/flashsale'
            }
        ]
    },

    // =========================
    // Admin layout + pages
    // =========================
    {
        path: '/admin',
        component: AdminLayout,
        meta: { requiresAdmin: true },
        children: [
            {
                path: '',
                redirect: '/admin/users'
            },
            {
                path: 'users',
                name: 'UserManagement',
                component: UserManagement
            },
            {
                path: 'products',
                name: 'ProductManagement',
                component: ProductManagement
            },
            {
                path: 'categories',
                name: 'CategoryManagement',
                component: CategoryManagement
            }
        ]
    }
]

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

router.beforeEach((to) => {
    if (to.meta?.requiresAuth) {
        const token = localStorage.getItem('token')
        const userRaw = localStorage.getItem('user')

        if (!token || !userRaw) {
            return {
                path: '/login',
                query: { redirect: to.fullPath }
            }
        }
    }

    if (!to.meta?.requiresAdmin) {
        return true
    }

    const token = localStorage.getItem('token')
    const userRaw = localStorage.getItem('user')

    if (!token || !userRaw) {
        return {
            path: '/login',
            query: { redirect: to.fullPath }
        }
    }

    try {
        const user = JSON.parse(userRaw)
        if (user?.role === 'ROLE_ADMIN' || user?.role === 'ADMIN') {
            return true
        }
    } catch (error) {
        // Nếu user trong localStorage bị lỗi parse, ép đăng nhập lại
    }

    return { path: '/' }
})

export default router