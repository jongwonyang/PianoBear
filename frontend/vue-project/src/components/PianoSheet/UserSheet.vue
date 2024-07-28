<template>
    <div class="page">
        <div class="bookshelf">
            <div class="shelf">
                <router-link :to="`/main/piano-sheet/${1}`"><div class="book">사용자악보1</div></router-link>
                <div class="book">사용자악보1</div>
                <div class="book">사용자악보1</div>
            </div>
        </div>
        <div class="bookshelf">
            <div class="shelf">
                <div class="book">사용자악보1</div>
                <div class="book">사용자악보1</div>
                <div class="book">사용자악보1</div>
            </div>
        </div>
    </div>   
</template>

<script setup>
import { ref } from 'vue';
</script>

<style scoped>
.page {
display: flex;
flex-direction: column;
justify-content: center; /* 수평 중앙 배치 */
align-items: center; /* 수직 중앙 배치 */
height: 70vh; /* 전체 화면 높이 */
}

.bookshelf {
flex-direction: column;
width: 900px; 
background-color: #D2B48C;
padding: 35px;
margin-top: 30px;
margin-bottom: 30px;
}

.shelf {
display: flex;
justify-content: space-between;
}

.book {
width: 130px; 
height: 160px; 
background-color: #e1e1e1;
display: flex;
align-items: center;
justify-content: center;
transition: all 0.3s linear; /* 악보 천천히 커지게 하는 효과 */
}

.book:hover {
    transform: scale(1.2); /* 악보 키우는 효과 */
}
</style>