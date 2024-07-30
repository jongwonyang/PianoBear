<template>
    <v-card class="edit-box">
        <v-card-title class="edit-title">
            프로필 편집
        </v-card-title>
        <v-card-text class="edit-element">
            <div class="profile-section">
                <div class="profile-image"></div>
                <md-elevated-button class="img-edit-button">
                    프로필 사진 변경
                </md-elevated-button>
            </div>
            <div class="edit-buttons">
                <md-elevated-button class="edit-button">
                    이메일 변경
                </md-elevated-button>
                <md-elevated-button class="edit-button">
                    이름 변경
                </md-elevated-button>
                <md-elevated-button class="edit-button">
                    비밀번호 변경
                </md-elevated-button>
            </div>
        </v-card-text>
        <v-card-actions>
            <v-spacer></v-spacer>
            <md-elevated-button class="close-button" @click="closeDialog">닫기</md-elevated-button>
        </v-card-actions>
    </v-card>
</template>

<script setup>
import { defineProps } from 'vue';

const props = defineProps({
    closeDialog: Function
});
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

.img-edit-button {
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

.img-edit-button:hover {
    background-color: #8dfa92;
}

.edit-buttons {
    display: flex;
    flex-direction: column;
    align-items: flex-end;
    gap: 10px;
}

.edit-button {
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

.edit-button:hover {
    background-color: #ffcb77;
}

.close-button {
    padding: 10px;
    background-color: #F5E5D1;
    color: #e31515;
    border: none;
    border-radius: 4px;
    font-size: 16px;
    cursor: pointer;
    transition: background-color 0.3s;
}
</style>
