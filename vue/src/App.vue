<template>

  <!-- Trang Admin -->

  <template v-if="$route.path.startsWith('/admin')">

    <AdminLayout />

  </template>

  <!-- Trang User -->

  <template v-else>

    <Layout>

      <router-view />

    </Layout>

  </template>

</template>

<script setup>

import { useRoute } from 'vue-router'

import Layout from './view/Layout.vue'

import AdminLayout from './view/admin/AdminLayout.vue'

const $route = useRoute()

</script>

<style>

body{

  background:#f8f9fa;

  margin:0;

}

</style>

<!-- <template>
  <Layout>
    <Home />
  </Layout>
</template>

<script setup>
import Layout from './view/Layout.vue' // Đường dẫn tới file Layout của Nhi
import Home from './view/Home.vue'     // Đường dẫn tới file Home vừa tạo
</script>

<style>
/* CSS toàn trang */
body {
  background-color: #f8f9fa;
}
</style> -->