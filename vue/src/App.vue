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