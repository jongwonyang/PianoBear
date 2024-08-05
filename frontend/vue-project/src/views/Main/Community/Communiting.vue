<template>
  <div>
    <div class="screen" v-if="openviduStore.isPlay">
      <JoinVideo videos="videos1" surface="surface1" />
      <Chat v-show="chatOn" chat="chat1" />
    </div>
    <div class="screen" v-else>
      <JoinVideo videos="videos2" surface="surface2" bear-check="true" />
      <Test />
      <Chat v-show="chatOn" chat="chat2" />
    </div>
    <Controller />
  </div>
</template>
<script lang="ts" setup>
import { onMounted, ref } from "vue";
import Controller from "@/components/Community/Controller.vue";
import JoinVideo from "@/components/Community/JoinVideo.vue";
import Chat from "@/components/Community/JoinChat.vue";
import Test from "@/components/Community/test.vue";
import { useRoute } from "vue-router";
import { useOpenviduStore } from "@/stores/community";

const openviduStore = useOpenviduStore();

const chatOn = ref(true);

const route = useRoute();
onMounted(() => {
  openviduStore.joinSession(route.params.id as string);
});
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
