<template>
    <div>
        <div class="my-profile-box">
            <md-elevation></md-elevation>
            <div class="profile-content">
                <div class="profile-image"></div>
                <div class="profile-info">
                    <!-- 편집 버튼 -->
                    <v-dialog v-model="profileDialogOpen" max-width="500">
                        <template v-slot:activator="{ props: activatorProps }">
                            <v-btn v-bind="activatorProps" icon="mdi-pencil-outline" class="edit-btn"
                                density="comfortable"></v-btn>
                        </template>
                        <template v-slot:default="{ isActive }">
                            <ProfileEdit v-if="isActive" :closeDialog="closeProfileDialog" />
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
            <div class="logout-btn">
                <md-elevated-button @click="LogOut">
                    로그아웃
                </md-elevated-button>
            </div>
        </div>
        <div class="practice-online">
            <div class="practice-box">
                <md-elevation></md-elevation>
                <div class="practice-header">
                    <v-btn icon="mdi-menu-left-outline" class="pre-month-btn" density="compact"></v-btn>
                    <div>{{ currentMonth }}월 나의 연습 기록</div>
                    <v-btn icon="mdi-menu-right-outline" class="next-month-btn" density="compact"></v-btn>
                </div>
                <div class="practice-calendar">
                    <template v-for="(day, index) in practiceDays" :key="index">
                        <v-dialog v-model="dialogState[index]" max-width="500">
                            <template v-slot:activator="{ props: activatorProps }">
                                <button class="honey-button" v-bind="activatorProps">
                                    <img :src="day ? honeyFilled : honeyEmpty" alt="벌꿀">
                                    <v-tooltip activator="parent" location="bottom">{{ currentMonth }}월 {{ index + 1 }}일
                                        연습기록</v-tooltip>
                                </button>
                            </template>
                            <template v-slot:default="{ isActive }">
<<<<<<< HEAD
                                <DayPracticeDetail v-if="isActive" :month="currentMonth" :day="index + 1"
                                    @close="closeDialog(index)" />
=======
                                <DayPracticeDetail v-if="isActive" :month="currentMonth" :day="index + 1" :closeDialog="closeDialog" :index="index" />
>>>>>>> master
                            </template>
                        </v-dialog>
                    </template>
                </div>
                <v-divider style="margin-bottom: 15px;"></v-divider>
                <div class="practice-day">연습일수 나오게</div>
            </div>
            <div class="online-friends-box">
                <md-elevation></md-elevation>
                <!-- 온라인 친구 최대 3명 나타내기
                 v-for 사용해서 현재 로그인 된 친구 3명 나타내기(가장 최근 대화한 순으로) -->
                <div class="online-friend-title">현재 온라인 친구들</div>
                <div class="online-friend">
                    <div class="friend-box">
                        <div class="friend-image"></div>
                        <div class="friend-name">친구 이름</div>
                        <div class="friend-chat">
                            <v-icon aria-hidden="false">
                                mdi-chat
                            </v-icon>
                            <v-tooltip activator="parent" location="bottom">채팅하기</v-tooltip>
                        </div>
                    </div>
                    <v-divider></v-divider>
                    <div class="friend-box">
                        <div class="friend-image"></div>
                        <div class="friend-name">친구 이름</div>
                        <div class="friend-chat">
                            <v-icon aria-hidden="false">
                                mdi-chat
                            </v-icon>
                        </div>
                    </div>
                    <v-divider></v-divider>
                    <div class="friend-box">
                        <div class="friend-image"></div>
                        <div class="friend-name">친구 이름</div>
                        <div class="friend-chat">
                            <v-icon aria-hidden="false">
                                mdi-chat
                            </v-icon>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user';
import { useTokenStore } from '@/stores/token';
import honeyFilledImg from '@/assets/images/채워진 벌꿀.png';
import honeyEmptyImg from '@/assets/images/빈 벌꿀.png';
import ProfileEdit from '@/components/MyInfo/ProfileEdit.vue';
import DayPracticeDetail from '@/components/MyInfo/DayPracticeDetail.vue';

const router = useRouter();
const userStore = useUserStore();
const tokenStore = useTokenStore();

const profileDialogOpen = ref(false);
const currentMonth = ref(7);
const practiceDays = ref([
    false, true, false, true, true, false, false,
    true, true, false, true, false, true, true,
    false, false, true, true, false, true, false,
    true, false, true, true, false, true, false,
    true, true
]);

const dialogState = ref(practiceDays.value.map(() => false));

const honeyFilled = honeyFilledImg;
const honeyEmpty = honeyEmptyImg;

const closeProfileDialog = () => {
    profileDialogOpen.value = false;
};

const closeDialog = (index) => {
    dialogState.value[index] = false;
};

<<<<<<< HEAD
async function LogOut() {
    try {
        const accessToken = tokenStore.GetAccessToken();
        const refreshToken = tokenStore.GetRefreshToken();
        await userStore.LogoutUser(accessToken, refreshToken);
        tokenStore.RemoveToken();
        router.push("/login");
    } catch (error) {
        console.error(error);
    }
}
=======
/////////////// 웹소켓 테스트 ////////////////////
import { useWebSocketStore } from "@/stores/useWebSocketStore";
const webSocketStore = useWebSocketStore();
webSocketStore.connectWebSocket();
/////////////////////////////////////////////////
>>>>>>> master
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
    animation: fadeIn 1s ease-in-out;
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

.logout-btn {
    position: absolute;
    bottom: 10px;
    right: 10px;
}

.practice-online {
    display: flex;
    justify-content: space-between;
    gap: 20px;
    animation: fadeIn 1s ease-in-out;
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
    width: 32px;
    height: 35px;
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

.friend-box {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 12px;
    margin-bottom: 12px;
}

.friend-image {
    width: 70px;
    height: 70px;
    background: url(@/assets/images/정수_어렸을적.png);
    background-size: cover;
    background-position: center;
    border-radius: 50%;
}

.friend-name {
    font-size: 18px;
    font-weight: 500;
    color: #947650;
}

.friend-chat {
    color: #947650;
    background: #F5E5D1;
    padding: 10px;
    border-radius: 50%;
    cursor: pointer;
}

.online-friend-title {
    font-size: 20px;
    font-weight: 500;
    color: #947650;
}
</style>
