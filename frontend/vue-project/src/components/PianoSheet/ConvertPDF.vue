<template>
    <!--PDF로 MXL파일을 만드는 버튼-->
    <div>
        <!-- <Loading :visible="isLoading" /> -->
        <button @click="convert">변환</button>
    </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import Loading from "./Loading.vue";
import { usePianoSheetStore } from "@/stores/pianosheet";

const isLoading = ref<boolean>(false);
const store = usePianoSheetStore();

// const fetchData = () => {
//   isLoading.value = true;
//   setTimeout(() => {
//     isLoading.value = false;
//     alert('Data loaded');
//   }, 2000);
// };

const convert = () => {
  store.postPDFConvert();
};
</script>

<style scoped>
img{
    width: 150px;
    height: 150px;
}
</style>