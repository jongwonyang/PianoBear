import { ref } from "vue";
import { defineStore } from "pinia";
import axios from "axios";
import { useRouter } from "vue-router";
const REST_USER_API = "https://apitest.pianobear.kr/api/v1/users/";

export const useUserStore = defineStore("user", () => {
  const user = ref({
    id: "",
    email: "",
    name: "",
    pw: "",
    pwCheck: "",
    gender: "",
    birth: "",
  });

  const router = useRouter();

  const RegistUser = async () => {
    try {
      await axios.post(REST_USER_API+"", user.value);
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
  return axios.get(REST_USER_API + "check-user-id?userId=" + userId);
};

export const CheckUserEmail = function (email: string) {
  return axios.get(REST_USER_API + "check-email?email=" + email);
};


