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
              <v-btn v-tooltip:bottom="'내 정보 수정'" v-bind="activatorProps" icon="mdi-pencil-outline" class="edit-btn"
                density="comfortable"></v-btn>
            </template>
            <template v-slot:default="{ isActive }">
              <ProfileEdit v-if="isActive" :closeDialog="closeProfileDialog" />
            </template>
          </v-dialog>
          <!-- user name 가져오게 -->
          <div class="profile-name">{{ userInfo.userName }} 님 반갑습니다!</div>
          <!-- user가 전날까지 연속 연습 날짜를 가져오기 -->
          <div class="profile-day">{{ userInfo.streak }} 일 째 꾸준히 연습하고 있어요!</div>
          <div class="favorite-music">
            <md-elevation></md-elevation>
            <div class="music-item">
              <v-icon aria-hidden="false"> mdi-numeric-1-circle-outline </v-icon>
              <div class="music-name">
                {{ favoriteMusic[0] }}
              </div>
            </div>
            <div class="music-item">
              <v-icon aria-hidden="false"> mdi-numeric-2-circle-outline </v-icon>
              <div class="music-name">
                {{ favoriteMusic[1] }}
              </div>
            </div>
            <div class="music-item">
              <v-icon aria-hidden="false"> mdi-numeric-3-circle-outline </v-icon>
              <div class="music-name">
                {{ favoriteMusic[2] }}
              </div>
            </div>
          </div>
        </div>
      </div>
      <div>
        <v-btn v-tooltip:bottom="'로그아웃'" icon="mdi-account-minus" @click="LogOut" class="logout-btn"
          density="comfortable"></v-btn>
      </div>
    </div>

    <div class="practice-online">
      <div class="practice-box">
        <md-elevation></md-elevation>
        <div class="practice-header">
          <v-btn icon="mdi-menu-left-outline" class="pre-month-btn" density="compact" @click="previousMonth"></v-btn>
          <div>{{ currentYear }}년 {{ currentMonth }}월 연습 스티커</div>
          <v-btn icon="mdi-menu-right-outline" class="next-month-btn" density="compact" @click="nextMonth"></v-btn>
        </div>
        <div v-if="isLoading.practice" class="loading-bar">
          <v-progress-linear indeterminate color="#C69C67"></v-progress-linear>
        </div>
        <div v-else class="practice-calendar">
          <template v-for="(day, index) in practiceDays" :key="index">
            <v-dialog v-model="dialogState[index]" max-width="500">
              <template v-slot:activator="{ props: activatorProps }">
                <button class="honey-button" v-bind="activatorProps">
                  <img :src="day ? honeyFilled : honeyEmpty" alt="벌꿀" />
                  <v-tooltip activator="parent" location="bottom">{{ currentMonth }}월 {{ index + 1 }}일 연습기록</v-tooltip>
                </button>
              </template>
              <template v-slot:default="{ isActive }">
                <DayPracticeDetail v-if="isActive" :month="currentMonth" :day="index + 1" :year="currentYear"
                  :closeDialog="closeDialog" :index="index" />
              </template>
            </v-dialog>
          </template>
        </div>
        <v-divider style="margin-bottom: 15px"></v-divider>
        <div class="practice-day">{{ practiceDaysCount }}일 동안 연습했어요!</div>
      </div>
      <div class="online-friends-box">
        <md-elevation></md-elevation>
        <div class="online-friend-title">현재 온라인 친구들</div>
        <div v-if="isLoading.friends" class="loading-bar">
          <v-progress-linear indeterminate color="#C69C67"></v-progress-linear>
        </div>
        <div v-else class="online-friend">
          <template v-for="friend in topOnlineFriends" :key="friend.id">
            <div class="friend-box">
              <div class="friend-image" :style="{ backgroundImage: `url(${friend.profilePic.path})` }"></div>
              <div class="friend-name">{{ friend.name }}</div>
              <div class="friend-chat">
                <v-icon aria-hidden="false"> mdi-chat </v-icon>
                <v-tooltip activator="parent" location="bottom">채팅하기</v-tooltip>
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
import honeyFilledImg from "@/assets/images/채워진 벌꿀.png";
import honeyEmptyImg from "@/assets/images/빈 벌꿀.png";
import ProfileEdit from "@/components/MyInfo/ProfileEdit.vue";
import DayPracticeDetail from "@/components/MyInfo/DayPracticeDetail.vue";

const router = useRouter();
const userStore = useUserStore();
const dashboardStore = useDashboardStore();
const webSocketStore = useWebSocketStore();

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

const favoriteMusic = ref(["-", "-", "-"]);
const practiceRecord = ref([]);
const currentYear = ref(2023);
const currentMonth = ref(7);
const practiceDays = ref([]);
const dialogState = ref([]);
const onlineFriends = ref([]);

const practiceDaysCount = computed(() => {
  return practiceDays.value.filter((day) => day).length;
});

const honeyFilled = honeyFilledImg;
const honeyEmpty = honeyEmptyImg;

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
  } else {
    return "src/assets/characters/토니/토니머리.png ";
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
  padding: 50px 55px;
  border-radius: 30px;
  text-align: center;
  max-width: 2000px;
  width: 100%;
  margin-bottom: 20px;
  animation: fadeIn 0.5s ease-in-out;
}

.profile-content {
  display: flex;
  align-items: center;
  justify-content: center;
}

.profile-image {
  width: 180px;
  height: 180px;
  background-size: cover;
  background-position: center;
  border-radius: 50%;
  margin-right: 50px;
  margin-left: 30px;
}

.edit-btn {
  position: absolute;
  top: 20px;
  right: 20px;
  color: #947650;
  background: #f5e5d1;
}

.profile-info {
  text-align: center;
  margin: 10px;
}

.logout-btn {
  position: absolute;
  top: 20px;
  right: 70px;
  background: #f5e5d1;
  color: #e31515;
}

.practice-online {
  display: flex;
  justify-content: space-between;
  gap: 20px;
  animation: fadeIn 0.5s ease-in-out;
}

.practice-box,
.online-friends-box {
  background: #fff9e0;
  position: relative;
  padding: 30px 30px;
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
  margin-top: 25px;
  margin-bottom: 10px;
}

.honey-button {
  background: none;
  border: none;
  padding: 0;
  cursor: pointer;
  outline: none;
}

.honey-button img {
  width: 32px;
  height: 35px;
}

.profile-name {
  font-size: 22px;
  font-weight: 500;
  color: #947650;
}

.profile-day {
  font-size: 16px;
  color: #947650;
}

.favorite-music {
  background: #f5e5d1;
  display: flex;
  position: relative;
  padding: 20px 10px;
  border-radius: 10px;
  text-align: center;
  max-width: 2000px;
  width: 100%;
  margin-top: 30px;
  --md-sys-color-shadow: #947650;
}

.music-item {
  display: flex;
  align-items: center;
  width: 100px;
  /* 너비를 고정합니다 */
}

.music-name {
  font-size: 16px;
  margin-right: 10px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  flex: 1;
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
  color: #947650;
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
  color: #947650;
}
</style>
