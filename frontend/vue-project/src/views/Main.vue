<template>
    <LayoutNav style="z-index: 100;" />
    <RouterView />
</template>
<script setup>
import { RouterView } from 'vue-router';
import LayoutNav from '@/components/Layout/LayoutNav.vue';

</script>