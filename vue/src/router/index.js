import { createRouter, createWebHistory } from 'vue-router'

import Home from '../view/Home.vue'
import ProductDetail from '../view/ProductDetail.vue'

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
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router