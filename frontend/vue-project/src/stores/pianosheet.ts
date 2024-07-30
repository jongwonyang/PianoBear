import { ref } from 'vue';
import { defineStore } from 'pinia';
import axios from 'axios';
import router from '@/router';

// const REST_MATCH_API = `http://localhost:8080/`;

interface PianoSheet {
  id: number;
  title: string;
  // 필요한 다른 속성들 추가
}

export const usePianoSheetStore = defineStore('pianosheet', () => {
    // 악보 리스트
    const pianoSheetList = ref<PianoSheet[]>([
        { id: 1, title: "하정수" },
        { id: 2, title: "서근범" },
        { id: 3, title: "김태성" },
        { id: 4, title: "양종원" },
        { id: 5, title: "박찬수" },
        { id: 6, title: "이소민" },
        { id: 7, title: "7" },
        { id: 8, title: "8" },
        { id: 9, title: "9" },
        { id: 10, title: "10" },
        { id: 11, title: "11" },
        { id: 12, title: "12" }
      ]);

    const handleFavorite = () => {
        console.log("즐겨찾기 요청 보냄~~")
        // 매개변수 필요할듯?
    };

    const handleDelete = () => {
        console.log("삭제 요청 보냄~~")
        // 매개변수 필요할듯?
    };

    return {
        pianoSheetList,
        handleFavorite,
        handleDelete,
    };
});