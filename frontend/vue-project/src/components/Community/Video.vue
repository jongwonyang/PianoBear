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
  requestWebcamAndMicPermissions()
    .then(() => {
      // 권한이 승인된 후의 로직을 여기에 작성합니다.
      openviduStore.initOpenvidu();
      console.log("권한이 성공적으로 요청되었습니다.");
    })
    .catch((error) => {
      // 권한 요청이 실패했을 때의 로직
      alert("소통방에 참여하려면 카메라와 마이크 권한이 필요합니다.");
      console.log("권한 요청이 실패했습니다:", error);
    });
});

async function requestWebcamAndMicPermissions() {
  try {
    // 웹캠과 마이크 권한 요청
    const stream = await navigator.mediaDevices.getUserMedia({
      video: true, // 웹캠 권한
      audio: true, // 마이크 권한
    });

    // 스트림 사용 없이 권한만 요청하는 경우, 스트림을 바로 중지
    stream.getTracks().forEach((track) => track.stop());

    console.log("웹캠과 마이크 권한이 승인되었습니다.");
  } catch (error) {
    console.error("웹캠 또는 마이크 권한이 거부되었습니다:", error);
    throw error; // 권한 요청 실패 시 에러를 던집니다.
  }
}
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
