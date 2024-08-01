import { ref } from 'vue';
import { defineStore } from 'pinia';
import axios from 'axios';
import router from '@/router';

const REST_PIANOSHEET_API = `https://apitest.pianobear.kr/api/v1/music`;

interface PianoSheet {
  id: number;
  title: string;
  // 필요한 다른 속성들 추가
}

export const usePianoSheetStore = defineStore('pianosheet', () => {
    // 악보 리스트
    const pianoSheetList = ref<PianoSheet[]>([]);

    // 백엔드에서 악보목록을 가져오는 함수
    const fetchPianoSheets = async (): Promise<void> => {
        try {
          const response = await axios.get(REST_PIANOSHEET_API);
          console.log('응답 데이터:', response.data);  // 응답 데이터 확인
          pianoSheetList.value = response.data;
        } catch (error) {
          console.error('악보 목록 가져오기 실패!', error);
        }
      };
    
    // 사용자가 업로드 한 악보
    const selectedFile = ref<File | null>(null);
    // 변환된 악보
    const sheetData = ref<{ title: string } | null>(null); 

    // PDFtoMXL.vue에서 사용할 메서드
    // selectedFile에 file 저장
    const setSelectedFile = (file: File | null) => {
        selectedFile.value = file;
      };
    
    // pdf 파일 변환 시작 요청
    const convertFile = async (file: File): Promise<void> => {
        if (!file) {
            alert('먼저 악보를 선택해주세요!');
            return;
        }
        
        const formData = new FormData();
        formData.append('file', file); // key, value 형태고 key의 변수명을 백엔드랑 맞춰야 함
        
        try {
            const response = await axios.post(REST_PIANOSHEET_API, formData, {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            });
            console.log('악보 백엔드 전송 성공!', response.data);
        } catch (error) {
            console.error('악보 백엔드 전송 실패!', error);
        }
    };

    // PDF에서 MXL로 변환 완료된 악보 가져오기
    const convertedFile = async (): Promise<void> => {
        try {
            const response = await axios.get(REST_PIANOSHEET_API);
            if (response.status >= 200 && response.status < 300) {
                // 상태 코드가 200번대인 경우
                console.log("악보 변환 성공!")
                sheetData.value = response.data;
            } else {
                // 상태 코드가 200번대가 아닌 경우
                console.error('악보 변환 실패! 상태 코드:', response.status);
            }
        } catch (error) {
            console.error('Error fetching sheet data', error);
        }
    }

    // 변환된 악보 제목 수정
    const editTitle = () => {
        // 파라미터로 악보 id를 받아야 하나?
        console.log("악보제목 수정 요청!")
    }

    // 변환된 악보 저장
    const saveSheet = async (): Promise<void> => {
        try {
            const response = await axios.post(REST_PIANOSHEET_API);
        } catch (error){
            console.error('악보 저장 실패ㅠ', error);
        }
    }

    // 상세 - 즐겨찾기
    const handleFavorite = async (id: number) => {
        console.log("즐겨찾기 요청 보냄~~")
        try {
            const response = await axios.post(`REST_PIANOSHEET_API/${id}/favorite`);
            if (response.status >= 200 && response.status < 300) {
                console.log('즐겨찾기 성공!');
            }
        } catch (error) {
            console.error('즐겨찾기 실패 ㅠㅠ', error);
            // db에 악보가 없어서 현재는 실패가 맞는 듯
        }
        // 매개변수 필요할듯?
    };

    // 상세 - 삭제
    const handleDelete = async (id: number) => {
        console.log("삭제 요청 보냄~~")
        try {
            const response = await axios.delete(`REST_PIANOSHEET_API/${id}`);
            if (response.status >= 200 && response.status < 300) {
                console.log('삭제 성공!')
                // pianoSheetList.value = pianoSheetList.value.filter(sheet => sheet.id !== id);
            } else {
                console.error('악보 삭제 실패! 상태 코드:', response.status);
            }
        } catch (error) {
            console.error('Error deleting sheet', error);
        }
        // 매개변수 필요할듯?
    };

    return {
        pianoSheetList,
        fetchPianoSheets,
        selectedFile,
        sheetData,
        setSelectedFile,
        handleFavorite,
        handleDelete,
        convertFile,
        convertedFile,
        editTitle,
        saveSheet,
    };
});