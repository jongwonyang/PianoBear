<template>
  <!--변환된 파일-->
  <div class="container">
    <div class="filebox">
      <div class="icon">
        <v-icon>mdi-music</v-icon>
      </div>
      <div class="text">
        <!-- 변환된 악보가 있을 때 제목을 표시 -->
        <h4 v-if="store.convertedFile?.title">{{ store.convertedFile.title }}</h4>
        <!-- 변환된 악보가 없을 때 기본 메시지 표시 -->
        <h4 v-else>여기에 악보가 만들어집니다.</h4>
      </div>
      <!-- 제목 수정 아이콘은 악보가 있을 때만 표시 -->
      <div v-if="store.convertedFile?.title">
        <v-icon @click="openModal">mdi-pencil</v-icon>
      </div>
      <div v-if="store.convertedFile?.title">
        <button @click="saveSheet">저장</button>
      </div>
      <div class="text-center pa-4" v-if="store.convertedFile">
        <v-dialog v-model="isModalOpen" max-width="400" persistent>
          <v-card prepend-icon="mdi-map-marker" text="악보 제목 변경!" title="ㅇㅇㅇ">
            <template v-slot:actions>
              <v-spacer></v-spacer>
              <v-btn @click="closeModal">취소</v-btn>
              <v-btn @click="editTitle">수정</v-btn>
            </template>
          </v-card>
        </v-dialog>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { usePianoSheetStore } from "@/stores/pianosheet";

const store = usePianoSheetStore();
const isModalOpen = ref(false);

// 악보 데이터를 가져오는 함수
const fetchSheetData = async () => {
  await store.convertedFilefun(); // 스토어의 convertedFile 메서드 호출
  console.log(store.convertedFile);
};

const openModal = () => {
  isModalOpen.value = true;
};

const closeModal = () => {
  isModalOpen.value = false;
};

// 제목 수정 함수 (클릭 시 호출됨)
const editTitle = async (): Promise<void> => {
  await store.editTitle();
};

// 악보 저장
const saveSheet = () => {
  store.saveSheet();
};

onMounted(() => {
  fetchSheetData(); // 컴포넌트가 마운트될 때 악보 데이터 가져오기
});
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
</style>
