<template>
    <!--PDF로 MXL파일을 만드는 버튼-->
    <div>
        <Loading :visible="isLoading" />
        <button @click="fetchData">변환</button>
    </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import Loading from "./Loading.vue";

const isLoading = ref<boolean>(false);

const fetchData = () => {
  isLoading.value = true;
  setTimeout(() => {
    isLoading.value = false;
    alert('Data loaded');
  }, 2000); // Simulate an API call with a 2 second delay
};
</script>

<style scoped>
img{
    width: 150px;
    height: 150px;
}
</style>