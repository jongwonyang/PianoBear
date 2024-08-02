import { ref } from "vue";
import { defineStore } from "pinia";
import apiClient from "@/loginController/verification"; // Axios 인스턴스 import
import { useRouter } from "vue-router";

const REST_DASHBOARD_API = "http://localhost:7000/api/v1/dashboard/";

export const useDashboardStore = defineStore("dashboard", () => {
  const router = useRouter();

  // 요약정보
  const GetSummary = () => {
    return apiClient.get(REST_DASHBOARD_API + "summary");
  };

  // 월별 연습 기록
  const GetMonthlyPracticeRecord = (year: number, month: number) => {
    return apiClient.get(
      REST_DASHBOARD_API +
        "monthly-practice-record" +
        `?year=${year}&month=${month}`
    );
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
