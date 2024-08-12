import { defineStore } from "pinia";
import { ref } from "vue";
import apiClient from "@/loginController/verification"; // Axios 인스턴스 import
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";
import { useUserStore } from "./user";

const REST_CHAT_API = import.meta.env.VITE_API_BASE_URL + "/ws/";

export type MessageDTO = {
  id: number;
  chatRoomId: number;
  senderId: string;
  receiverId: string;
  content: string;
  timestamp: string;
};

export const useWebSocketStore = defineStore("websocket", () => {
  const stompClient = ref<Client | null>(null);
  const connected = ref(false);
  const currentChatRoomId = ref<number | null>(null);
  const messages = ref<MessageDTO[]>([]);
  const userStore = useUserStore();

  function connectWebSocket() {
    if (stompClient.value && connected.value) {
      return;
    }

    const headers = {
      Authorization: "Bearer " + sessionStorage.getItem("accessToken"),
    };

    const socket = new SockJS(import.meta.env.VITE_API_BASE_URL + "/ws");
    const client = new Client({
      webSocketFactory: () => socket,
      connectHeaders: headers,
      onConnect: () => {
        stompClient.value = client;
        connected.value = true;
        console.log("Connected!");
        // 현재 채팅방이 있다면 재연결 시 자동 구독
        // if (currentChatRoomId.value) {
        //   subscribeToChatRoom(
        //     currentChatRoomId.value,
        //     (message: MessageDTO) => {
        //       messages.value.push(message);
        //     }
        //   );
        // }
      },
      onStompError: (error) => {
        console.error("WebSocket 연결 오류:", error);
      },
      onWebSocketClose: () => {
        console.log("WebSocket 연결이 종료되었습니다.");
        connected.value = false;
      },
    });

    client.activate();
  }

  function disconnectWebSocket() {
    console.log(stompClient.value);
    if (stompClient.value !== null) {
      console.log("LETS DISCONNECT!!!!!!!!!!!!!!!!!!!!!!!");
      stompClient.value.deactivate().then(() => {
        connected.value = false;
        stompClient.value = null;
        console.log("Disconnected");
      });
    }
  }

  const enterChatRoom = async (friendId: string) => {
    try {
      const response = await apiClient.get(REST_CHAT_API + `room/${friendId}`);
      currentChatRoomId.value = response.data.id;
      messages.value = response.data.messages;

      // 웹소켓 연결 후 구독
      if (!connected.value) {
        connectWebSocket();
      }

      // subscribeToChatRoom(currentChatRoomId.value, (message: MessageDTO) => {
      //   messages.value.push(message);
      // });

      return response.data;
    } catch (error) {
      console.error("채팅방 입장 실패:", error);
      throw error;
    }
  };

  // 메시지 전송
  const sendMessage = (receiverId: string, content: string) => {
    if (stompClient.value && connected.value) {
      const message: Partial<MessageDTO> = {
        content: content,
        receiverId: receiverId,
        senderId: userStore.user.id,
      };

      console.log("메시지 전송:", message);
      console.log("제이슨변환:", JSON.stringify(message));
      stompClient.value.publish({
        destination: `/topic/chat/${currentChatRoomId.value}`,
        body: JSON.stringify(message),
      });

      // 로컬 메시지 리스트에 바로 추가
      messages.value.push({
        ...message,
        id: Date.now(), // 임시 ID
        timestamp: new Date().toISOString(),
      } as MessageDTO);
    }
  };

  // 채팅방 메시지 수신 구독
  const subscribeToChatRoom = (
    chatRoomId: number,
    callback: (message: MessageDTO) => void
  ) => {
    if (stompClient.value && connected.value) {
      const subscription = stompClient.value.subscribe(
        `/topic/chat/${chatRoomId}`,
        (message) => {
          const receivedMessage = JSON.parse(message.body) as MessageDTO;
          callback(receivedMessage);
        }
      );

      // 구독 해제를 위한 반환 함수
      return () => subscription.unsubscribe();
    }
  };

  return {
    stompClient,
    connected,
    connectWebSocket,
    disconnectWebSocket,
    enterChatRoom,
    sendMessage,
    subscribeToChatRoom,
  };
});
