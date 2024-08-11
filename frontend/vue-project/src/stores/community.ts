// stores/openvidu.ts
import { defineStore } from "pinia";
import { ref } from "vue";
import axios from "axios";
import { OpenVidu, Publisher, Session, StreamManager } from "openvidu-browser";
import apiClient from "@/loginController/verification";
import { shallowRef } from "vue";
import { useUserStore, type User } from "./user";
import { useRouter } from "vue-router";
import { da } from "vuetify/locale";

const APPLICATION_SERVER_URL =
  import.meta.env.VITE_API_BASE_URL + "/community/";

export const useOpenviduStore = defineStore("openvidu", () => {
  let OV: OpenVidu | null = null;
  let session: Session | null = null;
  let mainStreamManager: StreamManager | null = null;
  let publisher = shallowRef<Publisher | null>(null);
  let subscribers = ref<any>([]);
  let isInitialized = ref<boolean>(false);
  let isAudioActive = ref<boolean>(false);
  let isVideoActive = ref<boolean>(false);

  const router = useRouter();

  const chatHistory = ref<{ sender: string; content: string }[]>([]);

  const isPlay = ref(true);

  const emoteMap = ref(new Map());

  const userStore = useUserStore();

  const initOpenvidu = () => {
    OV = new OpenVidu();
    publisher.value = OV.initPublisher(undefined, {
      audioSource: undefined,
      videoSource: undefined,
      publishAudio: true,
      publishVideo: true,
      resolution: "640x480",
      frameRate: 30,
      insertMode: "APPEND",
      mirror: true,
    });
    isInitialized.value = true;
  };

  const deinitOpenvidu = () => {
    leaveSession();
  };

  const joinSession = async (sessionId: string) => {
    if (OV == null) {
      initOpenvidu();
    }

    isPlay.value = true;
    session = OV!.initSession();

    session.on("streamCreated", ({ stream }) => {
      const subscriber = session!.subscribe(stream, undefined);
      subscribers.value.push(subscriber);
    });

    session.on("streamDestroyed", ({ stream }) => {
      const index = subscribers.value.findIndex(
        (sub: any) => sub.stream.streamId == stream.streamId
      );
      console.log(index);
      if (index >= 0) {
        subscribers.value.splice(index, 1);
      }
    });

    session.on("exception", ({ data }) => {
      console.warn(data);
    });

    // 메시지 수신
    session.on("signal:chat", (event) => {
      const message = event.data;
      console.log("Message received: ", JSON.parse(message as string));
      chatHistory.value.push(JSON.parse(message as string));
      // 메시지를 화면에 표시하는 로직을 추가
    });

    // 이모트 수신
    session.on("signal:emote", (event) => {
      const message = event.data;
      console.log("Emote received: ", JSON.parse(message as string));
      let data = JSON.parse(message as string);
      if (emoteMap.value.has(data.senderId)) {
        let expired = emoteMap.value.get(data.senderId);
        clearTimeout(expired.timeout);
      }
      data.timeout = setTimeout(() => {
        emoteMap.value.delete(data.senderId);
      }, 1000);
      console.log(data);
      emoteMap.value.set(data.senderId, data);
    });

    try {
      const token = await createToken(sessionId);
      await session.connect(token, {
        clientData: {
          name: (userStore.user as User).name,
          id: (userStore.user as User).id,
        },
      });

      console.log(token);

      mainStreamManager = publisher.value;
      session.publish(publisher.value!);
    } catch (error: any) {
      console.error(
        "There was an error connecting to the session:",
        error.code,
        error.message
      );
      alert("방에 입장할 수 없습니다.");
      router.push({ name: "community" });
    }
  };

  const leaveSession = () => {
    if (publisher.value) {
      publisher.value.stream
        .getMediaStream()
        .getTracks()
        .forEach((track) => track.stop());
    }
    if (session) {
      session.disconnect();
    }

    OV = null;
    session = null;
    mainStreamManager = null;
    publisher.value = null;
    subscribers.value = [];
    chatHistory.value = [];
    isInitialized.value = false;
  };

  const updateMainVideoStreamManager = (streamManager: StreamManager) => {
    if (mainStreamManager !== streamManager) {
      mainStreamManager = streamManager;
    }
  };

  const createSession = async (roomSetting: RoomSetting) => {
    const response = await apiClient.post(APPLICATION_SERVER_URL + "sessions", {
      sessionTitle: roomSetting.sessionTitle,
      invitationMessage: roomSetting.invitationMessage,
      description: roomSetting.description,
    });
    return response.data; // The sessionId
  };

  const createToken = async (sessionId: string) => {
    const response = await apiClient.post(
      `${APPLICATION_SERVER_URL}sessions/${sessionId}/connect`,
      {}
    );
    return response.data; // The token
  };

  // 메시지 전송
  function sendMessage(message: string) {
    if (!session) {
      console.log("You are not in session yet.");
    } else {
      session
        .signal({
          type: "chat",
          data: JSON.stringify({
            sender: (userStore.user as User).name,
            content: message,
          }),
        })
        .then(() => {
          console.log("Message sent successfully");
        })
        .catch((error) => {
          console.error("There was an error sending the message:", error);
        });
    }
  }

  // 메시지 전송
  function sendEmote(message: string) {
    if (!session) {
      console.log("You are not in session yet.");
    } else {
      session
        .signal({
          type: "emote",
          data: JSON.stringify({
            sender: (userStore.user as User).name,
            senderId: (userStore.user as User).id,
            content: message,
          }),
        })
        .then(() => {
          console.log("Emote sent successfully");
        })
        .catch((error) => {
          console.error("There was an error sending the Emote:", error);
        });
    }
  }

  // 친구 초대
  function inviteFriend(invitee: string) {
    if (!session) {
      console.log("You are not in session yet.");
    } else {
      apiClient.post(
        `${APPLICATION_SERVER_URL}sessions/${session.sessionId}/invite/${invitee}`,
        {}
      );
    }
  }

  const getInvitedFriends = () => {
    return apiClient.get(
      `${APPLICATION_SERVER_URL}sessions/${session?.sessionId}/participants`
    );
  };

  return {
    publisher,
    subscribers,
    isPlay,
    isInitialized,
    chatHistory,
    isAudioActive,
    isVideoActive,
    emoteMap,
    joinSession,
    leaveSession,
    createSession,
    updateMainVideoStreamManager,
    initOpenvidu,
    deinitOpenvidu,
    sendMessage,
    sendEmote,
    inviteFriend,
    getInvitedFriends,
  };
});

export type RoomSetting = {
  sessionTitle: string;
  invitationMessage: string;
  description: string;
};
