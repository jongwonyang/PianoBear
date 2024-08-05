import { ref } from "vue";
import { defineStore } from "pinia";
import apiClient from "@/loginController/verification"; // Axios 인스턴스 import
import { useRouter } from "vue-router";
import { API_BASE_URL } from "@/api";

const REST_FRIEND_API = API_BASE_URL + "/friends/";

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

  return {
    GetFriendList,
    GetFriendInfo,
    SendFriendRequest,
  };
});
