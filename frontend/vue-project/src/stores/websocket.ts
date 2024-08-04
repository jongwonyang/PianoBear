import { defineStore } from "pinia";
import { ref } from "vue";
import { API_BASE_URL } from "@/api";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";

export const useWebSocketStore = defineStore("websocket", () => {
  const stompClient = ref<Client | null>(null);
  const connected = ref(false);

  function connectWebSocket() {
    if (stompClient.value && connected.value) {
      return;
    }

    const headers = {
      Authorization: "Bearer " + localStorage.getItem("accessToken")
    }

    const socket = new SockJS(API_BASE_URL + "/ws");
    const client = new Client({
      webSocketFactory: () => socket,
      connectHeaders: headers,
      onConnect: () => {
        stompClient.value = client;
        connected.value = true;
        console.log("Connected!")
      }
    });


    client.activate();
  }

  function disconnectWebSocket() {
    console.log(stompClient.value);
    if (stompClient.value !== null) {
      console.log("LETS DISCONNECT!!!!!!!!!!!!!!!!!!!!!!!");
      stompClient.value.deactivate()
        .then(() => {
          connected.value = false;
          stompClient.value = null;
          console.log('Disconnected');
        });
    }
  }

  return {
    stompClient,
    connected,
    connectWebSocket,
    disconnectWebSocket
  };
});