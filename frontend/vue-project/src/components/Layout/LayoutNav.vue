<template>
  <v-card>
    <v-layout>
      <v-navigation-drawer expand-on-hover rail>
        <v-list>
          <v-list-item prepend-avatar="@/assets/images/정수_어렸을적.png" subtitle="kmk2528@naver.com"
            title="하정수"></v-list-item>
        </v-list>
        <v-divider></v-divider>
        <v-list density="compact" nav>
          <v-list-item prepend-icon="mdi-home-variant-outline" title="메인" value="내 정보" @click="router.push({name: 'myInfo'})"></v-list-item>
          <v-list-item prepend-icon="mdi-account-multiple-outline" title="친구들" value="친구들" @click="router.push({name: 'friends'})"></v-list-item>
          <v-list-item prepend-icon="mdi-music-box-multiple" title="내 악보" value="악보목록" @click="router.push({name: 'pianoSheetView'})"></v-list-item>
          <v-list-item prepend-icon="mdi-video-account" title="놀이터" value="소통방" @click="router.push({name: 'communityView'})"></v-list-item>
          <v-list-item prepend-icon="mdi-bell-outline" title="알림" value="알림"></v-list-item>
        </v-list>
      </v-navigation-drawer>

    </v-layout>
  </v-card>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();


</script>

<style scoped></style>