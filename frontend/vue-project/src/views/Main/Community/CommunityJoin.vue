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
        <DialogsModal
          text="참가하기"
          content="참가하시겠습니까?"
          @join="join"
        />
      </div>
    </div>
  </div>
</template>
<script lang="ts" setup>
import { onUnmounted, ref } from "vue";
import { useRouter, useRoute, onBeforeRouteLeave } from "vue-router";
import Video from "@/components/Community/Video.vue";
import Bear from "@/components/Community/Bear.vue";
import DialogsModal from "@/components/Community/DialogsModal.vue";

const router = useRouter();
const route = useRoute();
const createCheck = ref(false);

const join = function () {
  router.push({
    name: "communiting",
    params: { id: route.query.value as string },
  });
};

onBeforeRouteLeave((to, from, next) => {
  if (!to.path.startsWith("/main/community")) {
    console.log("Exiting /community path");
    // 원하는 코드를 이곳에 추가
  }
  next();
});
</script>
<style scoped>
.btn {
  position: absolute;
  margin-left: 100%;
  width: 200px;
  height: 100px;
}
.bear-frame {
  width: 100%;
  height: 700px;
  margin-top: 5%;
  z-index: -1;
}
.bear {
  position: relative;
  width: 697px;
  height: 700px;
  margin: auto;
}
.video-frame {
  position: absolute;
  width: 100%;
  height: 430px;
  top: 170px;
  z-index: 1;
}
.video {
  position: relative;
  margin: auto;
  width: 410px;
}
</style>
