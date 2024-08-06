<template>
    <h2>{{ route.params.id }}번째 악보 연습</h2>
    <PracticePiano />
</template>
<script setup>
import { useRoute } from 'vue-router';
import PracticePiano from '@/components/PianoSheet/PracticePiano.vue';

const route = useRoute();
</script>
<style>
h2 {
    position: absolute;
    top: 3vh;
    left: 13vh;
    width: 25vh;

}
</style>