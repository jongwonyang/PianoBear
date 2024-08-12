<template>
  <div class="reset-container">
    <div class="reset-box">
      <md-elevation></md-elevation>
      <div class="back-btn">
        <v-icon @click="router.push({ name: 'login' })">mdi-arrow-left</v-icon>
      </div>
      <div class="reset-text">비밀번호 재설정</div>
      <div class="input-box">
        <md-outlined-text-field
          label="아이디"
          type="text"
          class="reset-input id"
          :value="userId"
          @input="setUserId"
        ></md-outlined-text-field>
        <md-outlined-text-field
          label="이름"
          type="text"
          class="reset-input name"
          :value="userName"
          @input="setUserName"
        ></md-outlined-text-field>
        <md-outlined-text-field
          label="이메일"
          type="email"
          class="reset-input email"
          :value="userEmail"
          @input="setUserEmail"
        ></md-outlined-text-field>
      </div>
      <v-btn class="reset-button" @click="passwordReset" :loading="isLoading" :disabled="isLoading"
        >임시 비밀번호 발급</v-btn
      >
    </div>
  </div>
</template>
<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/user";

const router = useRouter();
const userStore = useUserStore();

const userId = ref("");
const userName = ref("");
const userEmail = ref("");
const isLoading = ref(false);

const setUserId = (e) => {
  userId.value = e.target.value;
};

const setUserName = (e) => {
  userName.value = e.target.value;
};

const setUserEmail = (e) => {
  userEmail.value = e.target.value;
};

const passwordReset = async () => {
  isLoading.value = true;
  try {
    await userStore.PasswordReset(userId.value, userName.value, userEmail.value);
    alert("임시 비밀번호가 발급되었습니다.");
    router.push({ name: "login" });
  } catch (error) {
    alert("비밀번호 재설정에 실패했습니다.");
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
.reset-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  width: 100%;
}

.reset-box {
  background: #fff9e0;
  position: relative;
  padding: 60px 40px;
  border-radius: 30px;
  text-align: center;
  max-width: 600px;
  width: 100%;
}

.reset-text {
  padding: 10px;
  font-size: 24px;
  font-weight: 500;
  margin-bottom: 20px;
  color: #947650;
}

.reset-input {
  width: calc(100% - 40px);
  margin: 10px 0;
  color: #f5e5d1;
  background-color: #fbfcfe;
  border-radius: 10px;
  font-size: 16px;
}

.reset-button {
  margin-top: 20px;
  background-color: #d9f6d9;
  color: #947650;
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
