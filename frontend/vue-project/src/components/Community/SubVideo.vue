<template>
  <!-- <Bear class="bear" /> -->
  <v-sheet
    style="position: relative; overflow: hidden"
    v-if="streamManager"
    :elevation="3"
    rounded="lg"
  >
    <OvVideo
      :stream-manager="streamManager"
      style="position: absolute"
      :muted="muted"
    ></OvVideo>
    <div class="menus">
      <Menu
        @toggle-audio="onToggleAudio"
        :muted="muted"
        style="padding: 2%"
      ></Menu>

      <Transition name="fade">
        <div
          v-if="communityStore.emoteMap.has(clientData.id)"
          :class="emoteSize"
        >
          {{ communityStore.emoteMap.get(clientData.id).content }}
        </div>
      </Transition>

      <div class="info">
        <span v-if="!hasVideo" class="mdi mdi-video-off me-2"></span>
        <span v-if="!hasAudio" class="mdi mdi-volume-off me-2"></span>
        <span
          v-if="muted"
          class="mdi mdi-volume-mute me-2"
          style="color: red"
        ></span>
        <span>{{ clientData.name }}</span>
      </div>
    </div>
  </v-sheet>
</template>

<script setup>
import { computed, ref } from "vue";
import Menu from "./Menu.vue";
import OvVideo from "./OvVideo.vue";
import { useUserStore } from "@/stores/user";
import { useOpenviduStore } from "@/stores/community";

const muted = ref(false);

const props = defineProps({
  streamManager: Object,
  surface: String,
});

const hasVideo = ref(props.streamManager.stream.videoActive);
const hasAudio = ref(props.streamManager.stream.audioActive);

props.streamManager.on("streamPropertyChanged", (event) => {
  if (event.changedProperty === "videoActive") {
    hasVideo.value = event.newValue;
  }
  if (event.changedProperty === "audioActive") {
    hasAudio.value = event.newValue;
  }
});

const emoteSize = computed(() => {
  if (props.surface == "surface1") {
    return "emote1";
  } else {
    return "emote2";
  }
});

function getConnectionData() {
  const { connection } = props.streamManager.stream;
  return JSON.parse(connection.data);
}

const clientData = computed(() => {
  return getConnectionData().clientData;
});

function onToggleAudio() {
  muted.value = !muted.value;
}

const userStore = useUserStore();
const communityStore = useOpenviduStore();
</script>

<style scoped>
.menus {
  display: flex;
  flex-direction: column;
  align-items: end;
  justify-content: space-between;

  height: 100%;
  z-index: 100;
}

.info {
  color: white;
  width: 100%;
  z-index: 100;
  max-width: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  padding: 5px 10px;
}

.emote1 {
  width: 100%;
  font-size: 180px;
  line-height: 220px;
  text-align: center;
  z-index: 100;
}

.emote2 {
  width: 100%;
  font-size: 100px;
  line-height: 70px;
  text-align: center;
  z-index: 100;
}

.fade-enter-active {
  transition: font-size 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.fade-leave-active {
  transition: opacity 7s ease-in-out;
}

.fade-enter-from {
  font-size: 0px;
}

.fade-leave-to {
  opacity: 0;
}
</style>
