import { ref } from "vue";
import { defineStore } from "pinia";
import axios from "axios";
import apiClient from "@/loginController/verification";

const REST_PIANOSHEET_API = `${import.meta.env.VITE_API_BASE_URL}/music`;

// 기본제공 악보
interface BasicSheet {
  id: number;
  title: string;
  practiceCountP: number;
  recentPracticeP: string;
  imgP: string;
  favoriteP: boolean;
  artistP: string;
  highestScoreP: number;
}

// 사용자 악보
export interface UserSheet {
  id: number;
  title: string;
  // originalFileRoute: string;
  // changedFileRoute: string;
  practiceCount: number;
  // recentPractice: string;
  userId: string;
  musicImg: string;
  favorite: boolean;
  uploadDate: string;
  // artist: string;
  // highestScore: number;
}

// 연습 기록의 형태를 나타내는 인터페이스 정의
interface PracticeRecord {
  id: number;
  practiceDate: string;
  practiceCount: number;
  musicId: number;
}

export const usePianoSheetStore = defineStore("pianosheet", () => {
  // 기본 악보 리스트
  const basicSheetList = ref<BasicSheet[]>([]);
  const basicPracticeList = ref<BasicSheet[]>([]);
  const basicFavoriteList = ref<BasicSheet[]>([]);

  // 기본 악보 목록을 가져오는 함수
  const basicSheetListfun = async (): Promise<void> => {
    try {
      const response = await apiClient.get<BasicSheet[]>(REST_PIANOSHEET_API);
      const data = response.data;

      // 연습량 기준으로 정렬
      basicPracticeList.value = [...data].sort(
        (a, b) => b.practiceCountP - a.practiceCountP
      );

      // 즐겨찾기 기준으로 정렬
      const favorites = data.filter((sheet) => sheet.favoriteP);
      const nonFavorites = data.filter((sheet) => !sheet.favoriteP);
      basicFavoriteList.value = [...favorites, ...nonFavorites];

      basicSheetList.value = data;
      console.log("응답 데이터:", data);
    } catch (error) {
      console.error("악보 목록 가져오기 실패!", error);
    }
  };

  // 사용자가 업로드 한 악보
  const selectedFile = ref<File | null>(null);

  // 변환된 악보
  const convertedFile = ref<UserSheet | null>(null);

  // PDFtoMXL.vue에서 사용할 메서드
  // selectedFile에 file 저장
  const setSelectedFile = (file: File | null) => {
    selectedFile.value = file;
  };

  // pdf 파일 변환 시작 요청
  const convertFilefun = async (file: File): Promise<void> => {
    if (!file) {
      alert("먼저 악보를 선택해주세요!");
      return;
    }

    const formData = new FormData();
    formData.append("file", file); // key, value 형태고 key의 변수명을 백엔드랑 맞춰야 함

    try {
      const response = await apiClient.post(
        `${REST_PIANOSHEET_API}/process`,
        formData,
        {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        }
      );
      console.log("악보 백엔드 전송 성공!", response.data);
    } catch (error) {
      console.error("악보 백엔드 전송 실패!", error);
    }
  };

  // PDF에서 MXL로 변환 완료된 악보 가져오기
  const convertedFilefun = async (): Promise<void> => {
    try {
      const response = await apiClient.get(REST_PIANOSHEET_API);
      if (response.status >= 200 && response.status < 300) {
        // 상태 코드가 200번대인 경우
        console.log("악보 변환 성공!");
        convertedFile.value = response.data;
      } else {
        // 상태 코드가 200번대가 아닌 경우
        console.error("악보 변환 실패! 상태 코드:", response.status);
      }
    } catch (error) {
      console.error("Error fetching sheet data", error);
    }
  };

  // 변환된 악보 제목 수정
  const editTitle = () => {
    // 파라미터로 악보 id를 받아야 하나?
    console.log("악보제목 수정 요청!");
  };

  // 변환된 악보 저장
  const saveSheet = async (): Promise<void> => {
    try {
      const response = await apiClient.post(REST_PIANOSHEET_API);
    } catch (error) {
      console.error("악보 저장 실패ㅠ", error);
    }
  };

  // 즐겨찾기 여부 확인
  const isFavorite = ref(false);

  const checkFavorite = async (id: number): Promise<void> => {
    try {
      const response = await apiClient.get(
        `${REST_PIANOSHEET_API}/${id}/favorite`
      );
      isFavorite.value = response.data;
      // console.log("즐겨찾기 여부 " + response.data);
    } catch (error) {
      console.error("즐겨찾기 상태 확인 실패", error);
    }
  };

  const handleFavorite = async (
    id: number,
    favorite: boolean
  ): Promise<void> => {
    try {
      const response = await apiClient.post(
        `${REST_PIANOSHEET_API}/${id}/favorite`,
        null,
        {
          params: {
            favorite: favorite,
          },
        }
      );

      if (response.status >= 200 && response.status < 300) {
        isFavorite.value = favorite;
        console.log("즐겨찾기 상태 변경 성공!");
      }
    } catch (error) {
      console.error("즐겨찾기 실패 ㅠㅠ", error);
    }
  };

  // 상세 - 삭제
  const handleDelete = async (id: number) => {
    console.log("삭제 요청 보냄~~");
    try {
      const response = await apiClient.delete(`${REST_PIANOSHEET_API}/${id}`);
      if (response.status >= 200 && response.status < 300) {
        console.log("삭제 성공!");
      } else {
        console.error("악보 삭제 실패! 상태 코드:", response.status);
      }
    } catch (error) {
      console.error("Error deleting sheet", error);
    }
    // 매개변수 필요할듯?
  };

  // 연습 기록 데이터
  const practiceData = ref<PracticeRecord[]>([]);
  const practiceCountSum = ref(0);

  // 상세 - 산딸기(연습기록 불러오기)
  const practiceDatafun = async (id: number): Promise<void> => {
    // console.log("연습기록!!");
    try {
      const response = await apiClient.get<PracticeRecord[]>(
        `${REST_PIANOSHEET_API}/practice/${id}/sorted`
      );
      practiceData.value = response.data;
      practiceCountSum.value = 0; // 초기화

      for (var i = 0; i < practiceData.value.length; i++) {
        practiceCountSum.value += practiceData.value[i].practiceCount;
      }

      // console.log(practiceData.value);
      console.log(practiceCountSum.value);
    } catch (error) {
      console.error("기록 불러오기 실패", error);
    }
  };

  // 사용자 악보 리스트
  const userSheetList = ref<UserSheet[]>([]);
  const userPracticeList = ref<UserSheet[]>([]);
  const userUploadList = ref<UserSheet[]>([]);
  const userFavoriteList = ref<UserSheet[]>([]);

  const userSheetListfun = async (): Promise<void> => {
    try {
      const response = await apiClient.get<UserSheet[]>(REST_PIANOSHEET_API);
      const data = response.data;

      // 각 악보의 총 연습량 계산
      const practiceCounts = await Promise.all(
        data.map(async (sheet) => {
          await practiceDatafun(sheet.id);
          return {
            ...sheet,
            totalPracticeCount: practiceCountSum.value,
          };
        })
      );

      // 연습량 기준으로 정렬
      userPracticeList.value = practiceCounts.sort(
        (a, b) => b.totalPracticeCount - a.totalPracticeCount
      );

      // 악보등록 기준으로 정렬
      userUploadList.value = [...data].sort(
        (a, b) =>
          new Date(b.uploadDate).getTime() - new Date(a.uploadDate).getTime()
      );

      // 즐겨찾기 기준으로 정렬
      const favorites = data.filter((sheet) => sheet.favorite);
      const nonFavorites = data.filter((sheet) => !sheet.favorite);
      userFavoriteList.value = [...favorites, ...nonFavorites];

      // 전체 목록 저장
      userSheetList.value = data;

      console.log("응답 데이터:", data); // 응답 데이터 확인
    } catch (error) {
      console.error("악보 목록 가져오기 실패!", error);
    }
  };

  const detailSheet = ref<UserSheet>();

  // 상세 - 특정 악보 불러오기
  const detailSheetfun = async (id: number): Promise<void> => {
    try {
      console.log("악보상세");
      const response = await apiClient.get<UserSheet>(
        `${REST_PIANOSHEET_API}/${id}`
      );
      detailSheet.value = response.data;
      console.log(detailSheet.value);
    } catch (error) {
      console.error("불러오기 실패", error);
    }
  };

  // mxl 로드 function
  const mxlLoadfun = async (id: number): Promise<ArrayBuffer> => {
    try {
      const response = await apiClient.get<ArrayBuffer>(
        `${REST_PIANOSHEET_API}/api/v1/music/${id}/download-modified-music-xml`,
        { responseType: "arraybuffer" }
      );
      return response.data;
    } catch (error) {
      console.error(error);
      throw error;
    }
  };

  // 연습완료
  const practicePostfun = async (id: number): Promise<void> => {
    try {
      await apiClient.post<void>(`${REST_PIANOSHEET_API}/practice/${id}`);
      alert("연습 완료");
    } catch (error) {
      console.error(error);
      throw error;
    }
  };

  // 정렬기준
  const sortOption = ref<number>();

  return {
    basicSheetList,
    basicPracticeList,
    basicFavoriteList,
    basicSheetListfun,
    userSheetList,
    userFavoriteList,
    userUploadList,
    userPracticeList,
    userSheetListfun,
    selectedFile,
    convertedFile,
    setSelectedFile,
    isFavorite,
    checkFavorite,
    handleFavorite,
    handleDelete,
    convertFilefun,
    convertedFilefun,
    editTitle,
    saveSheet,
    practiceData,
    practiceDatafun,
    detailSheet,
    detailSheetfun,
    sortOption,
    mxlLoadfun,
    practicePostfun,
  };
});
