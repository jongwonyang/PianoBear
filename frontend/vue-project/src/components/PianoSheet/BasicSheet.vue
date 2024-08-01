<template>
    <div class="page">
        <v-card elevation="5">
            <div>
                <div class="bookshelf1">
                    <div class="shelf">
                        <router-link v-for="(book) in pianoSheetList.slice(0,5)" :key="book.id" :to="`/main/piano-sheet/${book.id}`" class="router">
                            <div class="book">{{ book.title }}</div>
                        </router-link>
                    </div>
                </div>
                <div class="support1">
                    <div class="support">
                        <div v-for="(book) in pianoSheetList.slice(5,10)" :key="book.id" class="title">악보제목dddddd</div>
                    </div>
                </div>
            </div>
        </v-card>
        <div class="my-5"></div>
        <v-card elevation="5">
            <div>
                <div class="bookshelf2">
                    <div class="shelf">
                        <router-link v-for="(book) in pianoSheetList.slice(5,10)" :key="book.id" :to="`/main/piano-sheet/${book.id + 5}`" class="router">
                            <div class="book">{{ book.title }}</div>
                        </router-link>
                    </div>
                </div>
                <div class="support2">
                    <div class="support">
                        <div v-for="(book) in pianoSheetList.slice(5,10)" :key="book.id" class="title">제목길이생각..</div>
                    </div>
                </div>
            </div>
        </v-card>
    </div>   
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { usePianoSheetStore } from "@/stores/pianosheet";

const store = usePianoSheetStore();

const pianoSheetList = ref(store.pianoSheetList);
</script>

<style scoped>
.page {
    display: flex;
    flex-direction: column;
    align-items: center; /* 수직 중앙 배치 */
    height: 70vh; /* 전체 화면 높이 */
}

.bookshelf1 {
    flex-direction: column;
    width: 900px; 
    background-color: #D2B48C;
    padding: 35px;
    padding-bottom: 0px;
}

.bookshelf2 {
    flex-direction: column;
    width: 900px; 
    background-color: #E8C8A0;
    padding: 35px;
    padding-bottom: 0px;
}

.shelf {
    display: flex;
    justify-content: flex-start;
}

.support{
    display: flex;
    justify-content: flex-start;
}

.book {
    width: 126px; 
    height: 160px; 
    background-color: white;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.3s linear; /* 악보 천천히 커지게 하는 효과 */
    margin-left: 20px;
    margin-right: 20px; /* 악보들 사이의 고정된 간격 */
}

.title{
    width: 126px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-left: 20px;
    margin-right: 20px;
}

.book:hover {
    transform: scale(1.1); /* 악보 키우는 효과 */
}

.support1{
    background-color: #E8C8A0;
    padding: 3px;
    width: 900px;
    flex-direction: column;
    padding-left: 35px;
    padding-right: 35px;
}

.support2{
    background-color: #D2B48C;
    padding: 3px;
    width: 900px;
    flex-direction: column;
    padding-left: 35px;
    padding-right: 35px;
}

.router{
    text-decoration: none;
    color: black;
}
</style>
