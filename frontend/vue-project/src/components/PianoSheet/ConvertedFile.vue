<template>
  <!--변환된 파일-->
  <div class="container">
    <div class="filebox">
      <div class="icon">
        <v-icon>mdi-music</v-icon>
      </div>
      <div class="text">
        <!-- 변환된 악보가 있을 때 제목을 표시 -->
        <span v-if="store.convertedFile?.title">{{ store.convertedFile.title }}</span>
        <!-- 변환된 악보가 없을 때 기본 메시지 표시 -->
        <span v-else>여기에 악보가 만들어집니다.</span>
        <span v-if="store.convertedFile?.title">
          <v-icon @click="openModal">mdi-pencil</v-icon>
        </span>
      </div>
      <div v-if="store.convertedFile?.title">
        <button @click="saveSheet" v-if="!goDetail"  class="button">저장</button>
        <router-link
          v-if="goDetail"
          :to="`/main/piano-sheet/${store.convertedFile.id}`"
          class="router"
          >악보 바로가기</router-link
        >
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { usePianoSheetStore } from "@/stores/pianosheet";

const store = usePianoSheetStore();
const goDetail = ref(false);

const openModal = () => {
  store.isOpen = true;
};

// 악보 저장
const saveSheet = () => {
  // console.log(store.convertedFile.id);
  store.saveSheet();
  // console.log(store.convertedFile.id);
  goDetail.value = true;
};
</script>

<style scoped>
.container {
  border: 2px solid #f5e5d1;
  color: #947650;
  width: 350px;
  height: 550px;
  padding: 10px;
  font-size: large;
}

.filebox {
  border: 2px dashed #f5e5d1;
  width: 350px;
  height: 530px;
  padding-left: 70px;
  padding-right: 70px;
  padding-top: 200px;
  padding-bottom: 200px;
}

.icon {
  margin-bottom: 10px;
}

.router {
  text-decoration: none;
  color: #947650;
  font-weight: bold;
}

.text {
  font-weight: bold;
  margin-top: 10px;
  margin-bottom: 10px;
}

.text span {
  margin-right: 10px;
}

.button {
  font-weight: bold;
  background-color: #947650;
  color: white;
  width: 80px;
  height: 40px;
  border-radius: 15%;
}
</style>
