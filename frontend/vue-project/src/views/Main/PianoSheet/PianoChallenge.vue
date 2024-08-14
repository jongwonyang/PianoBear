<template>
  <div>
    <ResultModal v-if="store.isResultModalOpen" class="modal" />
    <PracticePiano v-if="!store.challengeLoading && !store.isResultModalOpen" challenge="true" />
    <div v-if="store.challengeLoading" class="loading">
      <img src="@/assets/characters/토니/토니악보변환.png" width="270px" />
      <h1>Now Loading...
        <v-progress-linear color="dark-blue" indeterminate></v-progress-linear>
      </h1>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import PracticePiano from '@/components/PianoSheet/PracticePiano.vue';
import { usePianoSheetStore } from "@/stores/pianosheet";
import ResultModal from "@/components/PianoSheet/ResultModal.vue";

const store = usePianoSheetStore();

onMounted(() => {
  store.isResultModalOpen = false;
});
</script>

<style scoped>
h1 {
  position: relative;
  left: 0;
  top: 3vh;
  width: 40vh;
  text-align: center;
}

.modal {
  margin: auto;
}

.loading {
  position: relative;
  display: flex;
  width: 150vh;
  height: 70vh;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}
</style>