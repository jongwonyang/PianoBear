<template>
    <h2>{{ route.params.id }}번째 악보 연습</h2>

    <PracticePiano :challenge="false" />
</template>
<script setup>
import { useRoute } from 'vue-router';
import PracticePiano from '@/components/PianoSheet/PracticePiano.vue';

const route = useRoute();
</script>
<style scoped>
h2 {
    position: absolute;
    top: 3vh;
    left: 30vh;
    width: 25vh;

}
</style>