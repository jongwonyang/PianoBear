<template>
  <!--PDF로 MXL파일을 만드는 버튼-->
  <div class="container">
    <!-- <Loading :visible="isLoading" /> -->
    <button @click="convertFile" class="round-button">
        <v-icon>mdi-arrow-right-thick</v-icon>
        <h5>시작</h5>
    </button>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import Loading from "./Loading.vue";
import { usePianoSheetStore } from "@/stores/pianosheet";

// const isLoading = ref<boolean>(false);
const store = usePianoSheetStore();

// const fetchData = () => {
//   isLoading.value = true;
//   setTimeout(() => {
//     isLoading.value = false;
//     alert('Data loaded');
//   }, 2000);
// };

const convertFile = async (): Promise<void> => {
  const selectedFile = store.selectedFile;
  if (selectedFile) {
    store.convertFilefun(selectedFile);
  }
};
</script>

<style scoped>
img {
  width: 150px;
  height: 150px;
}

.round-button {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  border: none;
  background-color: #F5E5D1;
  color: #947650;
  font-size: 16px;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  flex-direction: column; /* 수직 정렬 */
}

.round-button:hover {
  background-color: #7a5a43;
}
</style>
