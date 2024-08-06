// stores/openvidu.ts
import { defineStore } from "pinia";
import { ref } from "vue";
import axios from "axios";
import { OpenVidu, Publisher, Session, StreamManager } from "openvidu-browser";
import apiClient from "@/loginController/verification";
import { shallowRef } from "vue";
import { useUserStore } from "./user";

const APPLICATION_SERVER_URL = "https://test0.sgr.cspark.kr/";

const OPENVIDU_SERVER_URL: String = "https://openvidu.pianobear.kr/openvidu";

export const useOpenviduStore = defineStore("openvidu", () => {
  let OV: OpenVidu | null = null;
  let session: Session | null = null;
  let mainStreamManager: StreamManager | null = null;
  let publisher = shallowRef<Publisher | null>(null);
  let subscribers = ref<any>([]);
  let isInitialized = ref<boolean>(false);

  const chatHistory = ref<{ sender: string; content: string }[]>([]);

  const isPlay = ref(true);

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

    try {
      const token = await createToken(sessionId);
      await session.connect(token, { name: userStore.user.name });

      console.log(token);

      mainStreamManager = publisher.value;
      session.publish(publisher.value!);
    } catch (error: any) {
      console.error(
        "There was an error connecting to the session:",
        error.code,
        error.message
      );
    }
  };

  const leaveSession = () => {
    if (publisher.value) {
      publisher.value.stream
        .getMediaStream()
        .getTracks()
        .forEach((track) => track.stop());

      if (session) {
        session.unpublish(publisher.value);
      }
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

  const createSession = async () => {
    const response = await apiClient.post(
      APPLICATION_SERVER_URL + "api/v1/community/sessions"
    );
    return response.data; // The sessionId
  };

  const createToken = async (sessionId: string) => {
    const response = await apiClient.post(
      `${APPLICATION_SERVER_URL}api/v1/community/sessions/${sessionId}/connections`,
      {}
    );
    return response.data; // The token
  };

  // 메시지 전송
  function sendMessage(sender: string, message: string) {
    if (!session) {
      console.log("You are not in session yet.");
    } else {
      session
        .signal({
          type: "chat",
          data: JSON.stringify({
            sender: "sender",
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

  return {
    publisher,
    subscribers,
    isPlay,
    isInitialized,
    chatHistory,
    joinSession,
    leaveSession,
    createSession,
    updateMainVideoStreamManager,
    initOpenvidu,
    deinitOpenvidu,
    sendMessage,
  };
});
