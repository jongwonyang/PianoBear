<template>
  <div>
    <button @click="openModal">모달 열기</button>
    <div v-if="store.isResultModalOpen">
      <ResultModal class="modal" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { usePianoSheetStore } from "@/stores/pianosheet";
import ResultModal from "@/components/PianoSheet/ResultModal.vue";

const store = usePianoSheetStore();

const openModal = () => {
  store.isResultModalOpen = true;
  console.log(store.isResultModalOpen);
};
</script>

<style scoped>
.modal {
  margin: 13vh;
  margin-left: 34vw;
}
</style>
