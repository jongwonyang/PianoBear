<template>
  <!-- <v-card class="edit-box">
    <v-card-title class="edit-title"> 프로필 편집 </v-card-title>
    <v-card-text class="edit-element">
      <div class="profile-section">
        <img :src="userInfo.profilePic" alt="내 이미지" />
        <v-btn class="img-edit-button" @click="openProfileModal"> 프로필 사진 변경 </v-btn>
      </div>
      <div class="edit-buttons">
        <v-btn class="edit-button" @click="openNameModal"> 이름 변경 </v-btn>
        <v-divider></v-divider>
        <v-btn class="edit-button" @click="openPasswordModal"> 비밀번호 변경 </v-btn>
      </div>
    </v-card-text>
    <v-card-actions>
      <v-spacer></v-spacer>
      <v-btn class="close-button" @click="props.closeDialog">닫기</v-btn>
    </v-card-actions>
  </v-card> -->

  <div class="text-center pa-4">
    <v-dialog v-model="isProfileModalOpen" max-width="400" persistent>
      <template v-slot:activator="{ props: activatorProps }">
        <v-btn v-bind="activatorProps"> 프로필 사진 변경 </v-btn>
      </template>

      <v-card title="프로필 사진 변경">
        <v-card-text>
          <p>변경할 사진을 선택하세요.</p>
          <!-- 파일 입력 필드 추가 -->
          <v-file-input
            label="프로필 사진 선택"
            v-model="selectedFile"
            accept="image/*"
            prepend-icon="mdi-camera"
          ></v-file-input>
        </v-card-text>

        <v-card-actions>
          <v-spacer></v-spacer>
          <!-- 저장 버튼 클릭 시 handleFileUpload 메서드 호출 -->
          <v-btn color="primary" @click="changeProfileImage"> 저장 </v-btn>
          <v-btn @click="isProfileModalOpen = false"> 닫기 </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>

  <div class="text-center pa-4">
    <div class="text-center pa-4">
      <v-dialog v-model="isNameModalOpen" max-width="400" persistent>
        <template v-slot:activator="{ props: activatorProps }">
          <v-btn v-bind="activatorProps"> 이름 변경 </v-btn>
        </template>

        <v-card title="이름 변경">
          <v-card-text>
            <p>변경할 이름을 입력하세요.</p>
            <v-text-field
              label="새로운 이름"
              v-model="newName"
              prepend-icon="mdi-account"
              required
            ></v-text-field>
          </v-card-text>

          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="primary" @click="changeName"> 저장 </v-btn>
            <v-btn @click="isNameModalOpen = false"> 닫기 </v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
    </div>
  </div>

  <div class="text-center pa-4">
    <v-dialog v-model="isPasswordModalOpen" max-width="400" persistent>
      <template v-slot:activator="{ props: activatorProps }">
        <v-btn v-bind="activatorProps"> 비밀번호 변경 </v-btn>
      </template>

      <v-card title="비밀번호 변경">
        <v-card-text>
          <p>변경할 비밀번호를 입력하세요.</p>

          <!-- 현재 비밀번호 입력 필드 -->
          <v-text-field
            label="현재 비밀번호"
            v-model="oldPassword"
            type="password"
            prepend-icon="mdi-lock"
            required
          ></v-text-field>

          <!-- 새 비밀번호 입력 필드 -->
          <v-text-field
            label="새 비밀번호"
            v-model="newPassword"
            type="password"
            prepend-icon="mdi-lock"
            required
          ></v-text-field>

          <!-- 새 비밀번호 확인 입력 필드 -->
          <v-text-field
            label="새 비밀번호 확인"
            v-model="confirmNewPassword"
            type="password"
            prepend-icon="mdi-lock-check"
            required
          ></v-text-field>
        </v-card-text>

        <v-card-actions>
          <v-spacer></v-spacer>
          <!-- 저장 버튼 클릭 시 changePassword 메서드 호출 -->
          <v-btn color="primary" @click="changePassword"> 저장 </v-btn>
          <v-btn @click="isPasswordModalOpen = false"> 닫기 </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup>
import { defineProps, onMounted, ref } from "vue";
import { useUserStore } from "@/stores/user";

const userStore = useUserStore();
const userInfo = ref({});

const isProfileModalOpen = ref(false);
const selectedFile = ref("");

const isNameModalOpen = ref(false);
const newName = ref("");

const isPasswordModalOpen = ref(false);
const oldPassword = ref("");
const newPassword = ref("");
const confirmNewPassword = ref("");

onMounted(() => {
  // 유저 정보 불러오기
  userInfo.value = userStore.user;
});

const props = defineProps({
  closeDialog: Function,
});

const changeProfileImage = () => {
  userStore.updateProfile(selectedFile.value);
};

const changeName = () => {
  userStore.updateName(newName.value);
};

const changePassword = () => {
  if (newPassword.value !== confirmNewPassword.value) {
    alert("새 비밀번호가 일치하지 않습니다.");
    return;
  }

  userStore.updatePassword(oldPassword.value, newPassword.value);
  isPasswordModalOpen.value = false;
};
</script>

<style scoped>
.edit-box {
  background: #fff9e0;
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
  background-color: #d9f6d9;
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
  background-color: #f5e5d1;
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
  background-color: #f5e5d1;
  color: #e31515;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-content {
  background: #fffff8;
  border-radius: 8px;
  width: 80%;
  width: 800px;
  height: 500px;
}

.content {
  padding: 100px;
  padding-top: 110px;
  padding-bottom: 150px;
  font-size: x-large;
  font-weight: bold;
}

.modal-actions {
  margin-top: 100px;
  display: flex;
  justify-content: center;
  gap: 50px;
}

.text {
  display: flex;
  justify-content: center;
}

.close {
  display: flex;
  justify-content: flex-end;
  padding: 20px;
}
</style>
