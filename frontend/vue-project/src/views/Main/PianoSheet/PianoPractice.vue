<template>
    <h2>{{ route.params.id }}번째 악보 연습</h2>
    <PracticePiano />
</template>
<script setup>
import { useRoute } from 'vue-router';
import PracticePiano from '@/components/PianoSheet/PracticePiano.vue';

const route = useRoute();
</script>