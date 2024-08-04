<template>
  <h2>{{ route.params.id }}번 악보 상세</h2>
  <div class="container">
    <div class="left">
      <PianoSheet />
    </div>
    <div class="right">
      <DetailPractice />
      <Buttons />
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from "vue-router";
import { ref } from "vue";
import PianoSheet from "@/components/PianoSheet/PianoSheet.vue";
import DetailPractice from "@/components/PianoSheet/DetailPractice.vue";
import Buttons from "@/components/PianoSheet/Buttons.vue";

const route = useRoute();
</script>

<style scoped>
.container {
  display: flex; /* Flexbox 레이아웃 사용 */
}

.left {
  flex: 1; /* 왼쪽 콘텐츠 영역이 가로 공간을 차지하도록 설정 */
  margin-right: 20px; /* 왼쪽과 오른쪽 콘텐츠 사이에 공간 추가 */
}

.right {
  flex: 1; /* 오른쪽 콘텐츠 영역이 가로 공간을 차지하도록 설정 */
  display: flex;
  flex-direction: column; /* 수직으로 정렬 */
}
</style>
