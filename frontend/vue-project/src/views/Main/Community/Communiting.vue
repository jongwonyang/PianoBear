<template>
  <div>
    <div class="screen">
      <JoinVideo :videos="videos" :surface="surface" bear-check="true" />
      <Test v-if="!openviduStore.isPlay" />
      <Chat v-show="chatOn" :chat="chat" />
    </div>
    <Controller />
  </div>
</template>
<script lang="ts" setup>
import { computed, onBeforeMount, ref } from "vue";
import Controller from "@/components/Community/Controller.vue";
import JoinVideo from "@/components/Community/JoinVideo.vue";
import Chat from "@/components/Community/JoinChat.vue";
import Test from "@/components/Community/test.vue";
import { onBeforeRouteLeave, useRoute, useRouter } from "vue-router";
import { useOpenviduStore } from "@/stores/community";

const openviduStore = useOpenviduStore();

const chatOn = ref(true);

const route = useRoute();
const router = useRouter();

onBeforeMount(() => {
  if (openviduStore.isInitialized) {
    openviduStore.joinSession(route.params.id as string);
  } else {
    router.replace({
      name: "communityJoin",
      params: { id: route.params.id as string },
    });
  }
});

const videos = computed(() => {
  if (openviduStore.isPlay) {
    return "videos1";
  } else {
    return "videos2";
  }
});

const surface = computed(() => {
  if (openviduStore.isPlay) {
    return "surface1";
  } else {
    return "surface2";
  }
});

const chat = computed(() => {
  if (openviduStore.isPlay) {
    return "chat1";
  } else {
    return "chat2";
  }
});

window.onbeforeunload = () => {
  openviduStore.leaveSession();
};
</script>

<style scoped>
.screen {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: center;
  position: absolute;
  top: 25px;
  left: 0%;
  width: 100%;
  height: 90%;
}
</style>
