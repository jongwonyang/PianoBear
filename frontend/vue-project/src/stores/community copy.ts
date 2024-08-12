// // stores/openvidu.ts
// import { defineStore } from "pinia";
// import { ref } from "vue";
// import axios from "axios";
// import { OpenVidu, Publisher, Session, StreamManager } from "openvidu-browser";
// import apiClient from "@/loginController/verification";
// import { shallowRef } from "vue";

// const APPLICATION_SERVER_URL = "https://test0.sgr.cspark.kr/";

// const OPENVIDU_SERVER_URL: String = "https://openvidu.pianobear.kr/openvidu";

// export const useOpenviduStore = defineStore("openvidu", () => {
//   let OV: OpenVidu | null = null;
//   let session: Session | null = null;
//   let mainStreamManager: StreamManager | null = null;
//   let publisher = shallowRef<Publisher | null>(null);
//   let subscribers = ref<StreamManager[]>([]);

//   const myUserName = ref("Participant" + Math.floor(Math.random() * 100));

//   const joinSession = async (sessionId: string) => {
//     OV = new OpenVidu();
//     session = OV.initSession();

//     session.on("streamCreated", ({ stream }) => {
//       const subscriber = session!.subscribe(stream, undefined);
//       subscribers.value.push(subscriber);
//     });

//     session.on("streamDestroyed", ({ stream }) => {
//       const index = subscribers.value.findIndex(
//         (sub) => sub.stream.streamId == stream.streamId
//       );
//       console.log(index);
//       if (index >= 0) {
//         subscribers.value.splice(index, 1);
//       }
//     });

//     session.on("exception", ({ data }) => {
//       console.warn(data);
//     });

//     try {
//       console.log(sessionId);
//       const token = await createToken(sessionId);
//       await session.connect(token);

//       console.log(token);

//       publisher.value = OV.initPublisher(undefined, {
//         audioSource: undefined,
//         videoSource: undefined,
//         publishAudio: true,
//         publishVideo: true,
//         resolution: "640x480",
//         frameRate: 30,
//         insertMode: "APPEND",
//         mirror: false,
//       });

//       mainStreamManager = publisher.value;
//       session.publish(publisher.value);
//     } catch (error: any) {
//       console.error(
//         "There was an error connecting to the session:",
//         error.code,
//         error.message
//       );
//     }
//   };

//   const leaveSession = () => {
//     if (session) session.disconnect();

//     OV = null;
//     session = null;
//     mainStreamManager = null;
//     publisher.value = null;
//     subscribers.value = [];
//   };

//   const updateMainVideoStreamManager = (streamManager: StreamManager) => {
//     if (mainStreamManager !== streamManager) {
//       mainStreamManager = streamManager;
//     }
//   };

//   const createSession = async () => {
//     const response = await apiClient.post(
//       APPLICATION_SERVER_URL + "api/v1/community/sessions"
//     );
//     return response.data; // The sessionId
//   };

//   const createToken = async (sessionId: string) => {
//     const response = await apiClient.post(
//       `${APPLICATION_SERVER_URL}api/v1/community/sessions/${sessionId}/connections`,
//       {}
//     );
//     return response.data; // The token
//   };

//   return {
//     publisher,
//     subscribers,
//     joinSession,
//     leaveSession,
//     createSession,
//     updateMainVideoStreamManager,
//   };
// });
