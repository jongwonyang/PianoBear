<template>
  <div>
    <div>
      {{ store.detailSheet?.title }}
    </div>
    <div>
      <button @click="makeImg">악보 이미지 만들기</button>
    </div>
    <div class="container">
      <div class="left">
        <PianoSheet />
      </div>
      <div class="right">
        <div class="detail">
          <DetailPractice />
        </div>
        <Buttons />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import PianoSheet from "@/components/PianoSheet/PianoSheet.vue";
import DetailPractice from "@/components/PianoSheet/DetailPractice.vue";
import Buttons from "@/components/PianoSheet/Buttons.vue";
import { usePianoSheetStore } from "@/stores/pianosheet";

const route = useRoute();
const store = usePianoSheetStore();
const id = ref<number | null>(null);

const makeImg = async () => {
  if (id.value !== null) {
    console.log("클릭됨");
    console.log(id.value);
    await store.makeImg(id.value);
  } else {
    console.error("ID is null, cannot make image.");
  }
};

// 악보 데이터를 가져오는 함수
const fetchDetailData = async (id: number) => {
  await store.detailSheetfun(id); // 스토어의 practiceDatafun 메서드 호출
};

onMounted(() => {
  id.value = Number(route.params.id);
  fetchDetailData(Number(route.params.id)); // 1대신 musicId 들어가야 함
});
</script>

<style scoped>
.container {
  display: flex; /* Flexbox 레이아웃 사용 */
}

.left {
  flex: 1; /* 왼쪽 콘텐츠 영역이 가로 공간을 차지하도록 설정 */
  margin-right: 20px; /* 왼쪽과 오른쪽 콘텐츠 사이에 공간 추가 */
}

.right {
  flex: 1; /* 오른쪽 콘텐츠 영역이 가로 공간을 차지하도록 설정 */
  display: flex;
  flex-direction: column; /* 수직으로 정렬 */
}

.detail {
  margin-bottom: 20px;
}
</style>
