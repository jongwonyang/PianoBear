<template>
    <div>
        <div class="my-profile-box">
            <md-elevation></md-elevation>
            <div class="profile-content">
                <div class="profile-image"></div>
                <div class="profile-info">
                    <!-- 편집 버튼 -->
                    <v-dialog v-model="isDialogOpen" max-width="500">
                        <template v-slot:activator="{ props: activatorProps }">
                            <v-btn v-bind="activatorProps" icon="mdi-pencil-outline" class="edit-btn" density="comfortable"></v-btn>
                        </template>
                        <template v-slot:default="{ isActive }">
                            <ProfileEdit v-if="isActive" :closeDialog="closeDialog" />
                        </template>
                    </v-dialog>
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
                <div class="practice-header">
                    <v-btn icon="mdi-menu-left-outline" class="pre-month-btn" density="compact"></v-btn>
                    <div>{{ currentMonth }}월달 나의 연습 기록</div>
                    <v-btn icon="mdi-menu-right-outline" class="next-month-btn" density="compact"></v-btn>
                </div>
                <div class="practice-calendar">
                    <template v-for="(day, index) in practiceDays" :key="index">
                        <button class="honey-button">
                            <img :src="day ? honeyFilled : honeyEmpty" alt="벌꿀">
                            <v-tooltip activator="parent" location="bottom">{{ currentMonth }}월 {{ index + 1 }}일 연습기록</v-tooltip>
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
import { useRouter } from 'vue-router';
import honeyFilledImg from '@/assets/images/채워진 벌꿀.png';
import honeyEmptyImg from '@/assets/images/빈 벌꿀.png';
import ProfileEdit from '@/components/MyInfo/ProfileEdit.vue';

const router = useRouter();

const isDialogOpen = ref(false);
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

const closeDialog = () => {
    isDialogOpen.value = false;
};
</script>

<style scoped>
@keyframes fadeIn {
    from {
        opacity: 0;
    }
    to {
        opacity: 1;
    }
}

.my-profile-box {
    background: #FFF9E0;
    position: relative;
    padding: 60px 60px;
    border-radius: 30px;
    text-align: center;
    max-width: 2000px;
    width: 100%;
    margin-bottom: 20px;
    animation: fadeIn 2s ease-in-out;
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
    animation: fadeIn 2s ease-in-out;
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
