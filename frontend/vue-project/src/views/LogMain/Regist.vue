<template>
    <div class="regist-container">
        <div class="regist-box">
            <md-elevation></md-elevation>
            <div class="back-btn">
                <v-icon @click="router.push({ name: 'login' })">mdi-arrow-left</v-icon>
            </div>
            <div class="regist-text">회원가입</div>
            <form action=""></form>
            <div class="input-box">
                <md-outlined-text-field label="아이디" type="text" placeholder="영문+숫자 4~20자" class="regist-input id"
                    :value="userId" @input="setUserId" @change="checkDuplicateId" :error="idError"
                    :error-text="idErrorMessage"></md-outlined-text-field>
                <md-outlined-text-field label="이메일" type="email" placeholder="abc@example.com"
                    class="regist-input email" :value="userEmail" @input="setUserEmail" @change="checkDuplicateEmail"
                    :error="emailError" :error-text="emailErrorMessage"></md-outlined-text-field>
                <md-outlined-text-field label="이름" type="text" class="regist-input name" :value="userName"
                    @input="setUserName"></md-outlined-text-field>
                <md-outlined-text-field label="비밀번호" type="password" placeholder="영문+숫자 8~20자" class="regist-input pw"
                    :value="userPassword" @input="setUserPassword"></md-outlined-text-field>
                <md-outlined-text-field label="비밀번호 확인" type="password" class="regist-input pwCheck"
                    :value="PasswordCheck" @input="checkPassword" :error="passwordError"
                    :error-text="passwordErrorMessage"></md-outlined-text-field>
                <md-outlined-select label="성별" class="select-box" :value="userGender" @input="setUserGender">
                    <md-select-option value="M">
                        <div slot="headline">남</div>
                    </md-select-option>
                    <md-select-option value="F">
                        <div slot="headline">여</div>
                    </md-select-option>
                </md-outlined-select>
                <div class="birth-box">
                    <md-outlined-text-field label="생년월일" placeholder="YYYY-MM-DD ex) 1999-01-01"
                        class="regist-input birth" :value="userBirth" @input="setUserBirth" :error="birthError"
                        :error-text="birthErrorMessage"></md-outlined-text-field>
                </div>
            </div>
            <v-btn class="regist-button" @click="RegistUser" :loading="isLoading" :disabled="isLoading">
                회원가입
            </v-btn>
        </div>
    </div>
</template>

<script setup>
import { useUserStore } from '@/stores/user';
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

const userStore = useUserStore();
const userId = ref('');
const userEmail = ref('');
const userPassword = ref('');
const userName = ref('');
const userGender = ref('');
const userBirth = ref('');

const idError = ref(false);
const idErrorMessage = ref('');
const emailError = ref(false);
const emailErrorMessage = ref('');
const passwordError = ref(false);
const passwordErrorMessage = ref('');
const PasswordCheck = ref('');
const birthError = ref(false);
const birthErrorMessage = ref('');

const isLoading = ref(false); // 로딩 상태 변수

const setUserId = (e) => {
    userId.value = e.target.value;
    // 유효성 검사: 영문+숫자 4~20자
    const idPattern = /^[a-zA-Z0-9]{4,20}$/;
    if (!idPattern.test(userId.value)) {
        idError.value = true;
        idErrorMessage.value = '아이디는 영문+숫자 4~20자여야 합니다.';
    } else {
        idError.value = false;
        idErrorMessage.value = '';
    }
};

const checkDuplicateId = (e) => {
    setUserId(e);
    if (!idError.value) {
        userStore.CheckUserId(userId.value).then((res) => {
            if (res.data.exists === true) {
                idError.value = true;
                idErrorMessage.value = '중복된 아이디입니다.';
            } else {
                idError.value = false;
                idErrorMessage.value = '';
            }
        });
    }
};

const setUserEmail = (e) => {
    userEmail.value = e.target.value;
    // 유효성 검사: 이메일 형식
    const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    if (!emailPattern.test(userEmail.value)) {
        emailError.value = true;
        emailErrorMessage.value = '유효한 이메일 주소를 입력하세요.';
    } else {
        emailError.value = false;
        emailErrorMessage.value = '';
    }
};

const checkDuplicateEmail = (e) => {
    setUserEmail(e);
    if (!emailError.value) {
        userStore.CheckUserEmail(userEmail.value).then((res) => {
            if (res.data.exists === true) {
                emailError.value = true;
                emailErrorMessage.value = '중복된 이메일입니다.';
            } else {
                emailError.value = false;
                emailErrorMessage.value = '';
            }
        });
    }
};

const setUserPassword = (e) => {
    userPassword.value = e.target.value;
    // 유효성 검사: 영문+숫자 8~20자
    const passwordPattern = /^(?=.*[a-zA-Z])(?=.*\d).{8,20}$/;
    if (!passwordPattern.test(userPassword.value)) {
        passwordError.value = true;
        passwordErrorMessage.value = '비밀번호는 영문+숫자 8~20자여야 합니다.';
    } else {
        passwordError.value = false;
        passwordErrorMessage.value = '';
    }
    userStore.user.password = userPassword.value;
};

const setUserName = (e) => {
    userName.value = e.target.value;
    userStore.user.name = userName.value;
};

const setUserGender = (e) => {
    userGender.value = e.target.value;
    userStore.user.gender = userGender.value;
};

const setUserBirth = (e) => {
    userBirth.value = e.target.value;
    // 유효성 검사: YYYY-MM-DD 형식
    const birthPattern = /^\d{4}-\d{2}-\d{2}$/;
    if (!birthPattern.test(userBirth.value)) {
        birthError.value = true;
        birthErrorMessage.value = '생년월일은 YYYY-MM-DD 형식으로 입력하세요.';
    } else {
        birthError.value = false;
        birthErrorMessage.value = '';
    }
    userStore.user.birthday = userBirth.value;
};

const checkPassword = (e) => {
    PasswordCheck.value = e.target.value;

    if (userPassword.value !== PasswordCheck.value) {
        passwordError.value = true;
        passwordErrorMessage.value = '비밀번호가 일치하지 않습니다.';
    } else {
        passwordError.value = false;
        passwordErrorMessage.value = '';
    }
};

const RegistUser = async () => {
    userStore.user.id = userId.value;
    userStore.user.email = userEmail.value;

    if (idError.value === true || emailError.value === true || passwordError.value === true) {
        alert('입력값을 확인해주세요.');
        return;
    }
    if (userId.value === '' || userEmail.value === '' || userPassword.value === '' || userName.value === '' || userGender.value === '' || userBirth.value === '') {
        alert('입력값을 확인해주세요.');
        return;
    }

    isLoading.value = true; // 로딩 시작
    try {
        await userStore.RegistUser();
    } catch (error) {
        console.error(error);
        alert('회원가입에 실패했습니다.');
    } finally {
        isLoading.value = false; // 로딩 종료
    }
};
</script>


<style scoped>
.regist-container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100%;
    width: 100%;
}

.regist-box {
    background: #FFF9E0;
    position: relative;
    padding: 30px 20px;
    border-radius: 30px;
    text-align: center;
    max-width: 600px;
    width: 100%;
}

.regist-text {
    padding: 10px;
    font-size: 24px;
    font-weight: 500;
    margin-bottom: 20px;
    color: #947650;
}

.regist-input {
    width: calc(100% - 40px);
    margin: 10px 0;
    color: #F5E5D1;
    background-color: #FBFCFE;
    border-radius: 10px;
    font-size: 16px;
    --md-outlined-text-field-input-text-placeholder-color: #a5a5a5;
}

.regist-button {
    width: calc(100% - 40px);
    margin: 10px 0;
    color: #947650;
    background-color: #D9F6D9;
    border-radius: 10px;
    font-size: 16px;
}

.select-box {
    width: calc(100% - 40px);
    margin: 10px 0;
    color: #F5E5D1;
    background-color: #FBFCFE;
    border-radius: 10px;
    font-size: 16px;
}

.back-btn {
    position: absolute;
    top: 20px;
    left: 20px;
    cursor: pointer;
    font-size: 24px;
}
</style>
