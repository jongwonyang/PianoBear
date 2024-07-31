<template>
  <v-card>
    <v-layout>
      <v-navigation-drawer expand-on-hover rail>
        <v-list>
          <!-- 여기는 이미지 넣을 건데 이미지는 유저의 프로필 사진을 가져올거임 -->
          <v-list-item prepend-avatar="@/assets/images/정수_어렸을적.png" subtitle="kmk2528@naver.com"
            title="하정수"></v-list-item>
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
          <!-- 알림 구현 필요 -->
          <v-list-item prepend-icon="mdi-bell-outline" title="알림" value="알림"></v-list-item>
        </v-list>
      </v-navigation-drawer>

    </v-layout>
  </v-card>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';

const socket = new SockJS('http://localhost:7000/ws');
const stompClient = Stomp.over(socket);

stompClient.connect(
  { Authorization: 'Bearer ' + localStorage.getItem('accessToken') },
  (frame) => {
    console.log('Connected: ' + frame);

    // Subscribe to a topic if needed
  }
);

// Handle window close event to properly disconnect
window.addEventListener('beforeunload', () => {
  stompClient.disconnect(() => {
    console.log('Disconnected');
  });
});


const router = useRouter();


</script>

<style scoped></style>