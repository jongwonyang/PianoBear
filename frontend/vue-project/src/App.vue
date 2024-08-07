<template>
  <RouterView />
</template>

<script setup lang="ts">
import { RouterView } from 'vue-router'
import { onBeforeUnmount, onMounted, watch } from 'vue';
import { useUserStore } from '@/stores/user';
import { useWebSocketStore } from '@/stores/websocket';
import { storeToRefs } from 'pinia';

const userStore = useUserStore();
const webSocketStore = useWebSocketStore();

const { isLoggedIn } = storeToRefs(userStore);

// 페이지 로드 시 로그인 상태 확인 및 WebSocket 연결 설정
onMounted(() => {
  const token = localStorage.getItem("accessToken");

  if (token !== null) {
    userStore.isLoggedIn = true;
    userStore.GetUserInfo()
      .then((response) => {
        userStore.user = response?.data;
        console.log(userStore.user);
      })
      .catch((error) => {
        console.error(error);
      });
  }

  if (isLoggedIn.value) {
    webSocketStore.connectWebSocket();
  }

  // 브라우저 종료시 접속 여부 오프라인으로 설정
  window.addEventListener('beforeunload', handleBeforeUnload);
});

watch(isLoggedIn, (newVal, _) => {
  if (newVal === true) {
    // 로그인시 접속 여부 온라인으로 설정
    webSocketStore.connectWebSocket();
  } else {
    // 로그아웃시 접속 여부 오프라인으로 설정
    webSocketStore.disconnectWebSocket();
  }
});

function handleBeforeUnload(event: BeforeUnloadEvent) {
  if (userStore.isLoggedIn) {
    webSocketStore.disconnectWebSocket();
  }
}

// 컴포넌트가 파괴될 때 이벤트 리스너 제거
onBeforeUnmount(() => {
  window.removeEventListener('beforeunload', handleBeforeUnload);
});

</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;700&display=swap');

* {
  font-family: 'Inter', sans-serif;
}

html,
body {
  position: relative;
  width: 100%;
  height: 100%;
  background-size: cover;
  display: flex;
  justify-content: center;
  align-items: center;
  font-family: 'Arial', sans-serif;
  margin: 0;
  padding: 0;
  --md-sys-color-outline: #FFFFFF;
  --md-sys-color-on-surface: #947650;
  --md-sys-color-primary: #947650;
  --md-elevation-level: 5;
  --md-sys-color-shadow: #d2b659;
  animation: backgroundChange 20s infinite;
}

@keyframes backgroundChange {
  0% {
    background-image: url('./assets/images/background.webp');
  }

  20% {
    background-image: url('./assets/images/background2.webp');
  }

  40% {
    background-image: url('./assets/images/background3.webp');
  }

  60% {
    background-image: url('./assets/images/background4.webp');
  }

  80% {
    background-image: url('./assets/images/background5.webp');
  }

  100% {
    background-image: url('./assets/images/background.webp');
  }
}
</style>
