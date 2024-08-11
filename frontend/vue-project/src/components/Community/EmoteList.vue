<template>
  <div>
    <v-menu location="top center">
      <template v-slot:activator="{ props }">
        <v-btn v-bind="props" icon="mdi-emoticon"> </v-btn>
      </template>

      <v-list class="px-5 py-5">
        <v-row style="width: 550px">
          <v-col v-for="(item, index) in emotions" :key="index" cols="2">
            <button class="emote-button" @click="sendEmote(item)">
              {{ item }}
            </button>
          </v-col>
        </v-row>
      </v-list>
    </v-menu>
  </div>
</template>
<script lang="ts" setup>
import { ref } from "vue";
import { useOpenviduStore } from "@/stores/community";

const emotions = ref([
  "❤️",
  "😊",
  "😘",
  "😍",
  "👍",
  "🙌",
  "✌️",
  "😎",
  "🤣",
  "😭",
  "😱",
  "👻",
]);
const communityStore = useOpenviduStore();

function sendEmote(emote: string) {
  communityStore.sendEmote(emote);
}
</script>
<style scoped>
.emote-button {
  font-size: 40px;
  text-align: center;
  transition: font-size 0.3s;
  width: 100%;
  height: 50px;
  line-height: 40px;
}

.emote-button:hover {
  font-size: 50px;
}
</style>
