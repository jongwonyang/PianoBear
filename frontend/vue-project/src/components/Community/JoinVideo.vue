<template>
    <div class="videos">
        <div v-for="i in num" :key="i" class="surface">
            <md-elevation></md-elevation>
            <Menu :next="i" />
        </div>
    </div>
</template>
<script lang="ts" setup>
    import {ref} from 'vue';
    import Menu from './Menu.vue';
    
    const num = ref(['a1', 'a2', 'a3', 'a4', 'a5', 'a6']);


</script>
<style scoped>

.surface {
    position: relative;
    border-radius: 16px;
    height: 291px;
    width: 300px;
    margin: 10px;

    --md-elevation-level: 3;
}
.videos {
    display: flex;
    flex-wrap: wrap;
    background-color: #FFF8D8;
    border-radius: 20px;
    width: 960px;
    margin: 10px 10px 0px 80px;
}


</style>