import { ref } from "vue";
import { defineStore } from "pinia";
import apiClient from "@/loginController/verification"; // Axios 인스턴스 import
import { useRouter } from "vue-router";

const REST_USER_API = "http://192.168.31.37:7000/api/v1/users/";
const REST_AUTH_API = "http://192.168.31.37:7000/api/v1/auth/";

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

  return {
    user,
    RegistUser,
  };
});

export const CheckUserId = function (userId: string) {
  console.log(userId);
  return apiClient.get(REST_USER_API + "check-user-id?userId=" + userId);
};

export const CheckUserEmail = function (email: string) {
  return apiClient.get(REST_USER_API + "check-email?email=" + email);
};

export const LoginUser = function (id: string, password: string) {
  return apiClient.post(REST_AUTH_API + "login", {
    id: id,
    password: password,
  });
};
