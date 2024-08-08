<template>
    <div class="friend-container">
        <div class="left-panel">
            <div class="my-status">
                <md-elevation></md-elevation>
                <div v-if="isLoading.userInfo" class="loading-bar">
                    <v-progress-linear indeterminate color="#C69C67"></v-progress-linear>
                </div>
                <div v-else>
                    <div class="my-status-box">
                        <img class="my-status-image" :src="userInfo.profilePic">
                        <div class="my-status-ele">
                            <div class="my-name">{{ userInfo.name }}</div>
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
                    <v-btn append-icon="mdi-account-search" size="small" class="add-friend-btn"
                        @click="showDialog = true">친구 검색</v-btn>
                </div>
                <v-divider></v-divider>
                <div v-if="isLoading.friendList" class="loading-bar">
                    <v-progress-linear indeterminate color="#C69C67"></v-progress-linear>
                </div>
                <div v-else class="my-friends-ele">
                    <div class="friend-item" v-for="friend in friends" :key="friend.id"
                        @click="viewFriendInfo(friend.id)">
                        <div class="my-friends-ele-left">
                            <img :src="friend.profilePic" alt="친구 img">
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
        </div>
        <div class="chat-box">
            <md-elevation></md-elevation>
            <div class="my-chatting-header">
                <div class="my-chatting-text">채팅</div>
            </div>
            <v-divider></v-divider>
        </div>

        <!-- 친구 정보 다이얼로그 -->
        <v-dialog v-model="friendInfoDialog" max-width="500px">
            <v-card class="friend-info-form">
                <v-card-title class="headline">친구 정보</v-card-title>
                <v-card-text v-if="friendInfo">
                    <div class="friend-item">
                        <div class="my-friends-ele-left">
                            <img :src="friendInfo.profilePic" alt="친구 img">
                        </div>
                        <div class="my-friends-ele-right">
                            <div class="friend-name">{{ friendInfo.name }}</div>
                            <div class="friend-status-message">
                                <md-elevation></md-elevation>
                                {{ friendInfo.statusMessage }}
                            </div>
                        </div>
                    </div>
                </v-card-text>
                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn text @click="removeFriend(friendInfo.id)" color="red">삭제</v-btn>
                    <v-btn text @click="friendInfoDialog = false">닫기</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>

        <!-- 친구 검색 다이얼로그 -->
        <v-dialog v-model="showDialog" max-width="500px">
            <v-card class="add-friend-form">
                <v-card-title class="headline">친구 검색</v-card-title>
                <v-card-text>친구의 이름이나 아이디를 입력하세요!</v-card-text>
                <v-text-field label="친구 아이디" v-model="searchQuery" @keyup.enter="searchFriend"></v-text-field>
                <v-card-text v-if="searchResult">
                    <div class="friend-item">
                        <div class="my-friends-ele-left">
                            <img :src="searchResult.profilePic" alt="친구 img">
                        </div>
                        <div class="my-friends-ele-right">
                            <div class="friend-name">{{ searchResult.name }}</div>
                            <div class="friend-status-message">
                                <md-elevation></md-elevation>
                                {{ searchResult.statusMessage }}
                            </div>
                            <v-btn v-if="!isFriend(searchResult.id) && searchResult.id != userInfo.id"
                                class="add-friend-btn" @click="addFriend(searchResult.id)">추가</v-btn>
                            <v-btn v-else-if="searchResult.id == userInfo.id" disabled>자신은 추가할 수 없습니다</v-btn>
                            <v-btn v-else class="add-friend-btn" disabled>이미 친구입니다</v-btn>
                        </div>
                    </div>
                </v-card-text>
                <v-btn v-else-if="!searchResult" disabled>찾을 수 없습니다</v-btn>
                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn text @click="showDialog = false">취소</v-btn>
                    <v-btn text @click="searchFriend">검색</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
    </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { useUserStore } from '@/stores/user';
import { useFriendStore } from '@/stores/friend';

const userInfo = ref({

});

const isLoading = ref({
    userInfo: true,
    friendList: true,
    chattingList: true,
});

const friends = ref([]);

const userStore = useUserStore();
const friendStore = useFriendStore();
const showDialog = ref(false); // 친구 검색 다이얼로그 상태 추가
const friendInfoDialog = ref(false); // 친구 정보 다이얼로그 상태 추가
const searchQuery = ref(''); // 검색어를 저장할 상태
const searchResult = ref(null); // 검색 결과를 저장할 상태
const friendInfo = ref(null); // 친구 정보를 저장할 상태

onMounted(() => {

    // 친구 목록을 불러오는 로직을 여기에 구현합니다.
    friendStore.GetFriendList()
        .then((res) => {
            friends.value = res.data;
            isLoading.value.friendList = false;
            // 유저정보가 친구목록이 로딩되면 가져오도록 함.
            userInfo.value = userStore.user;
            isLoading.value.userInfo = false;
            console.log(res);
            console.log(res.data.length);
        })
        .catch((err) => {
            console.log(err);
        });
});

const searchFriend = () => {
    friendStore.GetFriendInfo(searchQuery.value)
        .then((res) => {
            searchResult.value = res.data;
            console.log(res);
        })
        .catch((err) => {
            searchResult.value = null;
            console.log(err);
        });
};

const addFriend = (id) => {
    // 친구 추가 로직을 여기에 구현합니다.
    friendStore.SendFriendRequest(id)
        .then((res) => {
            console.log(res);
            console.log(`친구 ID: ${id}를 친구요청하였습니다.`);
            showDialog.value = false; // 다이얼로그를 닫습니다.
        })
        .catch((err) => {
            console.log(err);
        });
};

const isFriend = (id) => {
    return friends.value.some(friend => friend.id === id);
};

const viewFriendInfo = (id) => {
    friendStore.GetFriendInfo(id)
        .then((res) => {
            friendInfo.value = res.data;
            friendInfoDialog.value = true; // 친구 정보 다이얼로그를 엽니다.
            console.log(res);
        })
        .catch((err) => {
            console.log(err);
        });
};

const removeFriend = (id) => {
    // 친구 삭제 로직을 여기에 구현합니다.
    friendStore.RemoveFriend(id)
        .then((res) => {
            console.log(res);
            console.log(`친구 ID: ${id}를 삭제하였습니다.`);
            // 친구 목록 업데이트
            friends.value = friends.value.filter(friend => friend.id !== id);
            friendInfoDialog.value = false; // 다이얼로그를 닫습니다.
        })
        .catch((err) => {
            console.log(err);
        });
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

.friend-container {
    display: flex;
    height: 100vh;
    overflow: hidden;
    animation: fadeIn 0.5s ease-in-out;
}

.left-panel {
    display: flex;
    flex-direction: column;
    padding: 20px;
    box-sizing: border-box;
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
    justify-content: center;
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
    margin-right: auto;
}

.add-friend-btn {
    margin-left: auto;
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
    display: -webkit-box;
    -webkit-line-clamp: 2;
    /* 원하는 줄 수 */
    -webkit-box-orient: vertical;
    overflow: hidden;
    text-overflow: ellipsis;
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
    width: 200px;
    /* 원하는 너비로 설정 */
    margin: 10px;
    --md-sys-color-shadow: #947650;
    height: 80px;
    /* 원하는 높이로 설정 */
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
    opacity: 0.6;
    -webkit-transition: .2s ease-in-out;
    transition: .2s ease-in-out;
}

.friend-item:hover {
    opacity: 1;
}

.add-friend-btn {
    color: #947650;
    background-color: #F5E5D1;
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
    width: 330px;
    height: 60px;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    /* 원하는 줄 수 */
    -webkit-box-orient: vertical;
    overflow: hidden;
    text-overflow: ellipsis;
}

.add-friend-form {
    background: #FFF9E0;
    color: #947650;
}

.friend-status-message {
    margin-bottom: 5px;
}

.friend-info-form {
    background: #FFF9E0;
    color: #947650;
}

.chat-box {
    flex: 1;
    width: 500px;
    background: #FFF9E0;
    position: relative;
    padding: 30px;
    border-radius: 30px;
    text-align: center;
    overflow-y: auto;
    scrollbar-width: none;
    --md-sys-color-shadow: #947650;
}

.my-chatting-header {
    justify-content: center;
    align-items: center;
    position: sticky;
    top: 0;
    background: #FFF9E0;
    z-index: 10;
    padding: 10px 0;
}

.my-chatting-text {
    font-size: 20px;
    font-weight: 700;
    color: #947650;
}
</style>
