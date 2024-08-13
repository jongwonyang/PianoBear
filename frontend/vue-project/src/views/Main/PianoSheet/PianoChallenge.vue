<template>
  <div>
    <ResultModal v-if="store.isResultModalOpen" class="modal" />
    <PracticePiano v-else challenge="true" />
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
h2 {
  position: absolute;
  top: 3vh;
  left: 30vh;
  width: 25vh;

}

.modal {
  margin: auto;
}
</style>