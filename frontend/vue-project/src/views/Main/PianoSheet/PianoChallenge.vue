<template>
  <div>
    <button @click="openModal">모달 열기</button>
    <ResultModal v-if="store.isResultModalOpen" />
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
