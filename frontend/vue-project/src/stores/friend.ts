import { ref } from "vue";
import { defineStore } from "pinia";
import apiClient from "@/loginController/verification"; // Axios 인스턴스 import
import { useRouter } from "vue-router";

const REST_FRIEND_API = import.meta.env.VITE_API_BASE_URL + "/friends/";

export const useFriendStore = defineStore("friend", () => {
  const GetFriendList = async () => {
    return apiClient.get(REST_FRIEND_API);
  };

  const GetFriendInfo = async (friendId: string) => {
    return apiClient.get(REST_FRIEND_API + "search/" + friendId);
  };

  const SendFriendRequest = async (receiverId: string) => {
    return apiClient.post(REST_FRIEND_API + "requests/send/" + receiverId);
  };

  const AcceptFriendRequest = async (requestId: string) => {
    return apiClient.post(
      REST_FRIEND_API + "requests/received/" + requestId + "/accept"
    );
  };

  const RejectFriendRequest = async (requestId: string) => {
    return apiClient.post(
      REST_FRIEND_API + "requests/received/" + requestId + "/reject"
    );
  };

  const SentRequestsList = async () => {
    return apiClient.get(REST_FRIEND_API + "requests/sent");
  };

  const ReceivedRequestsList = async () => {
    return apiClient.get(REST_FRIEND_API + "requests/received");
  };

  const IsSentRequest = async (receiverId: string) => {
    return apiClient.get(REST_FRIEND_API + "requests/is-sent/" + receiverId);
  };

  const RemoveFriend = async (friendId: string) => {
    return apiClient.delete(REST_FRIEND_API + friendId);
  };

  return {
    GetFriendList,
    GetFriendInfo,
    SendFriendRequest,
    AcceptFriendRequest,
    RejectFriendRequest,
    SentRequestsList,
    ReceivedRequestsList,
    IsSentRequest,
    RemoveFriend,
  };
});
