<template>
    <div>
        <div class="my-profile-box">
            <md-elevation></md-elevation>
            <div class="profile-content">
                <div class="profile-image"></div>
                <div class="profile-info">
                    <!-- 편집 버튼 -->
                    <v-btn icon="mdi-pencil-outline" class="edit-btn" density="comfortable"></v-btn>
                    <!-- user name 가져오게 -->
                    <div class="profile-name">하정수 님 반갑습니다!</div>
                    <!-- user가 전날까지 연속 연습 날짜를 가져오기 -->
                    <div class="profile-day">OO일 째 꾸준히 연습하고 있어요!</div>
                    <div class="favorite-music">
                        <md-elevation></md-elevation>
                        <v-icon aria-hidden="false">
                            mdi-numeric-1-circle-outline
                        </v-icon>
                        <div class="music-name">
                            악보이름
                        </div>
                        <v-icon aria-hidden="false">
                            mdi-numeric-2-circle-outline
                        </v-icon>
                        <div class="music-name">
                            악보이름
                        </div>
                        <v-icon aria-hidden="false">
                            mdi-numeric-3-circle-outline
                        </v-icon>
                        <div class="music-name">
                            악보이름
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="practice-online">
            <div class="practice-box">
                <md-elevation></md-elevation>
                <!-- 오른쪽 버튼을 클릭했을 때 이번달이 마지막이면 "가장 최신 달이에요!" 라는 표시 뜨게
                     왼쪽 버튼을 클릭했을 때 이번달이 처음이면 "가장 오래된 달이에요!" 라는 표시 뜨게
                     한줄에 7개씩 연습 기록이 보이게(맨처음 왼쪽부터 1일부터 시작)
                        연습 기록은 클릭하면 연습 기록 상세 페이지로 이동
                -->
                <div class="practice-header">
                    <v-btn icon="mdi-menu-left-outline" class="pre-month-btn" density="compact"></v-btn> <!-- 이전 달로 이동하는 버튼 -->
                    <div>{{ currentMonth }}월달 나의 연습 기록</div> <!-- O는 월을 나타내는 변수 -->
                    <v-btn icon="mdi-menu-right-outline" class="next-month-btn" density="compact"></v-btn> <!-- 다음 달로 이동하는 버튼 -->
                </div>
                <div class="practice-calendar">
                    <!-- 연습 기록 버튼 -->
                    <template v-for="(day, index) in practiceDays" :key="index">
                            <button class="honey-button">
                                <img :src="day ? honeyFilled : honeyEmpty" alt="벌꿀">
                            </button>
                    </template>
                </div>
            </div>
            <div class="online-friends-box">
                <md-elevation></md-elevation>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue';
import honeyFilledImg from '@/assets/images/채워진 벌꿀.png';
import honeyEmptyImg from '@/assets/images/빈 벌꿀.png';

const currentMonth = ref(7);

const practiceDays = ref([
  false, true, false, true, true, false, false,
  true, true, false, true, false, true, true,
  false, false, true, true, false, true, false,
  true, false, true, true, false, true, false,
  true, true
]);

const honeyFilled = honeyFilledImg;
const honeyEmpty = honeyEmptyImg;
</script>

<style scoped>
.my-profile-box {
    background: #FFF9E0;
    position: relative;
    padding: 60px 60px;
    border-radius: 30px;
    text-align: center;
    max-width: 2000px;
    width: 100%;
    margin-bottom: 20px;
}

.profile-content {
    display: flex;
    align-items: center;
    justify-content: center;
}

.profile-image {
    width: 180px;
    height: 180px;
    background: url(@/assets/images/정수_어렸을적.png);
    background-size: cover;
    background-position: center;
    border-radius: 50%;
    margin-right: 50px;
    margin-left: 30px;
}

.edit-btn {
    position: absolute;
    top: 10px;
    right: 10px;
    color: #947650;
    background: #F5E5D1;
}

.profile-info {
    text-align: center;
    margin: 10px;
}

.practice-online {
    display: flex;
    justify-content: space-between;
    gap: 20px;
}

.practice-box,
.online-friends-box {
    background: #FFF9E0;
    position: relative;
    padding: 30px 30px;
    border-radius: 30px;
    text-align: center;
    color: #947650;
    max-width: 400px;
    width: 100%;
}

.practice-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    position: absolute;
    top: 10px;
    left: 20px;
    right: 20px;
}

.practice-calendar {
    display: flex;
    flex-wrap: wrap;
    justify-content: left;
    gap: 10px;
    margin-top: 20px;
}

.honey-button {
    background: none;
    border: none;
    padding: 0;
    cursor: pointer;
    outline: none;
}

.honey-button img {
    width: 28px;
    height: 30px;
}

.profile-name {
    font-size: 22px;
    font-weight: 500;
    color: #947650;
}

.profile-day {
    font-size: 16px;
    color: #947650;
}

.favorite-music {
    background: #F5E5D1;
    display: flex;
    position: relative;
    padding: 20px 10px;
    border-radius: 10px;
    text-align: center;
    max-width: 2000px;
    width: 100%;
    margin-top: 30px;
    --md-sys-color-shadow: #947650;
}

.music-name {
    font-size: 16px;
    margin-right: 10px;
}

.pre-month-btn,
.next-month-btn {
    color: #947650;
    background: #F5E5D1;
}
</style>
