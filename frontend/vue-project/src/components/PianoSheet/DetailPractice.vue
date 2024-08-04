<template>
    <div class="container">
        <h3>연습기록 컴포넌트</h3>
        <ul>
            <li v-for="record in store.practiceData" :key="record.id">
                {{ record.practiceDate }}: <img v-for="n in record.practiceCount" :key="n" src="@/assets/images/산딸기.png" alt="Practice Image" class="practice-image" />
            </li>
        </ul>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { usePianoSheetStore } from "@/stores/pianosheet";

const store = usePianoSheetStore();

// 악보 데이터를 가져오는 함수
const fetchPracticeData = async (id: number) => {
    await store.practiceDatafun(id); // 스토어의 convertedFile 메서드 호출
};

onMounted(() => {
    fetchPracticeData(1); // 1대신 musicId 들어가야 함
})
</script>

<style scoped>
.practice-image{
    width: 30px;
    height: 30px;
    margin: 10px;
}

.container{
    border: 1px solid black;
}
</style>