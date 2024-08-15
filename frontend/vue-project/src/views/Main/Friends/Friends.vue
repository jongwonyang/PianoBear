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
            <img class="my-status-image" :src="userProfilePic" />
            <div class="my-status-ele">
              <div class="my-name">{{ userInfo.name }}</div>
              <!-- 상태 메시지 수정 버튼 -->
              <div class="my-status-message-box">
                <div class="my-status-message">
                  {{ userInfo.statusMessage }}
                </div>
              </div>
            </div>
          </div>
          <v-btn
            icon="mdi-pencil-outline"
            class="edit-status-message"
            @click="editStatusMessage = true"
            density="comfortable"
          ></v-btn>
        </div>
      </div>
      <div class="friend-box">
        <md-elevation></md-elevation>
        <div class="my-friends-header">
          <div class="my-friends-text">친구들</div>
          <v-btn
            append-icon="mdi-account-search"
            size="small"
            class="add-friend-btn"
            @click="showDialog = true"
            >친구 검색</v-btn
          >
        </div>
        <v-divider></v-divider>
        <div v-if="isLoading.friendList" class="loading-bar">
          <v-progress-linear indeterminate color="#C69C67"></v-progress-linear>
        </div>
        <div v-else class="my-friends-ele">
          <div
            class="friend-item"
            v-for="friend in friends"
            :key="friend.id"
            @click="viewFriendInfo(friend.id)"
          >
            <div class="my-friends-ele-left">
              <img :src="getFriendProfile(friend.profilePic)" alt="친구 img" />
            </div>
            <div class="my-friends-ele-right">
              <div class="friend-name">{{ friend.name }}</div>
              <div
                class="friend-status-message"
                v-if="friend.statusMessage != ''"
              >
                <md-elevation></md-elevation>
                {{ friend.statusMessage }}
              </div>
            </div>
          </div>
          <v-divider></v-divider>
        </div>
      </div>
    </div>
    <div style="padding: 20px; display: flex">
      <div class="chat-box">
        <div class="my-chatting-header">
          <div class="my-chatting-text" v-if="currentChatRoomId">
            채팅 - {{ receiver.name }}
          </div>
        </div>
        <v-divider></v-divider>
        <div class="chat-container" v-if="currentChatRoomId">
          <div class="messages">
            <div
              v-for="(message, index) in messages.slice().reverse()"
              :key="index"
              :class="{
                message: true,
                sent: message.senderId === userInfo.id,
                received: message.senderId !== userInfo.id,
              }"
            >
              <div class="message-header">
                <strong
                  >{{
                    message.senderId === userInfo.id ? "나" : receiver.name
                  }}&nbsp; : &nbsp;
                </strong>
                <span class="timestamp">{{
                  formatTimestamp(message.timestamp)
                }}</span>
              </div>
              <div class="message-content">{{ message.content }}</div>
            </div>
          </div>
          <div class="input-area">
            <input
              v-model="newMessage"
              placeholder="메시지를 입력하세요"
              @keyup.enter="sendMessage"
            />
            <button @click="sendMessage">전송</button>
          </div>
        </div>
        <div
          v-if="!currentChatRoomId"
          style="margin: auto"
          class="my-chatting-text"
        >
          친구 목록에서 친구와 대화를 시작하세요!
        </div>
      </div>
    </div>
  </div>

  <!-- 친구 정보 다이얼로그 -->
  <v-dialog v-model="friendInfoDialog" max-width="500px">
    <v-card class="friend-info-form">
      <v-card-title class="headline">친구 정보</v-card-title>
      <v-card-text v-if="friendInfo">
        <div class="friend-item">
          <div class="my-friends-ele-left">
            <img
              :src="getFriendProfile(friendInfo.profilePic)"
              alt="친구 img"
            />
          </div>
          <div class="my-friends-ele-right">
            <div class="friend-name">{{ friendInfo.name }}</div>
            <div
              class="friend-status-message"
              v-if="friendInfo.statusMessage != ''"
            >
              <md-elevation></md-elevation>
              {{ friendInfo.statusMessage }}
            </div>
          </div>
        </div>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn text @click="removeFriend(friendInfo.id)" color="red"
          >친구 삭제</v-btn
        >
        <v-btn text @click="chatButtonPressed(friendInfo.id)">대화하기</v-btn>
        <v-btn text @click="friendInfoDialog = false">닫기</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>

  <!-- 친구 검색 다이얼로그 -->
  <v-dialog v-model="showDialog" max-width="500px">
    <v-card class="add-friend-form pa-5">
      <v-card-title class="headline">친구 검색</v-card-title>
      <v-card-text>친구의 이름이나 아이디를 입력하세요!</v-card-text>
      <v-text-field
        label="친구 아이디"
        v-model="searchQuery"
        @keyup.enter="searchFriend"
      ></v-text-field>
      <v-card-text v-if="searchResult">
        <div class="friend-item">
          <div class="my-friends-ele-left">
            <img
              :src="getFriendProfile(searchResult.profilePic)"
              alt="친구 img"
            />
          </div>
          <div class="my-friends-ele-right">
            <div class="friend-name">{{ searchResult.name }}</div>
            <div
              class="friend-status-message"
              v-if="searchResult.statusMessage != ''"
            >
              <md-elevation></md-elevation>
              {{ searchResult.statusMessage }}
            </div>
            <v-btn
              v-if="
                !isFriend(searchResult.id) &&
                searchResult.id != userInfo.id &&
                !searchResultSentRequest
              "
              class="add-friend-btn"
              @click="addFriend(searchResult.id)"
              >추가</v-btn
            >
            <v-btn v-else-if="searchResult.id == userInfo.id" disabled
              >자신은 추가할 수 없습니다</v-btn
            >
            <v-btn v-else-if="searchResultSentRequest" disabled
              >친구 수락 대기중..</v-btn
            >
            <v-btn v-else class="add-friend-btn" disabled
              >이미 친구입니다</v-btn
            >
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
  <!-- 상태메시지 수정 다이얼로그 -->
  <v-dialog v-model="editStatusMessage" max-width="500px">
    <v-card class="edit-status-form">
      <v-card-title class="headline">상태 메시지 수정</v-card-title>
      <v-card-text>
        <v-text-field
          label="상태 메시지"
          v-model="newStatusMessage"
        ></v-text-field>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn text @click="editStatusMessage = false">취소</v-btn>
        <v-btn text @click="saveStatusMessage">저장</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup>
import { onMounted, ref, nextTick, computed, watch } from "vue";
import { useUserStore } from "@/stores/user";
import { useFriendStore } from "@/stores/friend";
import { useWebSocketStore } from "@/stores/websocket";
import { onBeforeRouteUpdate, useRoute, useRouter } from "vue-router";

const userInfo = ref({});
const isLoading = ref({
  userInfo: true,
  friendList: true,
  chattingList: true,
});
const friends = ref([]);
const currentChatRoomId = ref(null); // 현재 채팅방 ID
const messages = ref([]); // 현재 채팅방 메시지 리스트
const newMessage = ref(""); // 새 메시지 입력 필드

const userStore = useUserStore();
const friendStore = useFriendStore();
const webSocketStore = useWebSocketStore();
const router = useRouter();
const route = useRoute();

const showDialog = ref(false);
const friendInfoDialog = ref(false);
const searchQuery = ref("");
const searchResult = ref(null);
const searchResultSentRequest = ref(false);
const friendInfo = ref(null);
const receiver = ref(null);

let unSubscribeLastChat = null;

const editStatusMessage = ref(false); // 상태 메시지 수정 다이얼로그 상태
const newStatusMessage = ref(""); // 새로운 상태 메시지

onMounted(() => {
  // 친구 목록을 불러오는 로직
  friendStore
    .GetFriendList()
    .then((res) => {
      friends.value = res.data;
      isLoading.value.friendList = false;
      userInfo.value = userStore.user;
      console.log(userInfo.value);
      isLoading.value.userInfo = false;

      // 쿼리 매개변수를 통해 받은 friendId로 채팅 시작
      const chatWith = router.currentRoute.value.query.chatWith;
      if (chatWith) {
        router.push({
          name: "friends",
          query: { chatWith: router.currentRoute.value.query.chatWith },
        });
        startChatting(chatWith);
      }
    })
    .catch((err) => {
      console.log(err);
    });
});

watch(
  () => route.query,
  (newQuery, oldQuery) => {
    // 쿼리 매개변수를 통해 받은 friendId로 채팅 시작
    const chatWith = newQuery.chatWith;
    if (chatWith) {
      startChatting(chatWith);
    }
  },
  { immediate: true }
);

function chatButtonPressed(friendId) {
  router.push({
    name: "friends",
    query: { chatWith: friendId },
  });

  // 채팅창을 표시하기 위해 다이얼로그를 닫음
  friendInfoDialog.value = false;
}

async function startChatting(friendId) {
  try {
    // 채팅방 열기
    const chatRoom = await webSocketStore.enterChatRoom(friendId);
    console.log("채팅방을 열었습니다:", chatRoom);
    friendStore.GetFriendInfo(friendId).then((result) => {
      receiver.value = result.data;
      messages.value = chatRoom.messages;
      currentChatRoomId.value = chatRoom.id;
    });

    if (unSubscribeLastChat != null) {
      unSubscribeLastChat();
      unSubscribeLastChat = null;
    }

    // 채팅방에 메시지 구독
    unSubscribeLastChat = webSocketStore.subscribeToChatRoom(
      chatRoom.id,
      (message) => {
        messages.value.push(message);
      }
    );
  } catch (error) {
    console.error("채팅방을 열지 못했습니다:", error);
  }
}

const sendMessage = () => {
  if (newMessage.value.trim() !== "") {
    webSocketStore.sendMessage(receiver.value.id, newMessage.value);
    newMessage.value = ""; // 입력 필드 초기화
    console.log("메시지를 보냈습니다:", messages.value);

    // 메시지를 보낸 후에도 채팅창을 맨 아래로 스크롤

    nextTick(() => {
      scrollToBottom();
    });
  }
};

// onBeforeUnmount(() => {
//     webSocketStore.disconnectWebSocket();
// });

// 채팅창을 맨 아래로 스크롤하는 함수
const scrollToBottom = () => {
  const chatContainer = document.querySelector(".chat-box .messages");
  if (chatContainer) {
    chatContainer.scrollTop = chatContainer.scrollHeight;
  }
};

const formatTimestamp = (timestamp) => {
  const date = new Date(timestamp);
  const hours = date.getHours();
  const minutes = date.getMinutes();
  return `${hours}시 ${minutes.toString().padStart(2, "0")}분`;
};

const searchFriend = () => {
  friendStore
    .GetFriendInfo(searchQuery.value)
    .then((res) => {
      searchResult.value = res.data;
      console.log(res);
      // 검색 결과에 대해 친구 요청을 보냈는지 확인
      friendStore
        .IsSentRequest(searchResult.value.id)
        .then((requestRes) => {
          searchResultSentRequest.value = requestRes.data;
        })
        .catch((err) => {
          console.log(err);
          searchResultSentRequest.value = false;
        });
    })
    .catch((err) => {
      searchResult.value = null;
      searchResultSentRequest.value = false;
      console.log(err);
    });
};

const addFriend = (id) => {
  // 친구 추가 로직을 여기에 구현합니다.
  friendStore
    .SendFriendRequest(id)
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
  return friends.value.some((friend) => friend.id === id);
};

const viewFriendInfo = (id) => {
  friendStore
    .GetFriendInfo(id)
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
  friendStore
    .RemoveFriend(id)
    .then((res) => {
      console.log(res);
      console.log(`친구 ID: ${id}를 삭제하였습니다.`);
      // 친구 목록 업데이트
      friends.value = friends.value.filter((friend) => friend.id !== id);
      friendInfoDialog.value = false; // 다이얼로그를 닫습니다.
    })
    .catch((err) => {
      console.log(err);
    });
};

const saveStatusMessage = () => {
  userStore
    .UpdateStatusMessage(newStatusMessage.value)
    .then((res) => {
      userInfo.value.statusMessage = newStatusMessage.value; // 상태 메시지 업데이트
      editStatusMessage.value = false; // 다이얼로그 닫기
      console.log("상태 메시지가 업데이트되었습니다.");
    })
    .catch((err) => {
      console.log("상태 메시지 업데이트 실패:", err);
    });
};

const userProfilePic = computed(() => {
  if (userInfo.value.profilePic) {
    return (
      import.meta.env.VITE_API_BASE_URL +
      userInfo.value.profilePic.slice(7, userInfo.value.profilePic.length)
    );
  } else if (userInfo.gender === "M") {
    return "src/assets/characters/토니/토니머리.png ";
  } else {
    return "src/assets/characters/피치/피치머리.png ";
  }
});

const getFriendProfile = (profilePic) => {
  return (
    `${import.meta.env.VITE_API_BASE_URL}` +
    profilePic.slice(7, profilePic.length)
  );
};

const friendProfilePic = computed(() => {
  for (let i = 0; i < friends.value.length; i++) {
    if (friends.value[i].profilePic) {
      return (
        import.meta.env.VITE_API_BASE_URL +
        friends.value[i].profilePic.slice(7, friends.value[i].profilePic.length)
      );
    }
  }
});
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
  background: #fff9e0;
  position: relative;
  padding: 30px;
  border-radius: 30px;
  text-align: center;
  margin-bottom: 20px;
}

.friend-box {
  flex: 1;
  background: #fff9e0;
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
  background: #fff9e0;
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
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
}

.my-name {
  font-size: 20px;
  font-weight: 700;
  color: #947650;
  margin-bottom: 5px;
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
  padding-bottom: 10px;
}

.my-status-message-box {
  background: #f5e5d1;
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

.edit-status-message {
  position: absolute;
  top: 55px;
  /* 하단에서의 간격 */
  right: 20px;
  /* 우측에서의 간격 */
  background-color: #f5e5d1;
  color: #947650;
}

.edit-status-form {
  background: #fff9e0;
  color: #947650;
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
  margin: 3px 0;
  padding: 10px;
  border-bottom: 0.5px solid #947650;
  opacity: 1;
  -webkit-transition: 0.2s ease-in-out;
  transition: 0.2s ease-in-out;
  border-radius: 10px;

  background-color: #9476500c;
}

.friend-item:hover {
  background-color: #9476504e;
}

.add-friend-btn {
  color: #947650;
  background-color: #f5e5d1;
}

.my-friends-ele-left {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 0 10px;
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
  background: #f5e5d1;
  padding: 10px;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 500;
  color: #947650;
  width: 100%;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  /* 원하는 줄 수 */
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.add-friend-form {
  background: #fff9e0;
  color: #947650;
}

.friend-status-message {
  margin-bottom: 5px;
}

.friend-info-form {
  background: #fff9e0;
  color: #947650;
}

.chat-box {
  flex: 1;
  width: 500px;
  background: #fff9e0;
  position: relative;
  border-radius: 30px;
  text-align: center;

  --md-sys-color-shadow: #947650;
}

.chat-container {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.messages {
  flex-grow: 1;
  display: flex;
  flex-direction: column-reverse;
  padding: 10px;
  overflow-y: auto;
  scrollbar-width: none;
  /* 스크롤 활성화 */
  max-height: calc(100% - 60px);
  /* 스크롤을 위한 고정된 높이 설정 */
  padding-bottom: 60px;
}

.message {
  max-width: 70%;
  margin-bottom: 10px;
  padding: 10px;
  border-radius: 10px;
}

.message.sent {
  align-self: flex-end;
  background-color: #dcf8c6;
}

.message.received {
  align-self: flex-start;
  background-color: #fff;
}

.message-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
  font-size: 12px;
  color: #999;
}

.message-content {
  font-size: 14px;
}

.input-area {
  display: flex;
  padding: 10px;
  border-top: 1px solid #ddd;
  background-color: #fff;
  position: sticky;
  bottom: 0;
  /* 화면 하단에 고정 */
}

input {
  flex-grow: 1;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 10px;
  margin-right: 10px;
}

.chat-box {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #fff9e0;
  position: relative;
  border-radius: 30px;
  text-align: center;
  overflow: hidden;
  --md-sys-color-shadow: #947650;
}

.my-chatting-header {
  justify-content: center;
  align-items: center;
  position: sticky;
  top: 0;
  background: #fff9e0;
  z-index: 10;
  padding: 10px 0;
}

.my-chatting-text {
  font-size: 20px;
  font-weight: 700;
  color: #947650;
}
</style>
