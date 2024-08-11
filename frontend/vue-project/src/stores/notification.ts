import { ref } from "vue";
import { defineStore } from "pinia";
import { useUserStore } from "./user";
import apiClient from "@/loginController/verification"; // Axios 인스턴스 import
import EventSourcePolyFill from "event-source-polyfill";
import { useRouter } from "vue-router";
import { NativeEventSource, EventSourcePolyfill } from "event-source-polyfill";

const REST_NOTIFICATION_API =
  import.meta.env.VITE_API_BASE_URL + "/notifications/";

export const useNotificationStore = defineStore("notification", () => {
  const notifications = ref([]);
  const notificationCount = ref(0);
  const userStore = useUserStore();

  const EventSource = EventSourcePolyfill;
  const accessToken = ref(userStore.GetAccessToken());
  console.log("accessToken", accessToken.value);

  // const GetNotificationList = async () => {
  //   try {
  //     const response = await apiClient.get(REST_NOTIFICATION_API);
  //     console.log("response.data", response.data);
  //     notifications.value = response.data;
  //     console.log("notifications.value", notifications.value);
  //   } catch (error) {
  //     console.error("Error fetching notifications:", error);
  //   }
  // };

  const GetNotificationCount = async () => {
    try {
      const response = await apiClient.get(REST_NOTIFICATION_API + "count");
      notificationCount.value = response.data;
    } catch (error) {
      console.error("Error fetching notification count:", error);
    }
  };

  const DeleteNotification = async (id) => {
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

  const SubscribeToNotifications = () => {
    const source = new EventSource(REST_NOTIFICATION_API + "subscribe", {
      headers: {
        Authorization: `Bearer ${accessToken.value}`,
      },
      withCredentials: true,
    });

    // const eventSource = new EventSourcePolyFill(
    //   REST_NOTIFICATION_API + "subscribe",
    //   {
    //     headers: {
    //       Authorization: `Bearer ${accessToken.value}`,
    //     },
    //     withCredentials: true,
    //   }
    // );

    source.onopen = () => {
      console.log("SSE connection opened");
    };

    source.addEventListener("notification", (event) => {
      console.log("event.data", event.data);
      const newNotification = JSON.parse(event.data);
      newNotification.content = JSON.parse(newNotification.content);
      notifications.value.push(newNotification);
      notificationCount.value += 1;
      console.log("New notification:", newNotification);
      console.log("notificationCount.value", notificationCount.value);
    });

    source.onerror = (error) => {
      console.error("SSE connection error:", error);
      source.close();
    };
  };

  return {
    notifications,
    notificationCount,
    // GetNotificationList,
    GetNotificationCount,
    DeleteNotification,
    ClearNotifications,
    SubscribeToNotifications,
  };
});
