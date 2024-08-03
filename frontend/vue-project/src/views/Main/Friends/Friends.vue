<template>
    <div class="friend-container">
        <div class="my-status">
            <md-elevation></md-elevation>
            <div v-if="isLoading.userInfo" class="loading-bar">
                <v-progress-linear indeterminate color="#C69C67"></v-progress-linear>
            </div>
            <div v-else>
                <div class="my-status-box">
                    <img class="my-status-image" :src="userInfo.profileImage">
                    <div class="my-status-ele">
                        <div class="my-name">{{ userInfo.userName }}</div>
                        <div class="my-status-message-box">
                            <md-elevation></md-elevation>
                            <div class="my-status-message">
                                {{ userInfo.statusMessage }}
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="friend-box">
            <md-elevation></md-elevation>
            <div class="my-friends-header">
                <div class="my-friends-text">친구들</div>
                <v-btn append-icon="mdi-plus" size="small" class="add-friend-btn">친구추가</v-btn>
            </div>
            <div v-if="isLoading.friendList" class="loading-bar">
                <v-progress-linear indeterminate color="#C69C67"></v-progress-linear>
            </div>
            <div v-else class="my-friends-ele">
                <div class="friend-item" v-for="friend in friends" :key="friend.id">
                    <div class="my-friends-ele-left">
                        <img :src="friend.profileImage" alt="친구 img">
                    </div>
                    <div class="my-friends-ele-right">
                        <div class="friend-name">{{ friend.name }}</div>
                        <div class="friend-status-message">
                            <md-elevation></md-elevation>
                            {{ friend.statusMessage }}
                        </div>
                    </div>
                </div>
                <v-divider></v-divider>
            </div>
        </div>
        <div class="chat-box">
            <md-elevation></md-elevation>
        </div>
    </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { useUserStore } from '@/stores/user';

const userInfo = ref({
    userName: "string",
    profileImage: "string",
    statusMessage: "string",
});

const isLoading = ref({
    userInfo: true,
    friendList: true,
    chattingList: true,
});

const friends = ref([
    { id: 1, name: '친구 1', profileImage: 'https://via.placeholder.com/100', statusMessage: '상태메시지 1' },
    { id: 2, name: '친구 2', profileImage: 'https://via.placeholder.com/100', statusMessage: '상태메시지 2' },
    { id: 3, name: '친구 3', profileImage: 'https://via.placeholder.com/100', statusMessage: '상태메시지 3' },
    { id: 4, name: '친구 4', profileImage: 'https://via.placeholder.com/100', statusMessage: '상태메시지 4' },
    { id: 5, name: '친구 5', profileImage: 'https://via.placeholder.com/100', statusMessage: '상태메시지 5' },
    { id: 6, name: '친구 6', profileImage: 'https://via.placeholder.com/100', statusMessage: '상태메시지 6' },
    { id: 7, name: '친구 7', profileImage: 'https://via.placeholder.com/100', statusMessage: '상태메시지 7' },
    // 더 많은 친구들...
]);

const userStore = useUserStore();

onMounted(() => {
    userStore.GetUserInfo()
        .then((res) => {
            userInfo.value.userName = res.data.name;
            userInfo.value.profileImage = "https://i.namu.wiki/i/SddraDi09VYq0oCCOUERyLBj_WRMc9SXVXYW7ctya2JYNZtI0x1tnfejQtL9SNfhQyr_QqvqWn45PkRIupeTp5RxZ-He16vBNYb7kDwnRXU2Q-71QqDRpWYQrZuvhhe0D2jIonoYtqK4q4pr6wWv2g.webp";
            userInfo.value.statusMessage = res.data.statusMessage;
            isLoading.value.userInfo = false;
            isLoading.value.friendList = false; // 친구 목록 로딩 상태 초기화
            console.log(res);
        })
        .catch((err) => {
            console.log(err);
        });
});
</script>

<style scoped>
.friend-container {
    display: flex;
    flex-direction: column;
    height: 100vh;
    overflow: hidden;
}

.my-status {
    flex: 0 0 20%;
    background: #FFF9E0;
    position: relative;
    padding: 30px;
    border-radius: 30px;
    text-align: center;
    margin-bottom: 20px;
}

.friend-box {
    flex: 1;
    background: #FFF9E0;
    position: relative;
    padding: 30px;
    border-radius: 30px;
    text-align: center;
    overflow-y: auto;
    scrollbar-width: none;
    --md-sys-color-shadow: #947650;
}

.my-friends-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    position: sticky;
    top: 0;
    background: #FFF9E0;
    z-index: 10;
    padding: 10px 0;
}

.my-friends-text {
    font-size: 20px;
    font-weight: 700;
    color: #947650;
}

.add-friend-btn {
    margin-right: 20px;
}

.my-status-text {
    font-size: 20px;
    font-weight: 700;
    color: #947650;
    margin-bottom: 20px;
}

.my-status-box {
    display: flex;
    justify-content: center;
    align-items: center;
}

.my-name {
    font-size: 20px;
    font-weight: 700;
    color: #947650;
    margin-bottom: 20px;
}

.my-status-message {
    font-size: 15px;
    font-weight: 500;
    color: #947650;
}

.my-status-ele {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
}

.my-status-message-box {
    background: #F5E5D1;
    display: flex;
    position: relative;
    padding: 20px 10px;
    border-radius: 10px;
    text-align: center;
    max-width: 2000px;
    width: 100%;
    margin: 10px;
    --md-sys-color-shadow: #947650;
}

.my-status-image {
    width: 130px;
    height: 130px;
    background-size: cover;
    background-position: center;
    border-radius: 50%;
    margin-right: 50px;
    margin-left: 30px;
}

.my-friends-ele {
    display: flex;
    flex-direction: column;
    max-height: calc(100% - 40px);
    overflow-y: auto;
    scrollbar-width: none;
}

.friend-item {
    display: flex;
    padding: 10px 0;
    border-bottom: 0.5px solid #947650;
}

.my-friends-ele-left {
    flex: 0 0 auto;
    margin-right: 20px;
}

.my-friends-ele-left img {
    width: 70px;
    height: 70px;
    border-radius: 50%;
    background-size: cover;
    background-position: center;
}

.my-friends-ele-right {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: center;
}

.friend-name {
    font-size: 18px;
    font-weight: 700;
    color: #947650;
    margin-bottom: 10px;
}

.friend-status-message {
    background: #F5E5D1;
    padding: 10px;
    border-radius: 10px;
    font-size: 15px;
    font-weight: 500;
    color: #947650;
}
</style>
