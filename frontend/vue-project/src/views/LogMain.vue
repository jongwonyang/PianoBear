<template>
    <div class="log-main-container">
        <router-view />
    </div>
</template>

<script setup>
</script>

<style scoped>
.log-main-container {
    width: 100vw;
    height: 100vh;
    background-size: cover;
    animation: backgroundChange 20s infinite;
}

@keyframes backgroundChange {
    0% {
        background-image: url('@/assets/images/background.webp');
    }

    20% {
        background-image: url('@/assets/images/background2.webp');
    }

    40% {
        background-image: url('@/assets/images/background3.webp');
    }

    60% {
        background-image: url('@/assets/images/background4.webp');
    }

    80% {
        background-image: url('@/assets/images/background5.webp');
    }

    100% {
        background-image: url('@/assets/images/background.webp');
    }
}
</style>