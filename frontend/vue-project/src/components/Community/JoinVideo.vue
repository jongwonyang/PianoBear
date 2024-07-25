<template>
    <div :class="props.videos">
        <v-sheet :class="surface"  v-for="i in num" :key="i" :elevation="3" rounded="lg">
            <div class="menus">
                <Menu :next="i" />
            </div>
        </v-sheet>
    </div>
</template>
<script lang="ts" setup>
    import {ref, defineProps} from 'vue';
    import Menu from './Menu.vue';
    
    const num = ref(['a1', 'a2', 'a3', 'a4', 'a5', 'a6']);
    const props = defineProps({
        videos: String,
        surface: String
    })

</script>
<style scoped>

.surface1 {
    position: relative;
    height: 291px;
    width: 300px;
    margin: 10px;
}
.videos1 {
    display: flex;
    flex-wrap: wrap;
    background-color: #FFF8D8;
    border-radius: 20px;
    width: 960px;
    margin: 10px 10px 0px 80px;
}
.surface2 {
    position: relative;
    height: 140px;
    width: 140px;
    margin: 10px;
}
.videos2 {
    display: flex;
    background-color: #FFF8D8;
    border-radius: 20px;
    width: 960px;
}

.menus {
    display: flex;
    justify-content: right;
    padding: 2%;
    opacity: 0.7;
}
</style>