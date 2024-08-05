<template>
  <div class="container">
    <div class="record-list">
      <div v-for="record in store.practiceData" :key="record.id" class="record-item">
        <div class="record-content">
          <span class="date">
            {{ record.practiceDate[0].toString().slice(-2) }}.{{ record.practiceDate[1] }}.{{
              record.practiceDate[2]
            }}
          </span>
          <span class="images">
            <img
              v-for="n in record.practiceCount"
              :key="n"
              src="@/assets/images/산딸기.png"
              alt="Practice Image"
              class="practice-image"
            />
          </span>
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
.container {
  padding: 20px;
  border: 1px solid black;
}

.record-list {
  display: flex;
  flex-direction: column; /* 날짜별로 줄바꿈 */
  gap: 10px; /* 날짜 간격 */
  max-height: 150px; /* 최대 높이 설정 */
  overflow-y: auto;
  scrollbar-width: none;
}

.record-item {
  display: flex;
}

.record-content {
  display: flex;
  align-items: center; /* 가운데 정렬 */
  gap: 10px; /* 날짜와 이미지 사이 간격 */
}

.date {
  font-weight: bold;
}

.images {
  display: flex;
  gap: 5px; /* 이미지 사이 간격 */
}

.practice-image {
  width: 30px;
  height: 35px;
}
</style>
