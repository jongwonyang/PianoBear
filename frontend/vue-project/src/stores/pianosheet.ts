import { ref } from 'vue';
import { defineStore } from 'pinia';
import axios from 'axios';
import router from '@/router';

// const REST_MATCH_API = `http://localhost:8080/`;

// interface PianoSheet {
//   id: string;
//   title: string;
//   composer: string;
//   // 필요한 다른 속성들 추가
// }

export const usePianoSheetStore = defineStore('pianosheet', () => {
    // 악보 리스트
    // const pianoSheetList = ref<PianoSheet[]>([]);

    const handleFavorite = () => {
        console.log("즐겨찾기 요청 보냄~~")
        // 매개변수 필요할듯?
    };

    const handleDelete = () => {
        console.log("삭제 요청 보냄~~")
        // 매개변수 필요할듯?
    };

    return {
        handleFavorite,
        handleDelete,
    };
});