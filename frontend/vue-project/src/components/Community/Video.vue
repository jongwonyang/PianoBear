<template>
  <div>
    <OvVideo
      class="video"
      v-if="publisher"
      :stream-manager="publisher"
    ></OvVideo>
    <v-sheet class="surface" :elevation="5" rounded="lg">
      <div class="controller">
        <v-btn icon="mdi-video-off" v-if="!videoCheck" @click="turnOnVideo" />
        <v-btn icon="mdi-video" v-else @click="turnOnVideo" variant="tonal" />
        <v-btn
          icon="mdi-microphone-off"
          v-if="!audioCheck"
          @click="turnOnAudio"
        />
        <v-btn
          icon="mdi-microphone"
          v-else
          @click="turnOnAudio"
          variant="tonal"
        />
      </div>
    </v-sheet>
  </div>
</template>

<script lang="ts" setup>
import { onMounted, ref } from "vue";
import { useOpenviduStore } from "@/stores/community";
import { storeToRefs } from "pinia";
import OvVideo from "./OvVideo.vue";

const openviduStore = useOpenviduStore();

const { publisher } = storeToRefs(openviduStore);

const videoCheck = ref(true);
const audioCheck = ref(true);

const turnOnVideo = function () {
  videoCheck.value = !openviduStore.publisher?.stream.videoActive as boolean;
  openviduStore.publisher?.publishVideo(videoCheck.value);
};

const turnOnAudio = function () {
  audioCheck.value = !openviduStore.publisher?.stream.audioActive as boolean;
  openviduStore.publisher?.publishAudio(audioCheck.value);
};

onMounted(() => {
  openviduStore.initOpenvidu();
});
</script>

<style scoped>
.video {
  position: relative;
  border: 1px solid black;
  width: 409px;
  height: 307px;
  border-radius: 50px;
  margin-bottom: 50px;
}
.surface {
  position: relative;
  height: 64px;
  width: 300px;
  margin: auto;
  background-color: #fff8d8;
}
.controller {
  display: flex;
  justify-content: space-evenly;
  align-items: center;
  transform: translate(0, 15%);
}
</style>
