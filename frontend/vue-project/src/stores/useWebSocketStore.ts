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
            console.log("connecting...");
            client.subscribe(
              "/sub",
              (frame) => {
                connected.value = true;
                stompClient.value = client;
                console.log("Connected: " + frame);
              });
          },
          onStompError: (error) => {
            console.log("Error connecting: " + error);
            connected.value = false;
          }
        });
        

        client.activate();
    }

    function disconnectWebSocket() {
        if (stompClient.value !== null) {
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