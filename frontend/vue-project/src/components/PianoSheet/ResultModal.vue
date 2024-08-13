<template>
  <div class="modal">
    <div class="container">
      <div class="text">도전 결과</div>
      <div class="bear">
        <span>
          <img src="@/assets/characters/토니/토니응원.png" alt="" width="100" />
        </span>
        <span>
          <img src="@/assets/characters/피치/피치기쁨.png" alt="" width="100" />
        </span>
      </div>
      <div class="score">
        <div class="result">{{ result }} 점</div>
      </div>
      <div class="buttons">
        <v-btn @click="share" variant="tonal">공유하기</v-btn>
        <v-btn @click="closeModal" variant="tonal">닫기</v-btn>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from "vue";
import { usePianoSheetStore } from "@/stores/pianosheet";
import { useRoute, useRouter } from "vue-router";

const store = usePianoSheetStore();
const route = useRoute();
const result = computed(() => {
  console.log(store.resultChallenge.grade)
  return store.resultChallenge?.grade;
});
const closeModal = () => {
  store.isResultModalOpen = false;
};

const share = () => {

}


</script>

<style scoped>
.modal {
  background-color: #fff9e0;
  box-shadow: 0.1vw 0.4vh 0.8vh gray;
  width: 32vw;
  height: 75vh;
  padding: 3.6vw;
  display: flex;
  justify-content: center;
  position: fixed;
  /* Make modal stay in place */
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  align-items: center;
}

.bear {
  display: flex;
  justify-content: center;
}

.score {
  background-color: #947650;
  height: 30vh;
  width: 25vw;
  margin-top: -6.5vh;
  display: grid;
  justify-content: center;
  padding: 7vh;
  text-align: center;
  border-radius: 10px;
  box-shadow: 0.1vw 0.4vh 0.8vh gray;
}

.buttons {
  display: flex;
  justify-content: flex-end;
  gap: 3%;
  margin-top: 3%;
}

.text {
  display: flex;
  justify-content: center;
  font-size: 2.5vw;
  font-weight: bold;
  margin-bottom: 2%;
  color: #947650;
}

.result {
  font-size: 5vw;
  font-weight: bold;
  color: rgb(243, 244, 207);
}
</style>
