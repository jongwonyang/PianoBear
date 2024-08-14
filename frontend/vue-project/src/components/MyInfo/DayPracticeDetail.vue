<template>
  <v-card class="practice-box">
    <v-card-title class="practice-title">
      {{ year }}년 {{ month }}월 {{ day }}일 연습 기록
    </v-card-title>
    <v-card-text class="practice-element">
      <v-list v-if="musics.length > 0" class="practice-item">
        <v-list-item v-for="(music, index) in musics" :key="index" class="music-item">
          <v-list-item-content>
            <v-list-item-title class="practice-music">{{ music.musicTitle }}</v-list-item-title>
          </v-list-item-content>
          <v-list-item-action>
            <div class="praise-sticker">
              <div class="sticker-box" v-for="i in 5" :key="i">
                <img
                  class="sticker-image"
                  :src="i <= music.practiceCount ? berryFilled : berryEmpty"
                  :alt="i <= music.practiceCount ? '채워진 딸기' : '빈 딸기'"
                />
              </div>
            </div>
          </v-list-item-action>
        </v-list-item>
      </v-list>
      <div v-else class="no-record">연습한 기록이 없어요 ㅜㅅㅜ</div>
    </v-card-text>
    <v-card-actions>
      <v-spacer></v-spacer>
      <v-btn class="close-button" @click="props.closeDialog(props.index)">닫기</v-btn>
    </v-card-actions>
  </v-card>
</template>

<script setup>
import { defineProps, ref } from "vue";
import { onMounted } from "vue";
import { useDashboardStore } from "@/stores/dashboard";
import berryFilledImg from "@/assets/images/가득찬딸기.png";
import berryEmptyImg from "@/assets/images/빈딸기.png";

const props = defineProps({
  closeDialog: Function,
  year: Number,
  month: Number,
  day: Number,
  practiceCount: Number,
  index: Number,
});

const berryFilled = berryFilledImg;
const berryEmpty = berryEmptyImg;

const dashboardStore = useDashboardStore();
const musics = ref([]);

onMounted(() => {
  dashboardStore
    .GetMonthlyPracticeRecord(props.year, props.month, props.day)
    .then((response) => {
      console.log(response.data);
      musics.value = response.data;
    })
    .catch((error) => {
      console.error(error);
    });
});
</script>

<style scoped>
.practice-box {
  padding: 20px;
  margin: auto;
  height: 50vh;
  scrollbar-width: none;
}

.practice-title {
  font-size: 20px;
  font-weight: bold;
  color: black;
}

.practice-element {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.practice-item {
  /* max-height: 300px; */
  background: #fff9e0;
}

.music-item {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 5px;
}

.practice-music {
  font-size: 16px;
  margin-bottom: 5px;
}

.praise-sticker {
  display: flex;
}

.sticker-box {
  width: 50px;
  height: 50px;
  margin-left: 10px;
}

.sticker-image {
  width: 100%;
  height: 100%;
}

.no-record {
  font-size: 16px;
  color: #946450;
  text-align: center;
}

.v-btn.close-button {
  padding: 0 15px;
  background-color: #f5e5d1;
  color: #946450;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
}
</style>
