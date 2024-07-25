<template>
    <div>
        <div class="screen">
            <JoinVideo />
            <Chat />
        </div>
        <Controller />
    </div>
</template>
<script lang="ts" setup>
    import Controller from '@/components/Community/Controller.vue';
    import JoinVideo from '@/components/Community/JoinVideo.vue';
    import Chat from '@/components/Community/JoinChat.vue';
    import Menu from '@/components/Community/Menu.vue';
</script>

<style scoped>
.screen {
    display: flex;
    width: 1500px;
    height: 640px;
}
</style>