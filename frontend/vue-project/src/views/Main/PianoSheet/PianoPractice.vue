<template>
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