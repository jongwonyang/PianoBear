import { ref } from "vue";
import { defineStore } from "pinia";
import apiClient from "@/loginController/verification"; // Axios 인스턴스 import
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
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

export const useChattingStore = defineStore("chatting", () => {
  const stompClient = ref<Client | null>(null);
  const connected = ref(false);
  const currentChatRoomId = ref<number | null>(null);
  const messages = ref<MessageDTO[]>([]);
  const userStore = useUserStore();

  // WebSocket 연결 설정
  const connectWebSocket = () => {
    if (connected.value) return;

    const socket = new SockJS(REST_CHAT_API);
    stompClient.value = new Client({
      webSocketFactory: () => socket,
      onConnect: () => {
        connected.value = true;
        console.log("WebSocket 연결 성공");

        // 현재 채팅방이 있다면 재연결 시 자동 구독
        if (currentChatRoomId.value) {
          subscribeToChatRoom(
            currentChatRoomId.value,
            (message: MessageDTO) => {
              messages.value.push(message);
            }
          );
        }
      },
      onStompError: (error) => {
        console.error("WebSocket 연결 오류:", error);
      },
      onWebSocketClose: () => {
        console.log("WebSocket 연결이 종료되었습니다.");
        connected.value = false;
      },
    });
    stompClient.value.activate();
  };

  // WebSocket 연결 해제
  const disconnectWebSocket = () => {
    if (stompClient.value && connected.value) {
      stompClient.value.deactivate();
      connected.value = false;
      console.log("WebSocket 연결 해제");
    }
  };

  // 채팅방 입장
  const enterChatRoom = async (friendId: string) => {
    try {
      const response = await apiClient.get(REST_CHAT_API + `room/${friendId}`);
      currentChatRoomId.value = response.data.id;
      messages.value = response.data.messages;

      // 웹소켓 연결 후 구독
      if (!connected.value) {
        connectWebSocket();
      }

      subscribeToChatRoom(currentChatRoomId.value, (message: MessageDTO) => {
        messages.value.push(message);
      });

      return response.data;
    } catch (error) {
      console.error("채팅방 입장 실패:", error);
      throw error;
    }
  };

  // 메시지 전송
  const sendMessage = (chatRoomId: number, content: string) => {
    if (stompClient.value && connected.value) {
      const message: Partial<MessageDTO> = {
        chatRoomId: chatRoomId,
        content: content,
        senderId: userStore.user.id,
      };
      stompClient.value.publish({
        destination: `/topic/chat/${chatRoomId}`,
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
    connectWebSocket,
    disconnectWebSocket,
    enterChatRoom,
    sendMessage,
    subscribeToChatRoom,
    messages,
    currentChatRoomId,
  };
});
