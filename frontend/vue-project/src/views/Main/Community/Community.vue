<template>
  <div>
    <div style="position: relative">
      <div class="bear-frame">
        <Bear class="bear" />
      </div>
      <div class="video-frame">
        <Video class="video" />
      </div>
      <div class="btn">
        <Create @exit="exit" />
      </div>
    </div>
  </div>
</template>
<script lang="ts" setup>
import { onBeforeRouteLeave, RouterView } from "vue-router";
import { ref } from "vue";
import Video from "@/components/Community/Video.vue";
import Bear from "@/components/Community/Bear.vue";
import Create from "@/components/Community/CreateCommunity.vue";
import { useOpenviduStore } from "@/stores/community";

const createCheck = ref(false);

const exit = function () {
  createCheck.value = false;
};

const openviduStore = useOpenviduStore();

onBeforeRouteLeave((to, from, next) => {
  if (!to.path.startsWith("/main/community")) {
    console.log("Exiting /community path");
    openviduStore.deinitOpenvidu();
  }
  next();
});
</script>
<style scoped>
.btn {
  position: absolute;
  margin-left: 100%;
  width: 250px;
  margin-top: -10%;
}
.bear-frame {
  width: 100%;
  height: 600px;
  margin-top: 3%;
  z-index: -1;
}
.bear {
  position: relative;
  width: 597px;
  height: 600px;
  margin: auto;
}
.video-frame {
  position: absolute;
  width: 100%;
  height: 430px;
  top: 150px;
  z-index: 1;
}
.video {
  position: relative;
  margin: auto;
  width: 410px;
}
</style>
