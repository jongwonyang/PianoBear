<template>
    <!--변환된 파일-->
    <div class="container">
        <div>
            <div class="icon">
                <v-icon>mdi-music</v-icon>
            </div>
            <div class="text">
                <!-- 변환된 악보가 있을 때 제목을 표시 -->
                <h4 v-if="sheetData">{{ sheetData.title }}</h4>
                <!-- 변환된 악보가 없을 때 기본 메시지 표시 -->
                <h4 v-else>여기에 악보가 만들어집니다.</h4>
            </div>
            <!-- 제목 수정 아이콘은 악보가 있을 때만 표시 -->
            <div v-if="sheetData">
                <v-icon @click="openModal">mdi-pencil</v-icon>
            </div>
            <div v-if="sheetData">
                <button @click="saveSheet">저장</button>
            </div>
            <div class="text-center pa-4" v-if="sheetData">
                <v-dialog v-model="isModalOpen" max-width="400" persistent>
                    <v-card prepend-icon="mdi-map-marker" text="악보 제목 변경!" title="ㅇㅇㅇ">
                        <template v-slot:actions>
                        <v-spacer></v-spacer>
                        <v-btn @click="closeModal">취소</v-btn>
                        <v-btn @click="editTitle">수정</v-btn>
                        </template>
                    </v-card>
                </v-dialog>
            </div>
        </div>
    </div>

</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { usePianoSheetStore } from '@/stores/pianosheet';

const store = usePianoSheetStore();
const sheetData = ref<{ title: string } | null>(null);
const isModalOpen = ref(false);

// 악보 데이터를 가져오는 함수
const fetchSheetData = async () => {
    await store.convertedFile(); // 스토어의 convertedFile 메서드 호출
    sheetData.value = store.sheetData; // 변환된 데이터가 스토어에 저장됨
};

const openModal = () => {
    isModalOpen.value = true;
}

const closeModal = () => {
    isModalOpen.value = false;
}

// 제목 수정 함수 (클릭 시 호출됨)
const editTitle = async (): Promise<void> => {
    await store.editTitle();
};

// 악보 저장
const saveSheet = () => {
    store.saveSheet();
}

onMounted(() => {
    fetchSheetData(); // 컴포넌트가 마운트될 때 악보 데이터 가져오기
});
</script>

<style scoped>
.container {
    border: 2px solid #F5E5D1;
    color: #947650;
    width: 350px;
    height: 550px;
    padding-left: 100px;
    padding-right: 100px;
    padding-top: 200px;
    padding-bottom: 200px;
    font-size: large;
}
</style>
