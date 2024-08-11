<template>
  <div class="page">
    <div class="text">연습기록</div>
    <div class="container">
      <div class="record-list">
        <div v-for="record in store.practiceData" :key="record.id" class="record-item">
          <div class="record-content">
            <div class="date">
              {{ record.practiceDate[0].toString().slice(-2) }}.{{ record.practiceDate[1] }}.{{
                record.practiceDate[2]
              }}
            </div>
            <div class="images">
              <img
                v-for="n in record.practiceCount"
                :key="n"
                src="@/assets/images/산딸기.png"
                alt="Practice Image"
                class="practice-image"
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRoute } from "vue-router";
import { onMounted } from "vue";
import { usePianoSheetStore } from "@/stores/pianosheet";

const store = usePianoSheetStore();
const route = useRoute();

// 악보 데이터를 가져오는 함수
const fetchPracticeData = async (id: number) => {
  await store.practiceDatafun(id); // 스토어의 practiceDatafun 메서드 호출
};

onMounted(() => {
  fetchPracticeData(Number(route.params.id)); // 1대신 musicId 들어가야 함
});
</script>

<style scoped>
.text {
  display: flex;
  justify-content: center; /* 수평 가운데 정렬 */
  align-items: center; /* 수직 가운데 정렬 */
  height: 50px; /* 부모 요소의 높이를 100vh로 설정하여 화면 중앙에 위치시킴 */
  font-size: large;
  font-weight: bold;
  margin-top: 1vh;
}

.page {
  border: 2px solid #f5e5d1;
  border-radius: 5px;
  background-color: #fffff8;
}

.container {
  padding: 20px;
  width: 400px;
  height: 250px;
}

.record-list {
  display: flex;
  flex-direction: column; /* 날짜별로 줄바꿈 */
  gap: 10px; /* 날짜 간격 */
  max-height: 200px; /* 최대 높이 설정 */
  overflow-y: auto;
  /* scrollbar-width: none; */
}

.record-item {
  display: flex;
}

.record-content {
  display: flex;
  align-items: center; /* 가운데 정렬 */
  gap: 10px; /* 날짜와 이미지 사이 간격 */
  margin-bottom: 20px;
}

.date {
  font-weight: bold;
  width: 80px;
  font-size: large;
}

.images {
  display: flex;
  gap: 15px; /* 이미지 사이 간격 */
  width: 170px;
}

.practice-image {
  width: 35px;
  height: 40px;
}
</style>
