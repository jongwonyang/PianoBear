<template>
  <div>
    <div class="head"></div>
    <div class="btn">
      <button
        class="cursor-pointer duration-200 hover:scale-125 active:scale-100"
        title="Go Back"
        @click="router.push('/main/piano-sheet')"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          width="40px"
          height="40px"
          viewBox="0 0 24 24"
          class="goBack"
        >
          <path
            stroke-linejoin="round"
            stroke-linecap="round"
            stroke-width="1.5"
            d="M11 6L5 12M5 12L11 18M5 12H19"
          ></path>
        </svg>
      </button>
    </div>
    <div class="container">
      <div class="left">
        <div class="title">
          {{ store.detailSheet?.title }}
          <v-icon @click="openModal">mdi-pencil</v-icon>
        </div>
        <div class="make">
          <button @click="makeImg">표지 만들기</button>
        </div>
        <PianoSheet />
      </div>
      <div class="right">
        <div class="detail">
          <DetailPractice />
        </div>
        <Buttons />
      </div>
    </div>

    <!-- 로딩 중일 때 화면을 덮는 로더 -->
    <div v-if="loading" class="loader-overlay">
      <div class="loading-container">
        <div class="text">악보 표지 생성 중입니다! 잠시만 기다려주세요🤗</div>
        <div class="loader"></div>
      </div>
    </div>

    <!-- 모달이 필요할 때만 표시 -->
    <EditSheetModal v-if="store.isOpen" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import PianoSheet from "@/components/PianoSheet/PianoSheet.vue";
import DetailPractice from "@/components/PianoSheet/DetailPractice.vue";
import EditSheetModal from "@/components/PianoSheet/EditSheetModal.vue";
import Buttons from "@/components/PianoSheet/Buttons.vue";
import { usePianoSheetStore } from "@/stores/pianosheet";

const route = useRoute();
const router = useRouter();
const store = usePianoSheetStore();
const id = ref<number | null>(null);
const loading = ref(false);

const openModal = () => {
  store.isOpen = true;
};

const makeImg = async () => {
  if (id.value !== null) {
    loading.value = true;
    await store.makeImg(id.value);
    loading.value = false;
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
  display: flex;
}

.text {
  font-size: 200%;
  font-weight: bold;
  margin-right: 30px;
}

.left {
  flex: 1;
  margin-right: 20px;
  position: relative;
  top: -49px;
}

.right {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.detail {
  margin-bottom: 20px;
}

.title {
  background-color: #fffff8;
  color: #947650;
  height: 6vh;
  align-content: center;
  text-align: center;
  padding-left: 1vw;
  padding-right: 1vw;
  border: 2px solid #f5e5d1;
  border-radius: 5%;
  width: 200px;
  margin-bottom: 5px;
}

.make {
  background-color: #fffff8;
  color: #947650;
  height: 6vh;
  align-content: center;
  text-align: center;
  padding-left: 1vw;
  padding-right: 1vw;
  border: 2px solid #f5e5d1;
  border-radius: 5%;
  width: 150px;
  position: absolute;
  top: 0;
  right: 0;
}

.head {
  position: relative;
  justify-content: space-between;
  font-size: larger;
  font-weight: bold;
  margin-bottom: 1vh;
  padding-right: 51.5%;
  height: 100px;
}

.loader-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.8);
  /* 반투명 배경 */
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
  /* 다른 요소 위에 오도록 z-index 설정 */
}

.loader {
  width: 150px;
  height: 150px;
  position: relative;
  background: white;
  border-radius: 4px;
  overflow: hidden;
  border: 2px solid #d1e3f9;
}

.loader:before {
  content: "";
  position: absolute;
  left: 0;
  bottom: 0;
  width: 90px;
  height: 90px;
  transform: rotate(45deg) translate(30%, 40%);
  background: #5ba3f7;
  box-shadow: 32px -34px 0 5px #5ba3f7;
  animation: slide 2.5s infinite ease-in-out alternate;
}

.loader:after {
  content: "";
  position: absolute;
  left: 10px;
  top: 10px;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #5ba3f7;
  transform: rotate(0deg);
  transform-origin: 35px 145px;
  animation: rotate 2.5s infinite ease-in-out;
}

@keyframes slide {
  0%,
  100% {
    bottom: -35px;
  }

  25%,
  75% {
    bottom: -2px;
  }

  20%,
  80% {
    bottom: 2px;
  }
}

@keyframes rotate {
  0% {
    transform: rotate(-15deg);
  }

  25%,
  75% {
    transform: rotate(0deg);
  }

  100% {
    transform: rotate(25deg);
  }
}

.loading-container {
  display: flex;
  align-items: center;
  /* 세로축 가운데 정렬 */
  justify-content: center;
  /* 가로축 가운데 정렬 */
  height: 100vh;
  /* 전체 화면 높이 */
  text-align: center;
}

.goBack {
  stroke: black;
}

.btn {
  position: absolute;
  top: 10vh;
  left: 18vw;
}
</style>
