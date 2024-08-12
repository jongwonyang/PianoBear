import { ref } from "vue";
import { defineStore } from "pinia";
import apiClient from "@/loginController/verification"; // Axios 인스턴스 import
import { useRouter } from "vue-router";

const REST_USER_API = import.meta.env.VITE_API_BASE_URL + "/users/";
const REST_AUTH_API = import.meta.env.VITE_API_BASE_URL + "/auth/";
const REST_PROFILE_API = import.meta.env.VITE_API_BASE_URL + "/profile/";

export type User = {
  id: string;
  email: string;
  name: string;
  gender: string;
  birthday: string;
  profilePic: string;
  statusMessage: string;
  authEmail: string;
  role: string;
};

export const useUserStore = defineStore("user", () => {
  const user = ref<User | {}>({});

  const isLoggedIn = ref(false);
  const accessToken = ref(localStorage.getItem("accessToken") || "");
  const refreshToken = ref(sessionStorage.getItem("refreshToken") || "");

  const router = useRouter();

  const RegistUser = async () => {
    console.log(user.value);
    try {
      const formData = new FormData();
      formData.append("registerRequestDTO", JSON.stringify(user.value));
      console.log(formData);
      await apiClient.post(REST_AUTH_API + "register", formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      });
      alert("이메일 인증을 완료해야 로그인이 가능합니다.");
      router.push("/login");
    } catch (e) {
      console.error(e);
    }
  };

  const CheckUserId = async (userId: string) => {
    return apiClient.get(REST_USER_API + "check-user-id?userId=" + userId);
  };

  const CheckUserEmail = async (email: string) => {
    return apiClient.get(REST_USER_API + "check-email?email=" + email);
  };

  const LoginUser = async (id: string, password: string) => {
    return apiClient.post(REST_AUTH_API + "login", {
      id: id,
      password: password,
    });
  };

  const GetUserInfo = async () => {
    try {
      return apiClient.get(REST_USER_API + "my-info");
    } catch (e) {
      console.error(e);
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
        profilePic: "",
        statusMessage: "",
        authEmail: "",
        role: "",
      };
      isLoggedIn.value = false;
    } catch (e) {
      console.error(e);
      throw new Error("Failed to logout");
    }
  };

  const PasswordReset = async (id: string, name: string, email: string) => {
    try {
      await apiClient.post(REST_AUTH_API + "password-reset", {
        id: id,
        name: name,
        email: email,
      });
    } catch (e) {
      console.error(e);
    }
  };

  const UpdateStatusMessage = async (statusMessage: string) => {
    try {
      await apiClient.put(REST_USER_API + "status-message", {
        statusMessage: statusMessage,
      });
    } catch (e) {
      console.error(e);
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

  const updateProfile = async (file: File): Promise<void> => {
    if (!file) {
      alert("변경할 사진을 선택해주세요!");
      return;
    }

    const formData = new FormData();
    formData.append("profilePic", file);

    try {
      const response = await apiClient.put(`${REST_PROFILE_API}photo`, formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      });
      console.log(response.data);
    } catch (error) {
      console.error(error);
    }
  };

  const updateName = async (newName: string) => {
    try {
      const response = await apiClient.put(`${REST_PROFILE_API}name`, null, {
        params: {
          newName: newName,
        },
      });
      console.log(response);
    } catch (error) {
      console.error(error);
    }
  };

  const updatePassword = async (oldPassword: string, newPassword: string) => {
    try {
      const response = await apiClient.put(`${REST_PROFILE_API}password`, {
        oldPassword: oldPassword,
        newPassword: newPassword,
      });
      console.log(response);
      return response.data;
    } catch (error) {
      console.error(error);
    }
  };

  return {
    user,
    isLoggedIn,
    RegistUser,
    LogoutUser,
    CheckUserId,
    CheckUserEmail,
    LoginUser,
    PasswordReset,
    UpdateStatusMessage,
    SetAccessToken,
    SetRefreshToken,
    GetAccessToken,
    GetRefreshToken,
    RemoveToken,
    GetUserInfo,
    updateProfile,
    updateName,
    updatePassword,
  };
});
