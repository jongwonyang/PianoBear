<template>
    <div class="login-container">
        <div class="login-box">
            <md-elevation></md-elevation>
            <div class="login-text">로그인</div>
            <md-outlined-text-field label="아이디" type="text" class="login-input id"></md-outlined-text-field>
            <md-outlined-text-field label="비밀번호" type="password" class="login-input"></md-outlined-text-field>
            <md-elevated-button class="login-button">로그인</md-elevated-button>
            <div class="regist-pwReset-box">
                <md-elevated-button class="secondary-button" @click="router.push({ name: 'regist' })">
                    회원가입
                </md-elevated-button>
                <md-elevated-button class="secondary-button" @click="router.push({ name: 'pwReset' })">
                    비밀번호 재설정
                </md-elevated-button>
            </div>
            <div class="social-login-buttons">
                <a class="social-button" @click="kakaoLogin()">
                    <img src="@/assets/images/카카오로그인 이미지.png" alt="카카오로그인 버튼">
                </a>
                <a class="social-button" @click="googleLogin()">
                    <img src="@/assets/images/구글로그인 이미지.png" alt="구글로그인 버튼">
                </a>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

function kakaoLogin() {
    // Implement kakaoLogin functionality here
}

function googleLogin() {
    // Implement googleLogin functionality here
}
</script>

<style scoped>

.login-container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100%;
    width: 100%;
}

.login-box {
    background: #FFF9E0;
    position: relative;
    padding: 60px 40px;
    border-radius: 30px;
    text-align: center;
    max-width: 600px;
    width: 100%;
}

.login-text {
    padding: 10px;
    color: #947650;
    font-size: 24px;
    font-weight: 500;
}

.login-input {
    width: calc(100% - 40px);
    margin: 10px 0;
    color: #F5E5D1;
    background-color: #FBFCFE;
    border-radius: 10px;
    font-size: 16px;
}

.id {
    margin-top: 50px;
}

.regist-pwReset-box {
    display: flex;
    justify-content: space-between;
    margin: 20px 0;
}

.secondary-button {
    flex: 1;
    padding: 10px;
    margin: 0 20px;
    background-color: #F5E5D1;
    color: #947650;
    border: none;
    border-radius: 4px;
    font-size: 16px;
    cursor: pointer;
    transition: background-color 0.3s;
    text-align: center;
}

.login-button {
    width: 90%;
    padding: 10px;
    margin-top: 20px;
    background-color: #D9F6D9;
    color: #947650;
    border: none;
    border-radius: 4px;
    font-size: 16px;
    cursor: pointer;
    transition: background-color 0.3s;
}

.login-button:hover {
    background-color: #8dfa92;
}

.secondary-button a {
    text-decoration: none;
    color: #947650;
    display: block;
    width: 100%;
}

.social-login-buttons {
    display: flex;
    justify-content: center;
    margin-top: 20px;
}

.social-button {
    display: inline-block;
    margin: 0 10px;
    cursor: pointer;
}

.social-button img {
    border-radius: 4px;
    transition: transform 0.3s;
}
</style>