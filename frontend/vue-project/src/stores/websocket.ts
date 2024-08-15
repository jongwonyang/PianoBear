import { defineStore } from "pinia";
import { ref } from "vue";
import apiClient from "@/loginController/verification"; // Axios 인스턴스 import
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";
import { useUserStore } from "./user";
import { he } from "vuetify/locale";

const REST_CHAT_API = import.meta.env.VITE_API_BASE_URL + "/ws/";
const REST_NOTIFICATION_API =
  import.meta.env.VITE_API_BASE_URL + "/notifications/";

export type MessageDTO = {
  id: number;
  chatRoomId: number;
  senderId: string;
  receiverId: string;
  content: string;
  timestamp: string;
};

export const useWebSocketStore = defineStore("websocket", () => {
  let stompClient: Client | null = null;
  const connected = ref(false);
  const connecting = ref(false);
  let connectPromise: Promise<Client> | null = null;
  const currentChatRoomId = ref<number | null>(null);
  const messages = ref<MessageDTO[]>([]);
  const userStore = useUserStore();

  const notificationCount = ref(0);
  const notifications = ref<any[]>([]);

  const accessToken = ref(userStore.GetAccessToken());

  const GetNotificationList = async () => {
    try {
      const response = await apiClient.get(REST_NOTIFICATION_API);
      console.log("response.data", response.data);
      for (let i = 0; i < response.data.length; i++) {
        response.data[i].content = JSON.parse(response.data[i].content);
      }
      notifications.value = response.data;
      console.log("notifications.value", notifications.value);
      return response.data;
    } catch (error) {
      console.error("Error fetching notifications:", error);
    }
  };

  const GetNotificationCount = async () => {
    try {
      const response = await apiClient.get(REST_NOTIFICATION_API + "count");
      notificationCount.value = response.data;
    } catch (error) {
      console.error("Error fetching notification count:", error);
    }
  };

  const DeleteNotification = async (id: any) => {
    try {
      await apiClient.delete(`${REST_NOTIFICATION_API}${id}`);
      notifications.value = notifications.value.filter(
        (notification) => notification.id !== id
      );
      notificationCount.value -= 1;
    } catch (error) {
      console.error("Error deleting notification:", error);
    }
  };

  const ClearNotifications = async () => {
    try {
      await apiClient.delete(REST_NOTIFICATION_API + "clear");
      notifications.value = [];
      notificationCount.value = 0;
    } catch (error) {
      console.error("Error clearing notifications:", error);
    }
  };

  async function connectWebSocket() {
    if (connected.value) {
      return Promise.resolve(stompClient);
    }

    if (connecting.value) {
      return connectPromise;
    }

    const headers = {
      Authorization: "Bearer " + accessToken.value,
    };

    connectPromise = new Promise((resolve, reject) => {
      connecting.value = true;
      const socket = new SockJS(import.meta.env.VITE_API_BASE_URL + "/ws");
      const client = new Client({
        webSocketFactory: () => socket,
        connectHeaders: headers,
        onConnect: () => {
          stompClient = client;
          connected.value = true;
          connecting.value = false;

          //알림 구독
          subscribeToNotifications();
          console.log("Connected!");
          resolve(stompClient);
        },
        onStompError: (error) => {
          console.error("WebSocket 연결 오류:", error);
          connecting.value = false;
          connected.value = false;
          reject(error.headers["message"]);
        },
        onWebSocketClose: () => {
          console.log("WebSocket 연결이 종료되었습니다.");
          connected.value = false;
        },
      });

      client.activate();
    });
  }

  function disconnectWebSocket() {
    console.log(stompClient);
    if (stompClient !== null) {
      console.log("LETS DISCONNECT!!!!!!!!!!!!!!!!!!!!!!!");
      stompClient.deactivate().then(() => {
        connected.value = false;
        stompClient = null;
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
        await connectWebSocket();
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
    if (stompClient && connected.value) {
      const message: Partial<MessageDTO> = {
        content: content,
        receiverId: receiverId,
        senderId: (userStore.user as { id: string }).id,
      };

      const headers = {
        Authorization: "Bearer " + accessToken.value,
      };
      stompClient.publish({
        destination: `/app/sendMessage`,
        body: JSON.stringify(message),
        headers: headers,
      });

      // 로컬 메시지 리스트에 바로 추가
      // messages.value.push({
      //   ...message,
      //   id: Date.now(), // 임시 ID
      //   timestamp: new Date().toISOString(),
      // } as MessageDTO);
    }
  };

  // 채팅방 메시지 수신 구독
  const subscribeToChatRoom = (
    chatRoomId: number,
    callback: (message: MessageDTO) => void
  ) => {
    if (stompClient && connected.value) {
      const subscription = stompClient.subscribe(
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

  // 알림 구독
  const subscribeToNotifications = () => {
    if (stompClient && connected.value) {
      const subscription = stompClient.subscribe(
        `/user/queue/notifications`,
        (message) => {
          const notification = JSON.parse(message.body);
          GetNotificationList();
          GetNotificationCount();
          console.log("New notification:", notification);
        }
      );

      // 구독 해제를 위한 반환 함수
      return () => subscription.unsubscribe();
    }
  };

  return {
    connected,
    notifications,
    notificationCount,
    GetNotificationList,
    GetNotificationCount,
    DeleteNotification,
    ClearNotifications,
    connectWebSocket,
    disconnectWebSocket,
    enterChatRoom,
    sendMessage,
    subscribeToChatRoom,
    subscribeToNotifications,
  };
});
