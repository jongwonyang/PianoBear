<template>
  <div>
    <v-sheet
      class="surface"
      :elevation="10"
      :height="64"
      :width="600"
      rounded="lg"
    >
      <div class="controller">
        <v-btn
          icon="mdi-video-off"
          v-if="!openviduStore.isVideoActive"
          @click="turnOnVideo"
        />
        <v-btn icon="mdi-video" v-else @click="turnOnVideo" variant="tonal" />
        <v-btn
          icon="mdi-microphone-off"
          v-if="!openviduStore.isAudioActive"
          @click="turnOnAudio"
        />
        <v-btn
          icon="mdi-microphone"
          v-else
          @click="turnOnAudio"
          variant="tonal"
        />
        <EmoteList></EmoteList>
        <v-btn
          icon="mdi-music-note"
          v-if="!openviduStore.isPlay"
          @click="switchPlay"
        />
        <v-btn
          icon="mdi-music-note"
          v-else
          @click="switchPlay"
          variant="tonal"
        />
        <FriendList variant="tonal" />
        <DialogsModal text="나가기" content="나가시겠습니까?" @join="exit" />
      </div>
    </v-sheet>
    <div class="emote"></div>
  </div>
</template>
<script lang="ts" setup>
import { onMounted, ref, defineProps, defineEmits } from "vue";
import { useRouter, useRoute } from "vue-router";
import FriendList from "./FriendList.vue";
import DialogsModal from "./DialogsModal.vue";
import { useOpenviduStore } from "@/stores/community";
import EmoteList from "./EmoteList.vue";

const router = useRouter();
const route = useRoute();
const emoteCheck = ref(false);

const openviduStore = useOpenviduStore();

onMounted(() => {
  openviduStore.isVideoActive = openviduStore.publisher?.stream
    .videoActive as boolean;
  openviduStore.isAudioActive = openviduStore.publisher?.stream
    .audioActive as boolean;
});

const turnOnVideo = function () {
  openviduStore.isVideoActive = !openviduStore.publisher?.stream
    .videoActive as boolean;
  openviduStore.publisher?.publishVideo(openviduStore.isVideoActive);
};

const turnOnAudio = function () {
  openviduStore.isAudioActive = !openviduStore.publisher?.stream
    .audioActive as boolean;
  openviduStore.publisher?.publishAudio(openviduStore.isAudioActive);
};

const switchEmote = function () {
  if (!emoteCheck.value) {
    emoteCheck.value = true;
  } else {
    emoteCheck.value = false;
  }
};

const switchPlay = function () {
  if (!openviduStore.isPlay) {
    openviduStore.isPlay = true;
  } else {
    openviduStore.isPlay = false;
  }
};

const exit = function () {
  if (openviduStore.subscribers.length == 0) {
    router.push({ name: "community" });
  } else {
    router.push({
      name: "communityJoin",
      params: { id: route.query.value as string },
    });
  }
  openviduStore.leaveSession();
};
</script>
<style scoped>
.surface {
  position: fixed;
  background-color: #fff8d8;
  bottom: 2%;
  left: 35%;
  padding: 0;
}

.controller {
  display: flex;
  position: relative;
  top: 7px;
  justify-content: space-evenly;
  align-items: center;
}
</style>
