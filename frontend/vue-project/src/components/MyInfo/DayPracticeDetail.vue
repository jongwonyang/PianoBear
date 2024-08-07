<template>
    <v-card class="practice-box">
        <v-card-title class="practice-title">
            {{ month }}월 {{ day }}일 연습 기록
        </v-card-title>
        <v-card-text class="practice-element">
            <div class="practice-music">악보 이름</div>
            <div class="praise-sticker">
                <div class="sticker-box" v-for="index in 5" :key="index">
                    <img class="sticker-image" :src="index <= practiceCount ? filledHoney : emptyHoney"
                        :alt="index <= practiceCount ? '채워진 벌꿀' : '빈 벌꿀'">
                </div>
            </div>
        </v-card-text>
        <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn class="close-button" @click="props.closeDialog(props.index)">닫기</v-btn>
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
    practiceCount: Number,
    index: Number
});

const filledHoney = honeyFilledImg;
const emptyHoney = honeyEmptyImg;
</script>


<style scoped>
.practice-box {
    width: 500px;
    height: 500px;
    padding: 20px;
    margin: auto;
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

.v-btn.close-button {
    padding: 0 15px;
    background-color: #F5E5D1;
    color: #946450;
    border: none;
    border-radius: 4px;
    font-size: 16px;
    cursor: pointer;
    transition: background-color 0.3s;
}
</style>
