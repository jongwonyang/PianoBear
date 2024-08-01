<template>
    <div class="login-container">
        <div class="login-box">
            <md-elevation></md-elevation>
            <div class="login-text">로그인</div>
            <md-outlined-text-field label="아이디" type="text" class="login-input id" :value="userId"
                @input="setUserId"></md-outlined-text-field>
            <md-outlined-text-field label="비밀번호" type="password" class="login-input" :value="userPassword"
                @input="setUserPassword"></md-outlined-text-field>
            <md-elevated-button class="login-button" @click="Login()">로그인</md-elevated-button>
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
import { useUserStore} from '@/stores/user';

const router = useRouter();

function kakaoLogin() {
    // Implement kakaoLogin functionality here
}

function googleLogin() {
    // Implement googleLogin functionality here
}

const userStore = useUserStore();

const userId = ref('');
const userPassword = ref('');

const setUserId = (e) => {
    userId.value = e.target.value;
};

const setUserPassword = (e) => {
    userPassword.value = e.target.value;
};

async function Login() {
    try {
        const res = await userStore.LoginUser(userId.value, userPassword.value);
        if (res.status === 200) {
            console.log("로그인에 성공했습니다.");
            
            userStore.isLoggedIn = true;
            localStorage.setItem("accessToken", res.data.accessToken);
            sessionStorage.setItem("refreshToken", res.data.refreshToken);
            userStore.SetAccessToken(res.data.accessToken);
            userStore.SetRefreshToken(res.data.refreshToken);
            userStore.GetUserInfo();
            router.push("/main");
            console.log(userStore.isLoggedIn);
        } else if (res.status === 403) {
            console.log("이메일인증이 되지 않은 사용자입니다.");
            alert("메일인증이 되지 않은 사용자입니다.");
        } else {
            console.log("로그인에 실패했습니다.");
            alert("로그인에 실패했습니다.");
        }
    } catch (error) {
        console.log("아이디 또는 비밀번호가 일치하지 않습니다.");
        alert("아이디 또는 비밀번호가 일치하지 않습니다.");
    }
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