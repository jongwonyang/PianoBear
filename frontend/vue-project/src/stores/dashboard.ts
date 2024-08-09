import { ref } from "vue";
import { defineStore } from "pinia";
import apiClient from "@/loginController/verification"; // Axios 인스턴스 import
import { useRouter } from "vue-router";

const REST_DASHBOARD_API = import.meta.env.VITE_API_BASE_URL + "/dashboard/";

export const useDashboardStore = defineStore("dashboard", () => {
  const router = useRouter();

  // 요약정보
  const GetSummary = () => {
    return apiClient.get(REST_DASHBOARD_API + "summary");
  };

  // 월별 연습 기록
  const GetMonthlyPracticeRecord = (
    year: number,
    month: number,
    day: number | null = null
  ) => {
    let url =
      REST_DASHBOARD_API + "practice-records" + `?year=${year}&month=${month}`;
    if (day !== null) {
      url += `&day=${day}`;
    }
    return apiClient.get(url);
  };

  // 온라인 친구 목록
  const GetOnlineFriends = () => {
    return apiClient.get(REST_DASHBOARD_API + "online-friends");
  };
  return {
    GetSummary,
    GetMonthlyPracticeRecord,
    GetOnlineFriends,
  };
});
``;
