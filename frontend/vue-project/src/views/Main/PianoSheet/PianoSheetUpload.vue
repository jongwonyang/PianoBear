<template>
  <div class="card">
    <v-tabs-window v-model="tab">
      <v-tabs-window-item :value="1">
        <div>
          <div class="text">
            <div style="border: 2px dashed #947650">PDF로 악보 만들기</div>
          </div>
          <div class="_container">
            <div class="component">
              <PDFtoMXL />
            </div>
            <div class="trans">
              <ConvertPDF />
            </div>
            <div class="component">
              <ConvertedFile />
            </div>
          </div>
        </div>
      </v-tabs-window-item>

      <v-tabs-window-item :value="2">
        <AITranscriber />
        <!--
        <div>
          <div class="text">
            <div class="text">AI로 악보 만들기</div>
          </div>
          <div class="container">
            <div class="component">
              <AItoMXL />
            </div>
            <div class="trans">
              <ConvertAI />
            </div>
            <div class="component">
              <ConvertedFile />
            </div>
          </div>
        </div>
        -->
      </v-tabs-window-item>
    </v-tabs-window>

    <div class="tab">
      <v-tabs v-model="tab" align-tabs="center" color="#D2B48C" hide-slider height="40px">
        <v-tab :value="1" @click="setCurrentTab('UserSheet')">
          <div class="convert">PDF 올리기</div>
        </v-tab>
        <v-tab :value="2" @click="setCurrentTab('BasicSheet')">
          <div class="convert">음원 올리기</div>
        </v-tab>
      </v-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from "vue";
import { usePianoSheetStore } from "@/stores/pianosheet";
import AItoMXL from "@/components/PianoSheet/AItoMXL.vue";
import PDFtoMXL from "@/components/PianoSheet/PDFtoMXL.vue";
import ConvertedFile from "@/components/PianoSheet/ConvertedFile.vue";
import ConvertAI from "@/components/PianoSheet/ConvertAI.vue";
import ConvertPDF from "@/components/PianoSheet/ConvertPDF.vue";
import AITranscriber from "@/components/PianoSheet/AITranscriber.vue";

const tab = ref(1);
const currentTab = ref("UserSheet");
const store = usePianoSheetStore();

const hasShownModal = ref(false); // 모달이 이미 열렸는지 추적

const setCurrentTab = (tabName) => {
  currentTab.value = tabName;
};

// 변환된 파일이 업데이트되면 최초 한번은 자동으로 모달을 열도록 감시
watch(
  () => store.convertedFile,
  (newFile) => {
    console.log(store.convertedFile);
    if (newFile && !hasShownModal.value) {
      hasShownModal.value = true; // 모달이 열렸음을 기록 -> 이후에 모달을 열기 위해서는 연필 아이콘 클릭
    }
  }
);
</script>

<style scoped>
._container {
  display: flex; /* Flexbox 활성화 */
  justify-content: space-between; /* 컴포넌트 사이의 간격을 자동으로 조절 */
  align-items: flex-start; /* 세로 방향으로 상단 정렬 */
}

.component {
  margin-left: 50px;
  margin-right: 50px;
}

.text {
  text-align: center;
  background-color: #fffff8;
  color: #947650;
  font-size: 30px;
  font-weight: bold;
  margin-bottom: 20px;
  margin-left: 50px;
  margin-right: 50px;
  padding: 5px;
}

.tab div {
  margin-top: 10px;
  font-size: 20px;
  font-weight: bold;
  /* background-color: #fffff8; */
  /* width: 1000px; */
}

.trans {
  margin-top: 230px;
}

.convert {
  /* margin-bottom: 40px; */
  background-color: #fffff8;
  width: 15vw;
  /* border: 2px double #947650; */
}
</style>
