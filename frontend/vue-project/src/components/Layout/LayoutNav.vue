<template>
  <v-card>
    <v-layout>
      <v-navigation-drawer expand-on-hover rail color="#D9F6D9">
        <v-list>
          <!-- 여기는 이미지 넣을 건데 이미지는 유저의 프로필 사진을 가져올거임 -->
          <v-list-item :prepend-avatar="userInfo.profileImage" :subtitle="userInfo.userEmail"
            :title="userInfo.userName"></v-list-item>
        </v-list>
        <v-divider></v-divider>
        <v-list density="compact" nav>
          <v-list-item prepend-icon="mdi-home-variant-outline" title="메인" value="내 정보"
            @click="router.push({ name: 'myInfo' })"></v-list-item>
          <v-list-item prepend-icon="mdi-account-multiple-outline" title="친구들" value="친구들"
            @click="router.push({ name: 'friends' })"></v-list-item>
          <v-list-item prepend-icon="mdi-music-box-multiple" title="내 악보" value="악보목록"
            @click="router.push({ name: 'pianoSheetList' })"></v-list-item>
          <v-list-item prepend-icon="mdi-video-account" title="놀이터" value="소통방"
            @click="router.push({ name: 'community' })"></v-list-item>
          <v-list-item prepend-icon="mdi-bell-outline" title="알림" value="알림" @click="showDialog = true"></v-list-item>
        </v-list>
      </v-navigation-drawer>
    </v-layout>

    <!-- 알림 다이얼로그 -->
    <v-dialog v-model="showDialog" max-width="500px">
      <v-card class="notification-box">
        <v-card-title class="headline">
          알림
          <v-spacer></v-spacer>
          <v-btn class="delete-all" icon @click="clearAllNotifications">
            <v-icon>mdi-delete-outline</v-icon>
          </v-btn>
        </v-card-title>
        <v-card-text>
          <v-list class="notification-list">
            <v-list-item v-for="(notification, index) in notifications" :key="index">
              <v-list-item-content>
                <v-list-item-title>{{ notification.title }}</v-list-item-title>
                <v-list-item-subtitle>{{ notification.message }}</v-list-item-subtitle>
              </v-list-item-content>
              <v-list-item-action class="notification-actions">
                <template v-if="notification.type === 'friendRequest'">
                  <v-btn class="yes-btn" small text @click="acceptFriendRequest(index)">수락</v-btn>
                  <v-btn class="no-btn" small text @click="declineFriendRequest(index)">거절</v-btn>
                </template>
                <template v-else-if="notification.type === 'meetingInvite'">
                  <v-btn class="yes-btn" small text @click="acceptMeetingInvite(index)">수락</v-btn>
                  <v-btn class="no-btn" small text @click="declineMeetingInvite(index)">거절</v-btn>
                </template>
                <template v-else-if="notification.type === 'chatMessage'">
                  <v-btn class="yes-btn" small text @click="goToChat(index)">이동</v-btn>
                  <v-btn class="no-btn" small text @click="deleteChatMessage(index)">삭제</v-btn>
                </template>
              </v-list-item-action>
            </v-list-item>
          </v-list>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn text @click="showDialog = false">닫기</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-card>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user';

const router = useRouter();
const userStore = useUserStore();

const userInfo = ref({
  userEmail: "",
  userName: "",
  profileImage: "",
});

const showDialog = ref(false);
const notifications = ref([
  { type: "friendRequest", title: "친구 추가 요청", message: "홍길동님이 친구 추가를 요청했습니다." },
  { type: "meetingInvite", title: "회의실 초대", message: "회의실에 초대되었습니다." },
  { type: "chatMessage", title: "채팅 알림", message: "새로운 채팅 메시지가 도착했습니다." },
]);

onMounted(() => {
  userStore.GetUserInfo()
    .then((res) => {
      userInfo.value.userEmail = res.data.email;
      userInfo.value.userName = res.data.name;
      userInfo.value.profileImage = "https://i.namu.wiki/i/W-XZ2Oy1qM9Sxd3wxVUN837dvaI_Ed3GCCIwyJ7is037aZgMPOFdUq3XtjI_EGJ7rsFMyNqpYFhHb58GNc2AnsIVg8uGcLc6RuwicIS6CNWCbwJ7niosjDS_zyEbgQzuTnubiPUnVy2ke4focfz5kg.webp"
    })
    .catch((err) => {
      console.log(err);
    });
});

const clearAllNotifications = () => {
  notifications.value = [];
};

const acceptFriendRequest = (index) => {
  console.log("친구 추가 요청 수락:", notifications.value[index].message);
  // notifications.value.splice(index, 1);
};

const declineFriendRequest = (index) => {
  console.log("친구 추가 요청 거절:", notifications.value[index].message);
  // notifications.value.splice(index, 1);
};

const acceptMeetingInvite = (index) => {
  console.log("회의실 초대 수락:", notifications.value[index].message);
  // notifications.value.splice(index, 1);
};

const declineMeetingInvite = (index) => {
  console.log("회의실 초대 거절:", notifications.value[index].message);
  // notifications.value.splice(index, 1);
};

const goToChat = (index) => {
  console.log("채팅으로 이동:", notifications.value[index].message);
  // notifications.value.splice(index, 1);
};

const deleteChatMessage = (index) => {
  console.log("채팅 메시지 삭제:", notifications.value[index].message);
  // notifications.value.splice(index, 1);
};
</script>

<style scoped>
.notification-box {
  margin-top: 20px;
  background: #FFF9E0;
  color: #947650;
}

.notification-list {
  background: #FFF9E0;
  color: #947650;
  max-height: 300px;
}

.notification-actions {
  justify-content: flex-end;
}

.delete-all {
  color: #FFF9E0;
  background: #947650;
}

.yes-btn {
  color: #FFF9E0;
  background: #947650;
}

.no-btn {
  color: #ffffff;
  background: #ff7957;
}
</style>
