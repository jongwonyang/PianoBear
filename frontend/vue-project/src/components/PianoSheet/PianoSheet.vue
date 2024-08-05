<template>
  <div class="container">
    <h3>악보 컴포넌트</h3>
    <!-- <img v-if="store.detailSheet[0].musicImg" :src="store.detailSheet[0].musicImg" alt="악보 이미지" class="sheet-image" />
        <p v-else>악보 이미지가 없습니다.</p> -->
    <img src="" alt="">
  </div>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from "vue-router";
import { ref, onMounted } from "vue";
import { usePianoSheetStore } from "@/stores/pianosheet";

const store = usePianoSheetStore();
const route = useRoute();

const fetchdetailSheet = async (id: number) => {
  await store.detailSheetfun(id);
};

onMounted(() => {
  fetchdetailSheet(Number(route.params.id)); // 1대신 id 들어가야 함
});
</script>

<style scoped>
.container {
  width: 30vw;
  height: 70wh;
  border: 1px solid black;
}
</style>
