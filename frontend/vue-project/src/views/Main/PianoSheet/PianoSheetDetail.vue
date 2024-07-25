<template>
    <v-container>
      <h2>{{ route.params.id }}번 악보 상세</h2>
      
      <v-row class="main-container">
        <v-col cols="12" md="6">
          <PianoSheet />
        </v-col>
        <v-col cols="12" md="6">
          <DetailPractice />
          <v-btn variant="outlined" @click="navigateTo('practice')">연습하기</v-btn>
          <v-btn variant="outlined" @click="navigateTo('challenge')">도전하기</v-btn>
          <v-btn variant="outlined" @click="handleFavorite">즐겨찾기</v-btn>
          <v-btn variant="outlined" @click="openModal">삭제</v-btn>
        </v-col>
      </v-row>
    </v-container>

    <!--삭제 모달-->
    <div class="text-center pa-4">
        <v-dialog v-model="isModalOpen" max-width="400" persistent>
            <v-card prepend-icon="mdi-map-marker" text="정말 삭제하시겠습니까?" title="ㅇㅇㅇ 삭제">
                <template v-slot:actions>
                <v-spacer></v-spacer>

                <v-btn @click="closeModal">취소</v-btn>
                <v-btn @click="handleDelete">삭제</v-btn>
                </template>
            </v-card>
        </v-dialog>
    </div>


  </template>
  
<script setup>
import { useRoute, useRouter } from 'vue-router';
import { ref } from 'vue';
import PianoSheet from "@/components/PianoSheet/PianoSheet.vue";
import DetailPractice from "@/components/PianoSheet/DetailPractice.vue";
import { usePianoSheetStore } from "@/stores/pianosheet";

const route = useRoute();
const router = useRouter();
const store = usePianoSheetStore();
const isModalOpen = ref(false);

const handleFavorite = () => {
    store.handleFavorite();
};

const openModal = () => {
    isModalOpen.value = true;
}

const closeModal = () => {
    isModalOpen.value = false;
}

const handleDelete = () => {
    store.handleDelete();
    closeModal();
};

const navigateTo = (path) => {
    router.push(`/main/piano-sheet/${route.params.id}/${path}`);
};
</script>

<style scoped>

</style>