<template>
  <div>
    <div class="head">
      <div class="title">
        {{ store.detailSheet?.title }}
        <v-icon @click="openModal">mdi-pencil</v-icon>
      </div>
      <div class="make" v-if="!store.detailSheet?.musicImg">
        <button @click="makeImg">표지 만들기</button>
      </div>
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

  <!-- 모달이 필요할 때만 표시 -->
  <EditSheetModal v-if="store.isOpen" />
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import PianoSheet from "@/components/PianoSheet/PianoSheet.vue";
import DetailPractice from "@/components/PianoSheet/DetailPractice.vue";
import EditSheetModal from "@/components/PianoSheet/EditSheetModal.vue";
import Buttons from "@/components/PianoSheet/Buttons.vue";
import { usePianoSheetStore } from "@/stores/pianosheet";

const route = useRoute();
const store = usePianoSheetStore();
const id = ref<number | null>(null);

const openModal = () => {
  store.isOpen = true;
};

const makeImg = async () => {
  if (id.value !== null) {
    await store.makeImg(id.value);
  } else {
    console.error("ID is null, cannot make image.");
  }
};

// 악보 데이터를 가져오는 함수
const fetchDetailData = async (id: number) => {
  await store.detailSheetfun(id);
};

onMounted(() => {
  id.value = Number(route.params.id);
  fetchDetailData(Number(route.params.id));
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

.title {
  background-color: #fffff8;
  color: #947650;
  height: 6vh;
  align-content: center;
  padding-left: 1vw;
  padding-right: 1vw;
  border-radius: 5%;
}

.make {
  background-color: #fffff8;
  color: #947650;
  height: 6vh;
  align-content: center;
  margin-left: 1vw;
  padding-left: 1vw;
  padding-right: 1vw;
  border-radius: 5%;
}

.head {
  display: flex;
  justify-content: start;
  font-size: larger;
  font-weight: bold;
}
</style>
