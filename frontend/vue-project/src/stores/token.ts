import { ref } from "vue";
import { defineStore } from "pinia";

export const useTokenStore = defineStore("token", () => {
  const accessToken = ref(localStorage.getItem("accessToken") || "");
  const refreshToken = ref(sessionStorage.getItem("refreshToken") || "");

  const SetAccessToken = (token: string) => {
    accessToken.value = token;
    localStorage.setItem("accessToken", token);
  };

  const SetRefreshToken = (token: string) => {
    refreshToken.value = token;
    sessionStorage.setItem("refreshToken", token);
  };

  const GetAccessToken = () => {
    return accessToken.value || localStorage.getItem("accessToken");
  };

  const GetRefreshToken = () => {
    return refreshToken.value || sessionStorage.getItem("refreshToken");
  };

  const RemoveToken = () => {
    accessToken.value = "";
    refreshToken.value = "";
    localStorage.removeItem("accessToken");
    sessionStorage.removeItem("refreshToken");
  };

  return {
    accessToken,
    refreshToken,
    SetAccessToken,
    SetRefreshToken,
    GetAccessToken,
    GetRefreshToken,
    RemoveToken,
  };
});
