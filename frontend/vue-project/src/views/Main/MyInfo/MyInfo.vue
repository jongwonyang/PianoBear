<template>
  <div>
    <div class="my-profile-box">
      <md-elevation></md-elevation>
      <div class="profile-content">
        <div v-if="isLoading.profile" class="loading-bar">
          <v-progress-linear indeterminate color="#C69C67"></v-progress-linear>
        </div>
        <img v-else class="profile-image" :src="userProfilePic" />
        <div class="profile-info">
          <!-- 편집 버튼 -->
          <v-dialog v-model="profileDialogOpen" max-width="500">
            <template v-slot:activator="{ props: activatorProps }">
              <v-btn
                v-tooltip:bottom="'내 정보 수정'"
                v-bind="activatorProps"
                icon="mdi-pencil-outline"
                class="edit-btn"
                density="comfortable"
              ></v-btn>
            </template>
            <template v-slot:default="{ isActive }">
              <ProfileEdit v-if="isActive" :closeDialog="closeProfileDialog" />
            </template>
          </v-dialog>
          <!-- user name 가져오게 -->
          <!-- <div class="profile-name">{{ userInfo.userName }}님 반갑습니다!</div> -->
          <!-- user가 전날까지 연속 연습 날짜를 가져오기 -->
          <div class="best">
            <span style="font-size: 200%; font-weight: bold">{{ userInfo.userName }}</span>
            <span class="text"> 님의 연습량 TOP3</span>
            <div class="favorite-music">
              <div class="music-item">
                <img src="@/assets/images/first.png" alt="" />
                <div class="music-name">
                  {{ favoriteMusic[0] }}
                </div>
              </div>
              <div class="music-item">
                <img src="@/assets/images/second.png" alt="" />
                <div class="music-name">
                  {{ favoriteMusic[1] }}
                </div>
              </div>
              <div class="music-item">
                <img src="@/assets/images/third.png" alt="" />
                <div class="music-name">
                  {{ favoriteMusic[2] }}
                </div>
              </div>
            </div>
            <span style="font-size: 200%; font-weight: bold">{{ userInfo.streak }}</span>
            <span class="profile-day"> 일 째 꾸준히 연습하고 있어요!</span>
          </div>
        </div>
      </div>
      <div>
        <v-btn
          v-tooltip:bottom="'로그아웃'"
          icon="mdi-account-minus"
          @click="LogOut"
          class="logout-btn"
          density="comfortable"
        ></v-btn>
      </div>
    </div>

    <div class="practice-online">
      <div class="practice-box">
        <div class="practice-box-container">
          <v-btn
            icon="mdi-menu-left-outline"
            class="pre-month-btn"
            density="compact"
            @click="previousMonth"
          ></v-btn>
          <div class="practice-date">{{ currentYear }}년 {{ currentMonth }}월</div>
          <v-btn
            icon="mdi-menu-right-outline"
            class="next-month-btn"
            density="compact"
            @click="nextMonth"
          ></v-btn>
        </div>
        <md-elevation></md-elevation>
        <div class="practice-header"></div>
        <div v-if="isLoading.practice" class="loading-bar">
          <v-progress-linear indeterminate color="#C69C67"></v-progress-linear>
        </div>
        <div v-else class="practice-calendar">
          <template v-for="(day, index) in practiceDays" :key="index">
            <v-dialog v-model="dialogState[index]" max-width="500">
              <template v-slot:activator="{ props: activatorProps }">
                <button class="honey-button" v-bind="activatorProps">
                  <img :src="day ? honeyFilled : honeyEmpty" alt="벌꿀" />
                  <v-tooltip activator="parent" location="bottom"
                    >{{ currentMonth }}월 {{ index + 1 }}일 연습기록</v-tooltip
                  >
                </button>
              </template>
              <template v-slot:default="{ isActive }">
                <DayPracticeDetail
                  v-if="isActive"
                  :month="currentMonth"
                  :day="index + 1"
                  :year="currentYear"
                  :closeDialog="closeDialog"
                  :index="index"
                />
              </template>
            </v-dialog>
          </template>
        </div>
        <v-divider style="margin-bottom: 15px"></v-divider>
        <div class="practice-day">{{ currentMonth }}월에 {{ practiceDaysCount }}일 연습했어요!</div>
      </div>
      <div class="online-friends-box">
        <div class="online-friend-title">온라인 친구들</div>
        <md-elevation></md-elevation>
        <div v-if="isLoading.friends" class="loading-bar">
          <v-progress-linear indeterminate color="#C69C67"></v-progress-linear>
        </div>
        <div v-else class="online-friend">
          <template v-for="friend in topOnlineFriends" :key="friend.id">
            <div class="friend-box">
              <div
                class="friend-image"
                :style="{ backgroundImage: `url(${friend.profilePic})` }"
              ></div>
              <div class="friend-name">{{ friend.name }}</div>
              <div>
                <v-btn
                  class="friend-chat"
                  icon="mdi-chat"
                  v-tooltip:bottom="'채팅하기'"
                  @click="handleChat(friend.id)"
                ></v-btn>
              </div>
            </div>
            <v-divider></v-divider>
          </template>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, computed } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/user";
import { useDashboardStore } from "@/stores/dashboard";
import { useWebSocketStore } from "@/stores/websocket";
import { usePianoSheetStore } from "@/stores/pianosheet";
import honeyFilledImg from "@/assets/images/채워진 벌꿀.png";
import honeyEmptyImg from "@/assets/images/빈 벌꿀.png";
import berryFilledImg from "@/assets/images/가득찬딸기.png";
import berryEmptyImg from "@/assets/images/빈딸기.png";
import ProfileEdit from "@/components/MyInfo/ProfileEdit.vue";
import DayPracticeDetail from "@/components/MyInfo/DayPracticeDetail.vue";

const router = useRouter();
const userStore = useUserStore();
const dashboardStore = useDashboardStore();
const webSocketStore = useWebSocketStore();
const pianoStore = usePianoSheetStore();

const isLoading = ref({
  profile: true,
  practice: true,
  friends: true,
});

const userInfo = ref({
  userId: "string",
  userName: "string",
  profileImage: "string",
  streak: 0,
  most: ["string"],
});

const handleChat = (friendId) => {
  router.push({
    name: "friends",
    query: { chatWith: friendId },
  });
};

const favoriteMusic = ref(["-", "-", "-"]);
const practiceRecord = ref([]);
const currentYear = ref(2024);
const currentMonth = ref(8);
const practiceDays = ref([]);
const dialogState = ref([]);
const onlineFriends = ref([]);

const practiceDaysCount = computed(() => {
  return practiceDays.value.filter((day) => day).length;
});

const honeyFilled = honeyFilledImg;
const honeyEmpty = honeyEmptyImg;

const berryFilled = berryFilledImg;
const berryEmpty = berryEmptyImg;

const topOnlineFriends = computed(() => {
  return onlineFriends.value.slice(0, 3);
});

const updatePracticeDays = (year, month) => {
  const daysInMonth = new Date(year, month, 0).getDate();
  practiceDays.value = Array.from({ length: daysInMonth }, () => false);
  dialogState.value = Array.from({ length: daysInMonth }, () => false);
};

const fetchPracticeRecords = (year, month, day = null) => {
  isLoading.value.practice = true;
  dashboardStore
    .GetMonthlyPracticeRecord(year, month, day)
    .then((res) => {
      practiceRecord.value = Array.isArray(res.data) ? res.data : [];
      practiceRecord.value.forEach((record) => {
        const date = new Date(record.practiceDate);
        practiceDays.value[date.getDate() - 1] = true;
      });
      isLoading.value.practice = false;
    })
    .catch((error) => {
      console.error(error);
      isLoading.value.practice = false;
    });
};

const previousMonth = () => {
  if (currentMonth.value === 1) {
    currentYear.value--;
    currentMonth.value = 12;
  } else {
    currentMonth.value--;
  }
  updatePracticeDays(currentYear.value, currentMonth.value);
  fetchPracticeRecords(currentYear.value, currentMonth.value);
};

const nextMonth = () => {
  if (currentMonth.value === 12) {
    currentYear.value++;
    currentMonth.value = 1;
  } else {
    currentMonth.value++;
  }
  updatePracticeDays(currentYear.value, currentMonth.value);
  fetchPracticeRecords(currentYear.value, currentMonth.value);
};

onMounted(() => {
  // 유저 정보 가져오기
  dashboardStore
    .GetSummary()
    .then((res) => {
      console.log(res);
      isLoading.value.profile = false;
      userInfo.value.userId = res.data.userId;
      userInfo.value.userName = res.data.userName;
      userInfo.value.profileImage = res.data.profileImage;
      userInfo.value.streak = res.data.streak;
      userInfo.value.most = res.data.most;

      // favoriteMusic 배열 업데이트
      for (let i = 0; i < favoriteMusic.value.length; i++) {
        favoriteMusic.value[i] = userInfo.value.most[i] || "-";
      }
    })
    .catch((error) => {
      console.error(error);
    });

  // 초기 연습 기록 가져오기
  updatePracticeDays(currentYear.value, currentMonth.value);
  fetchPracticeRecords(currentYear.value, currentMonth.value);

  // 친구 목록 가져오기
  dashboardStore
    .GetOnlineFriends()
    .then((res) => {
      console.log(res);
      onlineFriends.value = res.data;
      isLoading.value.friends = false;
    })
    .catch((error) => {
      console.error(error);
      isLoading.value.friends = false;
    });

  // 웹소켓 연결
  // if (!webSocketStore.connected) {
  //   webSocketStore.connectWebSocket();
  // }
});

const userProfilePic = computed(() => {
  if (userInfo.value.profileImage) {
    return (
      import.meta.env.VITE_API_BASE_URL +
      userInfo.value.profileImage.slice(7, userInfo.value.profileImage.length)
    );
  }
});

const profileDialogOpen = ref(false);

const closeProfileDialog = () => {
  profileDialogOpen.value = false;
};

const closeDialog = (index) => {
  dialogState.value[index] = false;
};

async function LogOut() {
  try {
    const accessToken = userStore.GetAccessToken();
    const refreshToken = userStore.GetRefreshToken();
    await userStore.LogoutUser(accessToken, refreshToken);
    userStore.RemoveToken();
    router.push("/login");
  } catch (error) {
    console.log("로그아웃에 실패했습니다.");
    console.error(error);
  }
}
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

.loading-bar {
  position: relative;
  width: 100%;
  margin-bottom: 20px;
}

.my-profile-box {
  background: #fff9e0;
  position: relative;
  /* padding-top: 10vh;
  padding-bottom: 10vh; */
  border-radius: 30px;
  text-align: center;
  width: 60vw;
  height: 45vh;
  margin-bottom: 20px;
  animation: fadeIn 0.5s ease-in-out;
}

.profile-content {
  display: flex;
  align-items: center;
  /* justify-content: center; */
  padding: 3vh;
}

.profile-image {
  width: 19vw;
  height: 38vh;
  border-radius: 50%;
  margin-left: 5vw;
  border: 1px solid black;
}

.edit-btn {
  position: absolute;
  top: 20px;
  right: 70px;
  color: #947650;
  background: #f5e5d1;
}

.profile-info {
  text-align: center;
  margin-left: 4vw;
}

.logout-btn {
  position: absolute;
  top: 20px;
  right: 20px;
  background: #f5e5d1;
  color: #e31515;
}

.practice-online {
  display: flex;
  justify-content: space-between;
  /* gap: 20px; */
  animation: fadeIn 0.5s ease-in-out;
  /* border-radius: 30px; */
}

.practice-date {
  width: fit-content;
  font-size: 120%;
  font-weight: bold;
  color: black;
}

.practice-box,
.online-friends-box {
  background: #fff9e0;
  position: relative;
  padding-left: 1vw;
  border-radius: 30px;
  text-align: center;
  color: #947650;
  max-width: 400px;
  width: 100%;
}

.practice-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: absolute;
  top: 20px;
  left: 20px;
  right: 20px;
}

.practice-calendar {
  display: flex;
  flex-wrap: wrap;
  justify-content: left;
  gap: 10px;
  margin-top: 1vh;
  margin-bottom: 1vh;
}

.honey-button {
  background: none;
  border: none;
  padding: 0;
  cursor: pointer;
  outline: none;
}

.honey-button img {
  width: 43px;
  height: 45px;
}

.profile-name {
  font-size: 150%;
  font-weight: bold;
  color: black;
  margin-bottom: 2vh;
}

.profile-day {
  font-size: 120%;
  font-weight: bold;
  color: black;
  text-align: start;
}

.favorite-music {
  background: #e5ccaa;
  display: flex;
  position: relative;
  padding: 20px 10px;
  border-radius: 10px;
  text-align: center;
  width: 25vw;
  height: 25vh;
  margin-bottom: 1vh;
  flex-direction: column;
  gap: 2vh;
}

.music-item {
  display: flex;
  align-items: center;
  margin-top: 0.3vh;
  margin-left: 1vw;
}

.music-name {
  font-size: 120%;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  flex: 1;
  margin-left: 1vw;
  display: inline-block;
  text-align: left;
  /* font-weight: bold; */
}

.pre-month-btn,
.next-month-btn {
  color: #947650;
  background: #f5e5d1;
}

.friend-box {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
  margin-bottom: 12px;
}

.friend-image {
  width: 70px;
  height: 70px;
  background-size: cover;
  background-position: center;
  border-radius: 50%;
}

.friend-name {
  font-size: 18px;
  font-weight: 500;
  color: black;
}

.friend-chat {
  color: #947650;
  background: #f5e5d1;
  padding: 10px;
  border-radius: 50%;
  cursor: pointer;
}

.online-friend-title {
  font-size: 20px;
  font-weight: 500;
  color: black;
  font-weight: bold;
  margin-top: 10px;
}

.text {
  font-size: 110%;
  font-weight: bold;
}

.music-item img {
  width: 2.5vw;
}

.best {
  margin-top: 1vh;
}

.practice-box-container {
  display: flex;
  justify-content: space-around;
  margin-top: 10px;
}

.practice-day {
  text-align: end;
  margin-right: 1.5vw;
  margin-bottom: 5px;
  color: black;
  font-weight: bold;
}
</style>
