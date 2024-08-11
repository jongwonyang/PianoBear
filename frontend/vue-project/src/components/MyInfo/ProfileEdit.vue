<template>
    <v-card class="edit-box">
        <v-card-title class="edit-title">
            프로필 편집
        </v-card-title>
        <v-card-text class="edit-element">
            <div class="profile-section">
                <img :src="userInfo.profilePic" alt="내 이미지">
                <v-btn class="img-edit-button" @click="changeProfileImage">
                    프로필 사진 변경
                </v-btn>
            </div>
            <div class="edit-buttons">
                <v-btn class="edit-button" @click="changeName">
                    이름 변경
                </v-btn>
                <v-divider></v-divider>
                <v-btn class="edit-button" @click="changePassword">
                    비밀번호 변경
                </v-btn>
            </div>
        </v-card-text>
        <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn class="close-button" @click="props.closeDialog">닫기</v-btn>
        </v-card-actions>
    </v-card>
</template>


<script setup>
import { defineProps, onMounted, ref } from 'vue';
import { useUserStore } from '@/stores/user';

const userStore = useUserStore();
const userInfo = ref({});

onMounted(() => {
    // 유저 정보 불러오기
    userInfo.value = userStore.user;
});

const props = defineProps({
    closeDialog: Function
});

const changeProfileImage = () => {
    // 프로필 사진 변경 로직
};

const changeEmail = () => {
    // 이메일 변경 로직
};

const changeName = () => {
    // 이름 변경 로직
};

const changePassword = () => {
    // 비밀번호 변경 로직
};
</script>


<style scoped>
.edit-box {
    background: #FFF9E0;
    position: relative;
    padding: 60px 40px;
    border-radius: 30px;
    text-align: center;
    max-width: 600px;
    width: 100%;
}

.edit-title {
    padding: 10px;
    color: #947650;
    font-size: 24px;
    font-weight: 500;
}

.edit-element {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.profile-section {
    display: flex;
    flex-direction: column;
    align-items: center;
}

.profile-image {
    width: 180px;
    height: 180px;
    background: url(@/assets/images/정수_어렸을적.png);
    background-size: cover;
    background-position: center;
    border-radius: 50%;
    margin-bottom: 20px;
}

.v-btn.img-edit-button {
    width: 100%;
    padding: 10px;
    background-color: #D9F6D9;
    color: #947650;
    border: none;
    border-radius: 4px;
    font-size: 16px;
    cursor: pointer;
    transition: background-color 0.3s;
}

.v-btn.img-edit-button:hover {
    background-color: #8dfa92;
}

.edit-buttons {
    display: flex;
    flex-direction: column;
    align-items: flex-end;
    gap: 10px;
}

.v-btn.edit-button {
    width: 100%;
    padding: 10px;
    background-color: #F5E5D1;
    color: #946450;
    border: none;
    border-radius: 4px;
    font-size: 16px;
    cursor: pointer;
    transition: background-color 0.3s;
}

.v-btn.edit-button:hover {
    background-color: #ffcb77;
}

.v-btn.close-button {
    background-color: #F5E5D1;
    color: #e31515;
}
</style>
