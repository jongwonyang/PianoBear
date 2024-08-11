<template>
  <video
    autoplay
    style="width: 100%; height: 100%; object-fit: cover"
    ref="d"
    :muted="muted"
  ></video>
</template>

<script setup>
import { ref, defineProps, onMounted } from "vue";

const d = ref();

const props = defineProps({
  streamManager: Object,
  muted: Boolean,
});

onMounted(() => {
  props.streamManager.addVideoElement(d.value);
  d.value.addEventListener("contextmenu", function (event) {
    event.preventDefault(); // 우클릭 메뉴 비활성화
  });
});
</script>
