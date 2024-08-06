<template>
  <div class="chat" :class="props.chat">
    <h2>채팅창</h2>
    <div class="chat-history pb-4">
      <div
        v-for="(msg, idx) in openviduStore.chatHistory.slice().reverse()"
        :key="idx"
      >
        <div class="px-2">
          <b>{{ msg.sender }}</b>
        </div>
        <div class="chat-message-content mx-1 my-2 px-2 py-1">
          {{ msg.content }}
        </div>
      </div>
    </div>

    <div class="chat-input">
      <div class="chat-input-components">
        <md-outlined-text-field
          label="메시지"
          type="text"
          class="chat-input-text"
          :value="message"
          @input="setMessage"
          @keyup.enter="sendMessage"
        ></md-outlined-text-field>
      </div>
      <div class="chat-input-components">
        <v-btn icon="mdi-send" @click="sendMessage" />
      </div>
    </div>
  </div>
</template>
<script lang="ts" setup>
import { useOpenviduStore } from "@/stores/community";
import { useUserStore } from "@/stores/user";
import { defineProps, ref } from "vue";

const message = ref("");

const openviduStore = useOpenviduStore();
const userStore = useUserStore();

function sendMessage() {
  openviduStore.sendMessage(userStore.user.name, message.value);
  message.value = "";
}

const setMessage = (e: any) => {
  message.value = e.target.value;
};

const props = defineProps({
  chat: String,
});
</script>
<style scoped>
.chat {
  display: grid;
  grid-template-rows: 10% auto 10%;
  padding: 10px;
}
.chat1 {
  width: 350px;
  height: 630px;
  margin: 10px 0px;
  border-radius: 20px;
  background-color: #f0fff0;
}
.chat2 {
  width: 350px;
  height: 470px;
  margin: 10px 0px;
  border-radius: 20px;
  background-color: #f0fff0;
}

.chat-input {
  display: grid;
  grid-template-columns: 80% 20%;
  padding: auto;
}

.chat-input-components {
  display: flex;
  justify-content: center;
  align-items: center;
}

.chat-input-text {
  width: 100%;
  background-color: #fff8d8;
}

.send-btn {
  border-radius: 50%;
  height: 20px;
  width: 20px;
}

.chat-history {
  overflow-y: auto;
  display: flex;
  flex-direction: column-reverse;
}

.chat-message-content {
  background-color: #f5e5d1;

  border-radius: 5px;
}
</style>
