<template>
    <div class="loading-spinner" v-if="visible">
      <div class="spinner">
        <img :src="currentImage" alt="변환 중.." />
      </div>
    </div>
  </template>
  
  <script setup lang="ts">
  import { ref, watchEffect, onMounted, onUnmounted } from 'vue';
  
  const props = defineProps({
    visible: {
      type: Boolean,
      default: false,
    },
  });
  
  const image1 = "@/assets/characters/토니/토니악보변환.png";
  const image2 = "@/assets/characters/토니/토니놀람.png";
  
  const currentImage = ref(image1);
  
  let intervalId: number;
  
  const startImageSwitching = () => {
    intervalId = window.setInterval(() => {
        currentImage.value = currentImage.value === image1 ? image2 : image1;
        console.log(currentImage.value);
    }, 1000); // Change image every second
  };
  
  const stopImageSwitching = () => {
    clearInterval(intervalId);
  };
  
  watchEffect(() => {
    if (props.visible) {
      startImageSwitching();
    } else {
      stopImageSwitching();
    }
  });
  
  onMounted(() => {
    if (props.visible) {
      startImageSwitching();
    }
  });
  
  onUnmounted(() => {
    stopImageSwitching();
  });
  </script>
  
  <style scoped>
  .loading-spinner {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
  }
  
  .spinner img {
    width: 100px;
    height: 100px;
  }
  </style>
  