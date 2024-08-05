<template>
  <div class="chat" :class="props.chat">
    <h2>채팅창</h2>
    <div></div>

    <div class="chat-input">
      <div class="chat-input-components">
        <md-outlined-text-field
          label="메시지"
          type="text"
          class="chat-input-text"
        ></md-outlined-text-field>
      </div>
      <div class="chat-input-components">
        <v-btn icon="mdi-send" />
      </div>
    </div>
  </div>
</template>
<script lang="ts" setup>
import { defineProps } from "vue";

const props = defineProps({
  chat: String,
});
</script>
<style scoped>
.chat {
  display: grid;
  grid-template-rows: auto auto 10%;
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
}

.send-btn {
  border-radius: 50%;
  height: 20px;
  width: 20px;
}
</style>
