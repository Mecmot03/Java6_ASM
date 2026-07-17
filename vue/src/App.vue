<template>
  <Layout>

    <router-view />

  </Layout>
</template>

<script setup>
import Layout from './view/Layout.vue'
</script>

<style>
body{
  background:#f8f9fa;
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