<template>
    <h2>{{ route.params.id }}번째 악보 도전</h2>

    

</template>
<script setup>
import { useRoute } from 'vue-router';

const route = useRoute();
</script>