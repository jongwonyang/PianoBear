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
        <button @click="share">
          <div class="svg-wrapper-1">
            <div class="svg-wrapper">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="24" height="24">
                <path fill="none" d="M0 0h24v24H0z"></path>
                <path
                  fill="currentColor"
                  d="M1.946 9.315c-.522-.174-.527-.455.01-.634l19.087-6.362c.529-.176.832.12.684.638l-5.454 19.086c-.15.529-.455.547-.679.045L12 14l6-8-8 6-8.054-2.685z"
                ></path>
              </svg>
            </div>
          </div>
          <span>공유하기</span>
        </button>
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
const htmlContent = ref<string | undefined>();
const result = computed(() => {
  return store.resultChallenge?.grade;
});
const closeModal = () => {
  store.isResultModalOpen = false;
};

const share = async () => {
  if (store.resultChallenge) {
    htmlContent.value = await store.shareChallengefun(store.resultChallenge.id);

    if (htmlContent.value) {
      // Blob 객체 생성
      // URL.createObjectURL로 Blob URL 생성
      window.open(htmlContent.value, `_blank`, "width=800,height=600");
    }
  }
};
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

button {
  font-family: inherit;
  font-size: 20px;
  background: royalblue;
  color: white;
  padding: 0.7em 1em;
  padding-left: 0.9em;
  display: flex;
  align-items: center;
  border: none;
  border-radius: 16px;
  overflow: hidden;
  transition: all 0.2s;
  cursor: pointer;
}

button span {
  display: block;
  margin-left: 0.3em;
  transition: all 0.3s ease-in-out;
}

button svg {
  display: block;
  transform-origin: center center;
  transition: transform 0.3s ease-in-out;
}

button:hover .svg-wrapper {
  animation: fly-1 0.6s ease-in-out infinite alternate;
}

button:hover svg {
  transform: translateX(1.2em) rotate(45deg) scale(1.1);
}

button:hover span {
  transform: translateX(5em);
}

button:active {
  transform: scale(0.95);
}

@keyframes fly-1 {
  from {
    transform: translateY(0.1em);
  }

  to {
    transform: translateY(-0.1em);
  }
}
</style>
