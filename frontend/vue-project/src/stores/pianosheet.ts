import { ref } from "vue";
import { defineStore } from "pinia";
import axios from "axios";
import apiClient from "@/loginController/verification";
import type { promises } from "dns";
import { createModuleResolutionCache } from "typescript";

const REST_PIANOSHEET_API = `${import.meta.env.VITE_API_BASE_URL}/music`;

// 기본제공 악보
export interface BasicSheet {
  id: number;
  title: string;
  practiceCountP: number;
  recentPracticeP: string;
  imgP: string;
  favoriteP: boolean;
  artistP: string;
  highestScoreP: number;
  userId: string;
}

// 사용자 악보
export interface UserSheet {
  id: number;
  title: string;
  originalFileRoute: string;
  changedFileRoute: string;
  practiceCount: number;
  recentPractice: string;
  userId: string;
  musicImg: string;
  favorite: boolean;
  uploadDate: string;
  artist: string;
  highestScore: number;
}

// 변환된 악보
interface ConvertedSheet {
  id: number;
  title: string | null;
  musicXmlRoute: string;
  modifiedMusicXmlRoute: string;
  userId: string;
  artist: string | null;
  favorite: boolean;
  musicImg: string | null;
  uploadDate: [number, number, number]; // [year, month, day]
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
      const response = await apiClient.get<BasicSheet[]>(
        `${REST_PIANOSHEET_API}/user-or-default`
      );
      const data = response.data;

      // userId가 존재하는 데이터만 필터링
      const filteredData = data.filter((sheet) => !sheet.userId);

      // 연습량 기준으로 정렬
      basicPracticeList.value = [...filteredData].sort(
        (a, b) => b.practiceCountP - a.practiceCountP
      );

      // 즐겨찾기 기준으로 정렬
      const favorites = filteredData.filter((sheet) => sheet.favoriteP);
      const nonFavorites = filteredData.filter((sheet) => !sheet.favoriteP);
      basicFavoriteList.value = [...favorites, ...nonFavorites];

      // 전체 필터링된 리스트를 저장
      basicSheetList.value = filteredData;

      console.log("필터링된 응답 데이터:", filteredData);
    } catch (error) {
      console.error("악보 목록 가져오기 실패!", error);
    }
  };

  // 사용자가 업로드 한 악보
  const selectedFile = ref<File | null>(null);

  // 변환된 악보
  const convertedFile = ref<ConvertedSheet | null>(null);

  // PDFtoMXL.vue에서 사용할 메서드
  // selectedFile에 file 저장
  const setSelectedFile = (file: File | null) => {
    selectedFile.value = file;
  };

  // 수정된 악보 정보
  const modifySheetForm = ref({
    title: "",
    artist: "",
  });

  // 악보 제목, 작곡가 수정을 위한 모달 열기 위한 변수
  const isOpen = ref<boolean>(false);

  // pdf 파일 변환 시작 요청
  const convertFilefun = async (file: File): Promise<void> => {
    if (!file) {
      alert("먼저 악보를 선택해주세요!");
      return;
    }

    const formData = new FormData();
    formData.append("file", file); // key, value 형태고 key의 변수명을 백엔드랑 맞춰야 함

    try {
      console.log("변환시작");
      const response = await apiClient.post(
        `${REST_PIANOSHEET_API}/process`,
        formData,
        {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        }
      );

      convertedFile.value = response.data;
      modifySheetForm.value.title = convertedFile.value?.title ?? "";
      modifySheetForm.value.artist = convertedFile.value?.artist ?? "";
      console.log("악보 백엔드 전송 성공!", response.data);
      isOpen.value = true;
    } catch (error) {
      console.error("악보 백엔드 전송 실패!", error);
    }
  };

  // 변환된 악보 수정
  const editSheet = async (editTitle: string): Promise<void> => {
    if (!detailSheet.value) {
      return;
    }

    try {
      const sheetId = detailSheet.value.id;
      console.log(sheetId);
      console.log(editTitle);
      const response = await apiClient.post(
        `${REST_PIANOSHEET_API}/${sheetId}/edit`,
        null,
        {
          params: {
            id: sheetId,
            title: editTitle,
          },
        }
      );
      detailSheet.value.title = editTitle;
      console.log(response.data);
    } catch (error) {
      console.error("악보 수정 실패", error);
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

  // 즐겨찾기 요청
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
    try {
      const response = await apiClient.delete(`${REST_PIANOSHEET_API}/${id}`);
      if (response.status >= 200 && response.status < 300) {
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
      practiceData.value = [];
      response.data.forEach((e) => {
        if (e.practiceCount > 4) {
          e.practiceCount = 5;
        }
        practiceData.value.push(e);
      });
      practiceCountSum.value = 0; // 초기화

      for (var i = 0; i < practiceData.value.length; i++) {
        practiceCountSum.value += practiceData.value[i].practiceCount;
      }
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
      const response = await apiClient.get<UserSheet[]>(
        `${REST_PIANOSHEET_API}/user-or-default`
      );
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

      console.log("응답 데이터:", userSheetList.value[0]); // 응답 데이터 확인
    } catch (error) {
      console.error("악보 목록 가져오기 실패!", error);
    }
  };

  const detailSheet = ref<UserSheet>();

  // 상세 - 특정 악보 불러오기
  const detailSheetfun = async (id: number): Promise<void> => {
    try {
      // console.log("악보상세");
      const response = await apiClient.get<UserSheet>(
        `${REST_PIANOSHEET_API}/${id}`
      );
      detailSheet.value = response.data;
      console.log(detailSheet.value);
    } catch (error) {
      console.error("불러오기 실패", error);
    }
  };

  // 썸네일 불러오기
  const thumbnailImg = ref<string>();

  const thumbnail = async (id: number): Promise<void> => {
    try {
      const response = await apiClient.get<string>(
        `${REST_PIANOSHEET_API}/${id}/music-img`
      );
      thumbnailImg.value = response.data;
      console.log(thumbnailImg.value);
    } catch (error) {
      console.error(error);
    }
  };

  //mxl 불러오기
  const mxlLoadfun = async (id: number): Promise<ArrayBuffer> => {
    try {
      const response = await apiClient.get<ArrayBuffer>(
        `${REST_PIANOSHEET_API}/api/v1/music/${id}/download-music-xml`, //-modified
        {
          responseType: "arraybuffer",
          headers: {
            "Content-Type": "application/xml",
          },
        }
      );
      return response.data;
    } catch (error) {
      console.error(error);
      throw error;
    }
  };

  //mxl 불러오기
  const mxlModifiedLoadfun = async (id: number): Promise<ArrayBuffer> => {
    try {
      const response = await apiClient.get<ArrayBuffer>(
        `${REST_PIANOSHEET_API}/api/v1/music/${id}/download-modified-music-xml`, //-modified
        // const response = await axios.get(
        //   "/c6fb7283-44c1-4c7a-bdf7-a3264b1d452e_modified.mxl",
        {
          responseType: "arraybuffer",
          headers: {
            "Content-Type": "application/xml",
          },
        }
      );
      return response.data;
    } catch (error) {
      console.error(error);
      throw error;
    }
  };

  // 연습하기
  const practicePostfun = async (id: number): Promise<void> => {
    try {
      await apiClient.post<void>(`${REST_PIANOSHEET_API}/practice/${id}`);
    } catch (error) {
      console.error(error);
    }
  };

  // 결과 모달창
  // 도전 결과
  const resultChallenge = ref();
  const isResultModalOpen = ref<Boolean>(false);
  const challengefun = async (id: number, audioBlob: Blob): Promise<void> => {
    const formData = new FormData();
    formData.append("audioFile", audioBlob);
    try {
      const response = await apiClient.post(
        `${REST_PIANOSHEET_API}/test/${id}`,
        formData,
        {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        }
      );
      resultChallenge.value = response.data;
      isResultModalOpen.value = true;
    } catch (error) {
      console.log(error);
    }
  };

  const clearResult = function () {
    resultChallenge.value = null;
  };

  // 정렬기준
  const sortOption = ref<number>();

  // 검색어
  const searchText = ref<string>("");

  const makeImg = async (id: number) => {
    try {
      console.log("요청보냄");
      const response = await apiClient.post(
        `${REST_PIANOSHEET_API}/${id}/generate-image`
      );
      console.log(response.data);
    } catch (error) {
      console.error(error);
    }
  };

  const saveMakedImg = async (id: number) => {
    try {
      const response = await apiClient.get(
        `${REST_PIANOSHEET_API}/${id}/download-music-img`
      );
      console.log(response.data);
      return response.data;
    } catch (error) {
      console.error(error);
    }
  };

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
    modifySheetForm,
    isOpen,
    editSheet,
    practiceData,
    practiceDatafun,
    detailSheet,
    detailSheetfun,
    sortOption,
    thumbnailImg,
    thumbnail,
    searchText,
    isResultModalOpen,
    mxlLoadfun,
    practicePostfun,
    challengefun,
    mxlModifiedLoadfun,
    makeImg,
    saveMakedImg,
    resultChallenge,
    clearResult,
  };
});
