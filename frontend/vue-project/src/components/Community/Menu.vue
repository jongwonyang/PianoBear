<template>
  <div>
    <v-menu location="end">
      <template v-slot:activator="{ props }" >
        <v-btn
          v-bind="props"
          icon="mdi-dots-horizontal"
          :width="25"
          :height="25"
        >
        </v-btn>
      </template>
      <v-list lines="one">
        <v-list-item>
          <v-btn v-if="!muted" @click="toggleAudio()"> 음소거 </v-btn>
          <v-btn v-else @click="toggleAudio()"> 음소거 해제 </v-btn>
        </v-list-item>
      </v-list>
    </v-menu>
  </div>
</template>
<script lang="ts" setup>
import { ref, defineProps } from "vue";

const props = defineProps({
  muted: Boolean,
});

const emit = defineEmits(["toggle-audio"]);

function toggleAudio() {
  emit("toggle-audio");
}
</script>

<style scoped></style>
