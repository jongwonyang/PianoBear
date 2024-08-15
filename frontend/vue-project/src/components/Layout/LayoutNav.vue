<template>
  <v-card>
    <v-layout>
      <v-navigation-drawer
        expand-on-hover
        rail
        color="#D9F6D9"
        @mouseenter="isRail = false"
        @mouseleave="isRail = true"
        :mobile="false"
      >
        <v-list>
          <v-list-item>
            <img
              :src="isRail ? smallLogo : largeLogo"
              :max-width="isRail ? 60 : 120"
              class="mx-auto"
            />
          </v-list-item>
        </v-list>
        <v-divider></v-divider>
        <v-list density="comfortable" nav>
          <v-list-item
            prepend-icon="mdi-home-variant-outline"
            title="메인"
            value="내 정보"
            link
            :to="{ name: 'myInfoView' }"
          ></v-list-item>
          <v-list-item
            prepend-icon="mdi-account-multiple-outline"
            title="친구들"
            value="친구들"
            link
            :to="{ name: 'friends' }"
          ></v-list-item>
          <v-list-item
            prepend-icon="mdi-music-box-multiple"
            title="내 악보"
            value="악보목록"
            link
            :to="{ name: 'pianoSheetList' }"
          ></v-list-item>
          <v-list-item
            prepend-icon="mdi-video-account"
            title="놀이터"
            value="소통방"
            link
            :to="{ name: 'community' }"
          ></v-list-item>
          <v-list-item
            :prepend-icon="
              webSocketStore.notificationCount == 0 ? 'mdi-bell-outline' : 'mdi-bell-badge-outline'
            "
            title="알림"
            value="알림"
            :active="false"
            @click="handleNotificationClick"
          >
            <v-list-item-subtitle v-if="webSocketStore.notificationCount != 0"
              >{{ webSocketStore.notificationCount }} 개</v-list-item-subtitle
            >
          </v-list-item>
        </v-list>
      </v-navigation-drawer>
    </v-layout>

    <!-- 알림 다이얼로그 -->
    <v-dialog v-model="showDialog" max-width="500px">
      <v-card class="notification-box">
        <v-card-title class="headline">
          <div>알림</div>
          <v-btn class="delete-all" icon @click="showConfirmDeleteDialog = true">
            <v-icon>mdi-delete-outline</v-icon>
          </v-btn>
        </v-card-title>
        <v-divider></v-divider>
        <v-card-text>
          <v-list class="notification-list">
            <v-list-item v-for="(notification, index) in webSocketStore.notifications" :key="index">
              <v-list-item-content>
                <template v-if="notification.type === 'FRIEND_REQUEST'">
                  <v-list-item-title>친구 요청</v-list-item-title>
                  <v-list-item-subtitle
                    >{{ notification.content.senderName }} 님이 친구 요청을
                    보냈습니다!</v-list-item-subtitle
                  >
                </template>
                <template v-else-if="notification.type === 'INVITATION'">
                  <v-list-item-title>놀이터 초대</v-list-item-title>
                  <v-list-item-subtitle class="mb-2"
                    >{{ notification.content.inviterName }} 님이
                    {{ notification.content.sessionTitle }}&nbsp;놀이터에 초대하였습니다!
                  </v-list-item-subtitle>
                </template>
                <template v-else-if="notification.type === 'CHAT'">
                  <v-list-item-title>채팅</v-list-item-title>
                  <v-list-item-subtitle
                    >{{ notification.content.senderName }} 님이 채팅을
                    보냈습니다!</v-list-item-subtitle
                  >
                </template>
              </v-list-item-content>
              <v-list-item-action class="notification-actions">
                <template v-if="notification.type === 'FRIEND_REQUEST'">
                  <v-btn class="yes-btn" small text @click="acceptFriendRequest(index)">수락</v-btn>
                  <v-btn class="no-btn" small text @click="declineFriendRequest(index)">거절</v-btn>
                </template>
                <template v-else-if="notification.type === 'INVITATION'">
                  <v-btn class="yes-btn" small text @click="acceptMeetingInvite(index)">수락</v-btn>
                  <v-btn class="no-btn" small text @click="declineMeetingInvite(index)">거절</v-btn>
                </template>
                <template v-else-if="notification.type === 'CHAT'">
                  <v-btn class="yes-btn" small text @click="goToChat(index)">이동</v-btn>
                  <v-btn class="no-btn" small text @click="deleteChatMessage(index)">삭제</v-btn>
                </template>
                <template v-else-if="notification.type === 'sheetTranslation'">
                  <v-btn class="yes-btn" small text @click="goToSheet(index), (showDialog = false)"
                    >이동</v-btn
                  >
                </template>
              </v-list-item-action>
            </v-list-item>
            <v-list-item v-if="webSocketStore.notifications.length === 0">
              <v-list-item-content>
                <v-list-item-title>알림이 비어있습니다!</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
          </v-list>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn text @click="showDialog = false">닫기</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- 전체 삭제 확인 다이얼로그 -->
    <v-dialog v-model="showConfirmDeleteDialog" max-width="400px">
      <v-card class="delete-box">
        <v-card-title class="headline">알림 전체 삭제</v-card-title>
        <v-card-text>알림을 전체 삭제하시겠습니까?</v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn text @click="confirmDeleteAllNotifications" style="color: red">삭제</v-btn>
          <v-btn text @click="showConfirmDeleteDialog = false">닫기</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-card>
</template>

<script setup>
import { onMounted, ref, watch } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/user";
// import { useNotificationStore } from '@/stores/notification';
import { useFriendStore } from "@/stores/friend";
import { useWebSocketStore } from "@/stores/websocket";

const router = useRouter();
const userStore = useUserStore();
// const notificationStore = useNotificationStore();
const friendStore = useFriendStore();
const webSocketStore = useWebSocketStore();

const userInfo = ref({
  userEmail: "",
  userName: "",
  profileImage: "",
});

const showDialog = ref(false);
const showConfirmDeleteDialog = ref(false);

const isRail = ref(true);
import largeLogo from "@/assets/logo.png";
import smallLogo from "@/assets/characters/thf.png";

const handleNotificationClick = () => {
  webSocketStore.GetNotificationList();
  showDialog.value = true;
};

onMounted(() => {
  userStore
    .GetUserInfo()
    .then((res) => {
      userInfo.value.userEmail = res.email;
      userInfo.value.userName = res.name;
      userInfo.value.profileImage =
        "https://file2.mk.co.kr/meet/neds/2024/06/image_readtop_2024_417649_17176680616002440.jpg";
    })
    .catch((err) => {
      console.log(err);
    });
  webSocketStore.GetNotificationCount();
});

const clearAllNotifications = () => {
  webSocketStore.ClearNotifications();
  // notifications.value = [];
  // notificationCount.value = 0;
};

const confirmDeleteAllNotifications = () => {
  clearAllNotifications();
  showConfirmDeleteDialog.value = false;
};

const acceptFriendRequest = (index) => {
  // 친구 요청 수락 API 호출
  friendStore.AcceptFriendRequest(webSocketStore.notifications[index].content.senderId);

  console.log("친구 추가 요청 수락:", webSocketStore.notifications[index]);
  // 특정 알림 삭제
  webSocketStore.DeleteNotification(webSocketStore.notifications[index].id);
};

const declineFriendRequest = (index) => {
  console.log("친구 추가 요청 거절:", webSocketStore.notifications[index]);
  // 특정 알림 삭제
  webSocketStore.DeleteNotification(webSocketStore.notifications[index].id);
};

const acceptMeetingInvite = (index) => {
  console.log("회의실 초대 수락:", webSocketStore.notifications[index]);
  // 특정 알림 삭제
  webSocketStore.DeleteNotification(webSocketStore.notifications[index].id);
  router.push({
    name: "communityJoin",
    params: { id: webSocketStore.notifications[index].content.sessionId },
  });
  showDialog.value = false;
};

const declineMeetingInvite = (index) => {
  console.log("회의실 초대 거절:", webSocketStore.notifications[index]);
  // 특정 알림 삭제
  webSocketStore.DeleteNotification(webSocketStore.notifications.value[index].id);
};

const goToChat = (index) => {
  console.log("채팅으로 이동:", webSocketStore.notifications[index]);
  // 특정 알림 삭제
  webSocketStore.DeleteNotification(webSocketStore.notifications[index].id);
  router.push({
    name: "friends",
    query: { chatWith: webSocketStore.notifications[index].content.senderId },
  });
  showDialog.value = false;
};

const deleteChatMessage = (index) => {
  console.log("채팅 메시지 삭제:", webSocketStore.notifications[index]);
  // 특정 알림 삭제
  webSocketStore.DeleteNotification(webSocketStore.notifications[index].id);
};

// const goToSheet = (index) => {
//   router.push({ name: 'pianoUpload', params: { sheetId: 1 } });
//   console.log("악보로 이동:", notifications.value[index].message);
//   // 특정 알림 삭제
//   notificationStore.DeleteNotification(notifications.value[index].id);
//   notifications.value.splice(index, 1);
// };
</script>

<style scoped>
.notification-box {
  margin-top: 20px;
  background: #fff9e0;
  color: #947650;
}

.headline {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  margin-top: 16px;
  margin-left: 16px;
  margin-right: 16px;
}

.notification-list {
  background: #fff9e0;
  color: #947650;
  max-height: 300px;
}

.notification-actions {
  justify-content: flex-end;
  padding-bottom: 4px;
}

.delete-all {
  color: #fff9e0;
  background: #947650;
}

.yes-btn {
  color: #947650;
  background: #d9f6d9;
}

.no-btn {
  color: #ffffff;
  background: #ff7957;
}

.delete-box {
  background: #fff9e0;
  color: #947650;
}
</style>
