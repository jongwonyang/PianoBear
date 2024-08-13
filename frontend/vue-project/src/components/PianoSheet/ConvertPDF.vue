<template>
  <!--PDF로 MXL파일을 만드는 버튼-->
  <div>
    <button @click="convertFile" v-if="store.loading && !store.convertedFile">시작</button>
  </div>

  <div class="loading-wave" v-if="!store.loading && !store.convertedFile">
    <div class="loading-bar"></div>
    <div class="loading-bar"></div>
    <div class="loading-bar"></div>
    <div class="loading-bar"></div>
  </div>

  <div>
    <button v-if="store.convertedFile">완료</button>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { usePianoSheetStore } from "@/stores/pianosheet";

const store = usePianoSheetStore();

const convertFile = async (): Promise<void> => {
  const selectedFile = store.selectedFile;
  store.loading = false;
  store.success = true;
  if (selectedFile) {
    store.convertFilefun(selectedFile);
  }
};

watch(
  () => store.selectedFile,
  () => {
    store.loading = true;
    store.convertedFile = null;
  }
);
</script>

<style scoped>
img {
  width: 150px;
  height: 150px;
}

/* From Uiverse.io by Praashoo7 */
button {
  outline: none;
  color: #947650;
  padding: 1em;
  padding-left: 3em;
  padding-right: 3em;
  border: 2px dashed #947650;
  border-radius: 15px;
  background-color: #fffff8;
  box-shadow:
    0 0 0 4px #fffff8,
    2px 2px 4px 2px rgba(0, 0, 0, 0.5);
  transition:
    0.1s ease-in-out,
    0.4s color;
}

button:active {
  transform: translateX(0.1em) translateY(0.1em);
  box-shadow:
    0 0 0 4px #fffff8,
    1.5px 1.5px 2.5px 1.5px rgba(0, 0, 0, 0.5);
}

/* From Uiverse.io by mrpumps31232 */
.loading-wave {
  width: 132px;
  height: 100px;
  display: flex;
  justify-content: center;
  align-items: flex-end;
}

.loading-bar {
  width: 20px;
  height: 10px;
  margin: 0 5px;
  background-color: #947650;
  border-radius: 5px;
  animation: loading-wave-animation 1s ease-in-out infinite;
}

.loading-bar:nth-child(2) {
  animation-delay: 0.1s;
}

.loading-bar:nth-child(3) {
  animation-delay: 0.2s;
}

.loading-bar:nth-child(4) {
  animation-delay: 0.3s;
}

@keyframes loading-wave-animation {
  0% {
    height: 10px;
  }

  50% {
    height: 50px;
  }

  100% {
    height: 10px;
  }
}
</style>
