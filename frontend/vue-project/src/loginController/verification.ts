import axios from "axios";
import { useTokenStore } from "@/stores/token";
import { useRouter } from "vue-router";

const apiClient = axios.create({
  baseURL: "http://192.168.31.37:7000/api/v1/",
  headers: {
    "Content-Type": "application/json",
  },
});

apiClient.interceptors.request.use(
  async (config) => {
    const tokenStore = useTokenStore();
    const token = tokenStore.GetAccessToken();

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
    const tokenStore = useTokenStore();
    const originalRequest = error.config;

    if (error.response.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      try {
        const res = await axios.post(
          "http://192.168.31.37:7000/api/v1/auth/refresh",
          {
            refreshToken: tokenStore.GetRefreshToken(),
          }
        );

        tokenStore.SetAccessToken(res.data.accessToken);
        tokenStore.SetRefreshToken(res.data.refreshToken);
        axios.defaults.headers.common[
          "Authorization"
        ] = `Bearer ${res.data.accessToken}`;
        originalRequest.headers[
          "Authorization"
        ] = `Bearer ${res.data.accessToken}`;
        return apiClient(originalRequest);
      } catch (err) {
        tokenStore.RemoveToken();
        const router = useRouter();
        router.push("/login");
        return Promise.reject(err);
      }
    }

    return Promise.reject(error);
  }
);

export default apiClient;
