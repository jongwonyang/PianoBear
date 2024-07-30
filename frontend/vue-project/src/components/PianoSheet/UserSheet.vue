<template>
    <div class="page">
        <v-card elevation="5">
            <div>
                <div class="bookshelf1">
                    <div class="shelf">
                        <router-link v-for="(book, index) in books1" :key="index" :to="`/main/piano-sheet/${index + 1}`" class="router">
                            <div class="book">사용자악보표지{{ index + 1 }}</div>
                        </router-link>
                    </div>
                </div>
                <div class="support1">
                    <div class="support">
                        <div v-for="(book, index) in books1" :key="index" class="title">악보제목</div>
                    </div>
                </div>
            </div>
        </v-card>
        <div class="my-5"></div>
        <v-card elevation="5">
            <div>
                <div class="bookshelf2">
                    <div class="shelf">
                        <router-link v-for="(book, index) in books2" :key="index" :to="`/main/piano-sheet/${index + 6}`" class="router">
                            <div class="book">사용자악보표지{{ index + 6 }}</div>
                        </router-link>
                    </div>
                </div>
                <div class="support2">
                    <div class="support">
                        <div v-for="(book, index) in books2" :key="index" class="title">악보제목</div>
                    </div>
                </div>
            </div>
        </v-card>
    </div>   
</template>

<script setup>
import { ref } from 'vue';

const books1 = ref([1, 2, 3, 4]);
// const books2 = ref([6, 7, 8, 9, 10]);
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
    height: 195px;
    background-color: #D2B48C;
    padding: 35px;
    padding-bottom: 0px;
}

.bookshelf2 {
    flex-direction: column;
    width: 900px; 
    height: 195px;
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
    height: 30px;
    flex-direction: column;
    padding-left: 35px;
    padding-right: 35px;
}

.support2{
    background-color: #D2B48C;
    padding: 3px;
    width: 900px;
    height: 30px;
    flex-direction: column;
    padding-left: 35px;
    padding-right: 35px;
}

.router{
    text-decoration: none;
    color: black;
}
</style>
