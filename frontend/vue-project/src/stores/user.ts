import { ref } from "vue";
import { defineStore } from "pinia";
import apiClient from "@/loginController/verification"; // Axios 인스턴스 import
import { useRouter } from "vue-router";

const REST_USER_API = "https://apitest.pianobear.kr/api/v1/users/";
const REST_AUTH_API = "https://apitest.pianobear.kr/api/v1/auth/";

export const useUserStore = defineStore("user", () => {
  const user = ref({
    id: "",
    email: "",
    name: "",
    gender: "",
    birthday: "",
    password: "",
    statusMessage: "",
  });

  const accessToken = ref(localStorage.getItem("accessToken") || "");
  const refreshToken = ref(sessionStorage.getItem("refreshToken") || "");

  const router = useRouter();

  const RegistUser = async () => {
    console.log(user.value);
    try {
      const formData = new FormData();
      formData.append("registerRequestDTO", JSON.stringify(user.value));
      console.log(formData);
      await apiClient.post(REST_AUTH_API + "register", formData);
      alert("이메일 인증을 완료해야 로그인이 가능합니다.");
      router.push("/login");
    } catch (e) {
      console.error(e);
    }
  };

  const CheckUserId = async (userId: string) => {
    try {
      await apiClient.get(REST_USER_API + "check-user-id?userId=" + userId);
    } catch (e) {
      console.error(e);
      throw new Error("Failed to check user id");
    }
  };

  const CheckUserEmail = async (email: string) => {
    try {
      await apiClient.get(REST_USER_API + "check-email?email=" + email);
    } catch (e) {
      console.error(e);
      throw new Error("Failed to check email");
    }
  };

  const LoginUser = async (id: string, password: string) => {
    try {
      const res = await apiClient.post(REST_AUTH_API + "login", {
        id: id,
        password: password,
      });
      SetAccessToken(res.data.accessToken);
      SetRefreshToken(res.data.refreshToken);
      router.push("/");
    } catch (e) {
      console.error(e);
      throw new Error("Failed to login");
    }
  };

  const LogoutUser = async (accessToken: string, refreshToken: string) => {
    try {
      await apiClient.post(REST_AUTH_API + "logout", {
        accessToken: accessToken,
        refreshToken: refreshToken,
      });
      user.value = {
        id: "",
        email: "",
        name: "",
        gender: "",
        birthday: "",
        password: "",
        statusMessage: "",
      };
    } catch (e) {
      console.error(e);
      throw new Error("Failed to logout");
    }
  };

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
    user,
    RegistUser,
    LogoutUser,
    CheckUserId,
    CheckUserEmail,
    LoginUser,
    SetAccessToken,
    SetRefreshToken,
    GetAccessToken,
    GetRefreshToken,
    RemoveToken,
  };
});
