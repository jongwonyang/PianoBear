<template>
  <video
    autoplay
    style="width: 100%; height: 100%; object-fit: cover"
    ref="d"
  ></video>
</template>

<script setup>
import { ref, defineProps, onMounted } from "vue";

const d = ref();

const props = defineProps(["streamManager"]);

onMounted(() => {
  props.streamManager.addVideoElement(d.value);
});
</script>
