<template>
  <div v-if="store.convertedFile && store.success" class="alert-container" @click="goToSheetDetail">
    <v-alert type="success"> 악보 변환이 완료되었습니다! 바로가기 클릭! </v-alert>
  </div>
  <LayoutNav style="z-index: 100" />
  <RouterView />
</template>

<script setup lang="ts">
import { usePianoSheetStore } from "@/stores/pianosheet";
import { RouterView, useRouter } from "vue-router";
import LayoutNav from "@/components/Layout/LayoutNav.vue";

const store = usePianoSheetStore();
const router = useRouter();

const goToSheetDetail = () => {
  if (store.convertedFile) {
    router.push(`/main/piano-sheet/${store.convertedFile.id}`);
    store.success = false; // 클릭 시 v-alert 숨기기
  }
};
</script>

<style scoped>
/* v-alert를 화면의 오른쪽 상단에 고정 */
.alert-container {
  position: fixed;
  top: 1rem;
  right: 2rem;
  width: 300px; /* 알림의 너비 설정 */
  z-index: 1000; /* 다른 요소들보다 위에 오도록 설정 */
  display: flex;
  justify-content: center;
  align-items: center;
}

.alert-container v-alert {
  width: 100%; /* 알림 크기 조정 */
  cursor: pointer; /* 클릭 가능한 상태 표시 */
}

.alert-container:hover {
  background-color: rgba(0, 0, 0, 0.1); /* 호버 시 배경색 변경 */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2); /* 호버 시 그림자 효과 추가 */
  transform: scale(1.1);
  transition: all 0.2s linear; /* 악보 천천히 커지게 하는 효과 */
}
</style>
