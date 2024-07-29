<template>
    <v-card class="practice-box">
        <v-card-title class="practice-title">
           OO님의 {{month}}월 {{day}}일 연습 기록
        </v-card-title>
        <!-- 악보 이름과 최대 5개의 칭찬스티커 사용(
         예를 들어 이 날에 이 곡 연습을 3번 했다면 3번 찍어주고
        8번 했어도 5개만 찍히게)
            flex하게 양 옆에 일정한 간격으로 띄워주기
            v-for 사용해서 이 날에 연습한 기록 스크롤로
            이동할 수 있게 하기 -->
            <v-card-text class="practice-element">
                <div class="practice-music">악보 이름</div>
                <div class="praise-sticker">
                    <div class="sticker-box" v-for="index in 5" :key="index">
                        <img class="sticker-image" :src="index <= practiceCount ? filledHoney : emptyHoney" :alt="index <= practiceCount ? '채워진 벌꿀' : '빈 벌꿀'">
                    </div>
                </div>
            </v-card-text>
            <v-card-actions>
                <v-spacer></v-spacer>
                <md-elevated-button class="close-button" @click="closeDialog">닫기</md-elevated-button>
            </v-card-actions>
    </v-card>
</template>

<script setup>
import { defineProps, ref } from 'vue';
import honeyFilledImg from '@/assets/images/채워진 벌꿀.png';
import honeyEmptyImg from '@/assets/images/빈 벌꿀.png';

const props = defineProps({
    closeDialog: Function,
    month: Number,
    day: Number,
    practiceCount: Number
});

const isDialogOpen = ref(true);
const filledHoney = honeyFilledImg;
const emptyHoney = honeyEmptyImg;
</script>

<style scoped>
.practice-box {
    width: 400px;
    height: 300px;
    padding: 20px;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
}

.practice-title {
    font-size: 20px;
    font-weight: bold;
    margin-bottom: 20px;
}

.practice-element {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.practice-music {
    font-size: 16px;
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

</style>
