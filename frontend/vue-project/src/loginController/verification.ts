import axios from "axios";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/user";

const apiClient = axios.create({
  baseURL: "https://apitest.pianobear.kr/api/v1/",
  headers: {
    "Content-Type": "application/json",
  },
});

apiClient.interceptors.request.use(
  async (config) => {
    const userStore = useUserStore();
    const token = userStore.GetAccessToken();

    if (token) {
      config.headers["Authorization"] = `Bearer ${token}`;
    }

    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

apiClient.interceptors.response.use(
  (response) => {
    return response;
  },
  async (error) => {
    const userStore = useUserStore();
    const originalRequest = error.config;

    if (error.response.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      try {
        const res = await axios.post(
          "https://apitest.pianobear.kr/api/v1/auth/refresh",
          {
            refreshToken: userStore.GetRefreshToken(),
          }
        );

        userStore.SetAccessToken(res.data.accessToken);
        userStore.SetRefreshToken(res.data.refreshToken);
        axios.defaults.headers.common[
          "Authorization"
        ] = `Bearer ${res.data.accessToken}`;
        originalRequest.headers[
          "Authorization"
        ] = `Bearer ${res.data.accessToken}`;
        return apiClient(originalRequest);
      } catch (err) {
        userStore.RemoveToken();
        const router = useRouter();
        router.push("/login");
        return Promise.reject(err);
      }
    }

    return Promise.reject(error);
  }
);

export default apiClient;
