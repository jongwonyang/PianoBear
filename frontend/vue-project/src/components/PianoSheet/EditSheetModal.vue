<template>
  <div class="modal-overlay" @click="closeModal">
    <div class="modal-content" @click.stop>
      <div class="close">
        <v-icon @click="closeModal">mdi-close</v-icon>
      </div>
      <div class="content">
        <div class="title">변환된 악보 수정</div>
        <div class="form">
          <div class="form-group">
            <label for="title">제목 : </label>
            <input type="text" id="title" v-model="title" />
          </div>
        </div>
        <button class="submit-button" @click="submitChanges">수정 완료</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { usePianoSheetStore } from "@/stores/pianosheet";

const store = usePianoSheetStore();
const title = ref(store.detailSheet?.title || "");

const closeModal = () => {
  // if (store.convertedFile) {
  //   store.convertedFile.title = title.value;
  // }
  store.isOpen = false; // 모달을 닫도록 설정
};

const submitChanges = () => {
  if (title.value.trim() === "") {
    console.error("Title is empty.");
    return;
  }

  console.log(title.value);
  store.editSheet(title.value);
  closeModal(); // 수정 완료 후 모달 닫기
};
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5); /* 반투명 배경 */
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000; /* 다른 요소 위에 표시 */
}

.modal-content {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  position: relative;
  width: 400px;
}

.close {
  position: absolute;
  top: 10px;
  right: 10px;
  cursor: pointer;
}

.content {
  padding: 20px;
  color: #947650;
  font-weight: bold;
}

.title {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 20px;
  text-align: center;
}

.form {
  display: flex;
  flex-direction: column;
  gap: 15px; /* 요소 간의 간격 설정 */
}

.form-group {
  display: flex;
  gap: 5px;
}

.submit-button {
  margin-top: 20px;
  margin-left: 200px;
  background-color: #947650;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  text-align: center;
  font-weight: bold;
}

.submit-button:hover {
  background-color: #5d482f;
}
</style>
