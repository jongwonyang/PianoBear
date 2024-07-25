<template>
    <div :class="props.chat">
        <h2>채팅창</h2>
    </div>
</template>
<script lang="ts" setup>
    import {defineProps} from 'vue';

    const props = defineProps({
        chat: String
    })


</script>
<style scoped>
.chat1 {
    width: 350px;
    height: 630px;
    margin: 10px 0px;
    border-radius: 20px;
    background-color: #F0FFF0;
}
.chat2 {
    width: 350px;
    height: 470px;
    margin: 10px 0px;
    border-radius: 20px;
    background-color: #F0FFF0;
}
</style>