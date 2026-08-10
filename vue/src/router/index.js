import { createRouter, createWebHistory } from 'vue-router'

// Layouts
import Layout from '../view/Layout.vue'
import AdminLayout from '../view/admin/AdminLayout.vue'

// User Views
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
import FavoritesView from '../view/FavoritesView.vue'

// Admin Views
import UserManagement from '../view/admin/UserManagement.vue'
import ProductManagement from '../view/admin/ProductManagement.vue'
import CategoryManagement from '../view/admin/CategoryManagement.vue'

const routes = [
    {
        path: '/',
        component: Layout,
        children: [
            { path: '', name: 'Home', component: Home },
            { path: 'cart', name: 'Cart', component: CartView },
            
            // 🟢 BẮT BUỘC CÓ ROLE_USER MỚI VÀO THANH TOÁN & XEM LỊCH SỬ CÁ NHÂN
            { path: 'checkout', name: 'Checkout', component: CheckoutView, meta: { requiresUserRole: true } },
            { path: 'order-history', name: 'OrderHistory', component: OrderHistory, meta: { requiresUserRole: true } },
            
            // 🟢 BẮT BUỘC CHỈ CÓ ROLE_STAFF MỚI VÀO QUẢN LÝ / DUYỆT ĐƠN HÀNG
            { path: 'orders', name: 'Orders', component: OrderView, meta: { requiresStaffRole: true } },
            
            { path: 'user-info', name: 'UserInfo', component: UserInfo, meta: { requiresAuth: true } },
            { path: 'favorites', name: 'Favorites', component: FavoritesView, meta: { requiresAuth: true } },
            { path: 'product/:id', name: 'ProductDetail', component: ProductDetail, props: true },
            { path: 'login', name: 'Login', component: Login },
            { path: 'register', name: 'Register', component: Register },
            { path: 'flash-sale', name: 'FlashSale', component: FlashSale, alias: '/flashsale' }
        ]
    },

    {
        path: '/admin',
        component: AdminLayout,
        meta: { requiresAdmin: true },
        children: [
            { path: '', redirect: '/admin/users' },
            { path: 'users', name: 'UserManagement', component: UserManagement },
            { path: 'products', name: 'ProductManagement', component: ProductManagement },
            { path: 'categories', name: 'CategoryManagement', component: CategoryManagement }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes,
    scrollBehavior() {
        return { top: 0, behavior: "smooth" }
    }
})

router.beforeEach((to) => {
    const token = localStorage.getItem('token')
    const userRaw = localStorage.getItem('user')

    let isUser = false
    let isStaff = false
    let isAdmin = false

    if (userRaw) {
        try {
            const userObj = JSON.parse(userRaw)
            const userStr = JSON.stringify(userObj).toUpperCase()
            
            isUser = userStr.includes('ROLE_USER') || userStr.includes('"USER"')
            isStaff = userStr.includes('ROLE_STAFF') || userStr.includes('"STAFF"')
            isAdmin = userStr.includes('ROLE_ADMIN') || userStr.includes('"ADMIN"')
        } catch (e) {
            console.error("Lỗi parse thông tin user từ localStorage:", e)
        }
    }

    // 1. Kiểm tra yêu cầu đăng nhập cơ bản
    if (to.meta?.requiresAuth && (!token || !userRaw)) {
        return { path: '/login', query: { redirect: to.fullPath } }
    }

    // 2. Chức năng Mua hàng & Xem lịch sử cá nhân (/order-history) -> CHỈ CÓ ROLE_USER
    if (to.meta?.requiresUserRole) {
        if (!token || !userRaw) {
            return { path: '/login', query: { redirect: to.fullPath } }
        }
        if (!isUser) {
            if (isAdmin) return { path: '/admin' }
            if (isStaff) return { path: '/orders' }
            return { path: '/' }
        }
    }

    // 3. Chức năng Quản lý/Duyệt đơn hàng (/orders) -> CHỈ DÀNH CHO ROLE_STAFF (ADMIN KHÔNG VÀO)
    if (to.meta?.requiresStaffRole) {
        if (!token || !userRaw) {
            return { path: '/login', query: { redirect: to.fullPath } }
        }
        // Nếu không phải STAFF (bao gồm cả Admin hoặc User thường) -> Đuổi về đúng vị trí
        if (!isStaff) {
            if (isAdmin) return { path: '/admin' }
            return { path: '/' }
        }
    }

    // 4. Trang Quản trị hệ thống (/admin) -> BẮT BUỘC CHỈ CÓ ROLE_ADMIN
    if (to.meta?.requiresAdmin || to.path.startsWith('/admin')) {
        if (!token || !userRaw) {
            return { path: '/login', query: { redirect: to.fullPath } }
        }
        if (!isAdmin) {
            return isStaff ? { path: '/orders' } : { path: '/' }
        }
    }

    return true
})

export default router