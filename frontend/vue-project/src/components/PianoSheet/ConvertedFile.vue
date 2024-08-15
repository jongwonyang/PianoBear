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
      </div>
      <div v-if="store.convertedFile?.title">
        <button>
          <router-link
            v-if="store.convertedFile"
            :to="`/main/piano-sheet/${store.convertedFile.id}`"
            class="router"
            >악보 바로가기</router-link
          >
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { usePianoSheetStore } from "@/stores/pianosheet";

const store = usePianoSheetStore();
</script>

<style scoped>
.container {
  /* border: 2px solid #947650; */
  background-color: #fffff8;
  color: #947650;
  width: 350px;
  height: 550px;
  padding: 10px;
  font-size: large;
}

.filebox {
  border: 2px dashed #947650;
  width: 350;
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
  background-color: #f5e5d1;
  color: white;
  width: 80px;
  height: 40px;
  border-radius: 15%;
}

button {
  outline: none;
  color: #947650;
  padding: 10px;
  border: 2px dashed #947650;
  border-radius: 15px;
  background-color: #f3ebe0;
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
</style>
