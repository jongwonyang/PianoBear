<template>
  <div class="button-container">
    <button @click="navigateTo('practice')" class="btn">연습하기</button>
    <button @click="navigateTo('challenge')" class="btn">도전하기</button>
    <div class="third-row">
      <button @click="toggleFavorite" class="btn">
        <v-icon>{{ isFavorite ? "mdi-heart" : "mdi-heart-outline" }}</v-icon>
        <span style="color: darkkhaki">&nbsp;즐겨찾기</span>
      </button>
      <button @click="openModal" class="btn">
        <span>
          <v-icon>mdi-trash-can-outline</v-icon>
        </span>
        <span style="color: darkred">&nbsp;삭제</span>
      </button>
    </div>
  </div>

  <!-- 삭제 모달 -->
  <div v-if="isModalOpen" class="modal-overlay">
    <div class="modal-content">
      <div class="close">
        <v-icon @click="closeModal">mdi-close</v-icon>
      </div>
      <div class="content">
        <div class="text">정말 {{ store.detailSheet?.title }} 악보를 삭제하시겠습니까?</div>
        <div class="modal-actions">
          <button @click="handleDelete(Number(route.params.id))">네</button>
          <button @click="closeModal">아니요</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from "vue-router";
import { ref, onMounted } from "vue";
import { usePianoSheetStore } from "@/stores/pianosheet";

const route = useRoute();
const router = useRouter();
const store = usePianoSheetStore();
const isModalOpen = ref(false);

const isFavorite = ref(false);

const checkFavorite = async (id: number) => {
  await store.checkFavorite(id);
  isFavorite.value = store.isFavorite;
};

const handleFavorite = async (id: number, favorite: boolean) => {
  await store.handleFavorite(id, favorite);
  isFavorite.value = favorite;
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
  router.back();
};

const navigateTo = (path: string) => {
  router.push(`/main/piano-sheet/${route.params.id}/${path}`);
};

const toggleFavorite = async () => {
  const id = Number(route.params.id);
  const newFavoriteStatus = !isFavorite.value;
  console.log(newFavoriteStatus);
  console.log(id);
  await handleFavorite(id, newFavoriteStatus);
};

onMounted(() => {
  checkFavorite(Number(route.params.id));
});
</script>

<style scoped>
.button-container {
  display: flex;
  flex-direction: column;
  /* 수직으로 정렬 */
  gap: 20px;
  /* 버튼 간격 설정 */
  color: #947650;
}

.third-row {
  display: flex;
  justify-content: space-between;
  /* 좌우로 공간 분배 */
}

.third-row button {
  flex: 1;
  /* 버튼이 동일한 너비를 가지도록 설정 */
}

.third-row button:first-of-type {
  margin-right: 1vw;
}

.third-row button:last-of-type {
  margin-left: 1vw;
}

.btn {
  background-color: #fff9e0;
  height: 9vh;
  border-radius: 10px;
  /* 기본 버튼 스타일에 가장 가까운 둥근 모서리 추가 */
  cursor: pointer;
  /* 클릭 가능한 버튼 모양 */
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 120%;
  /* 버튼 텍스트의 크기 설정 */
  font-weight: bold;
  box-shadow: 2px 2px 2px gray;
}

.btn:hover {
  background-color: #e0e0e0;
  /* 버튼 호버 상태 색상 */
}

.btn:active {
  background-color: #9e9e9e;
  /* 버튼 호버 상태 색상 */
}

/* 모달 스타일 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-content {
  background: #fffff8;
  border-radius: 8px;
  width: 80%;
  /* 부모 요소의 80% 너비로 설정 */
  width: 50vw;
  height: 60vh;
}

.content {
  padding: 100px;
  font-size: 150%;
  font-weight: bold;
}

.modal-actions {
  margin-top: 13vh;
  display: flex;
  justify-content: center;
  gap: 5vw;
}

.modal-actions button {
  background-color: #947650;
  color: black;
  border-radius: 10em;
  font-size: 20px;
  font-weight: bold;
  padding: 1em 2em;
  cursor: pointer;
  transition: all 0.3s ease-in-out;
  border: 1px solid black;
  box-shadow: 0 0 0 0 black;
  width: 20vw;
}

.modal-actions button:hover {
  transform: translateY(-4px) translateX(-2px);
  box-shadow: 2px 5px 0 0 black;
}

.modal-actions button:active {
  transform: translateY(2px) translateX(1px);
  box-shadow: 0 0 0 0 black;
}

.text {
  display: flex;
  justify-content: center;
}

.close {
  display: flex;
  justify-content: flex-end;
  padding: 20px;
}
</style>
