<template>
  <div class="button-container">
    <v-btn variant="outlined" @click="navigateTo('practice')">연습하기</v-btn>
    <v-btn variant="outlined" @click="navigateTo('challenge')">도전하기</v-btn>
    <div class="third-row">
      <v-btn variant="outlined" @click="handleFavorite(Number(route.params.id))">즐겨찾기</v-btn>
      <v-btn variant="outlined" @click="openModal">삭제</v-btn>
    </div>
  </div>
  <!--삭제 모달-->
  <div class="text-center pa-4">
    <v-dialog v-model="isModalOpen" max-width="400" persistent>
      <v-card prepend-icon="mdi-map-marker" text="정말 삭제하시겠습니까?" title="ㅇㅇㅇ 삭제">
        <template v-slot:actions>
          <v-spacer></v-spacer>
          <v-btn @click="closeModal">취소</v-btn>
          <v-btn @click="handleDelete(Number(route.params.id))">삭제</v-btn>
        </template>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from "vue-router";
import { ref } from "vue";
import { usePianoSheetStore } from "@/stores/pianosheet";

const route = useRoute();
const router = useRouter();
const store = usePianoSheetStore();
const isModalOpen = ref(false);

const handleFavorite = async (id: number) => {
  await store.handleFavorite(id);
};

const openModal = () => {
  isModalOpen.value = true;
};

const closeModal = () => {
  isModalOpen.value = false;
};

const handleDelete = async (id: number) => {
  await store.handleDelete(id);
  closeModal();
};

const navigateTo = (path: string) => {
  router.push(`/main/piano-sheet/${route.params.id}/${path}`);
};
</script>

<style scoped>
.button-container {
  display: flex;
  flex-direction: column; /* 수직으로 정렬 */
  gap: 10px; /* 버튼 간격 설정 */
}

.third-row {
  display: flex;
  justify-content: space-between; /* 좌우로 공간 분배 */
  gap: 10px; /* 버튼 간격 설정 */
}

.third-row v-btn {
  flex: 1; /* 버튼이 동일한 너비를 가지도록 설정 */
}

.third-row v-btn:first-of-type {
  margin-right: 10px; /* '즐겨찾기' 버튼 오른쪽에 공간 추가 */
}

.third-row v-btn:last-of-type {
  margin-left: 10px; /* '삭제' 버튼 왼쪽에 공간 추가 */
}
</style>
