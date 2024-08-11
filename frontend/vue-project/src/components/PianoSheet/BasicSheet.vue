<template>
  <div class="container">
    <div class="button-container">
      <button class="prev" @click="downCount" :class="{ hidden: !canGoBack }"></button>
    </div>
    <div class="page">
      <div v-for="pageIndex in 2" :key="pageIndex" class="line">
        <div :class="`bookshelf${pageIndex}`">
          <div class="shelf">
            <router-link
              v-for="(book, index) in currentList.filter(
                (_, i) => bookCount + pageIndex - 1 <= i / 5 && i / 5 < bookCount + pageIndex
              )"
              :key="index"
              :to="`/main/piano-sheet/${book.id}`"
              class="router"
            >
              <div class="book">
                <img src="@/assets/images/blur.png" alt="Book Image" />
              </div>
            </router-link>
          </div>
        </div>
        <div :class="`support${pageIndex}`">
          <div class="support">
            <div
              v-for="(book, index) in currentList.filter(
                (_, i) => bookCount + pageIndex - 1 <= i / 5 && i / 5 < bookCount + pageIndex
              )"
              :key="index"
              class="title"
            >
              {{ book.title }}
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="button-container">
      <button class="next" @click="upCount" :class="{ hidden: !canGoForward }"></button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from "vue";
import { usePianoSheetStore } from "@/stores/pianosheet";

const store = usePianoSheetStore();
const bookCount = ref<number>(0);
const pageCount = ref<number>(0);
const currentSortOption = ref<number>(0);

const maxCount = computed<number>(() => Math.floor((store.userSheetList.length - 1) / 10));
const canGoBack = computed<boolean>(() => bookCount.value > 0);
const canGoForward = computed<boolean>(() => maxCount.value > pageCount.value);

const downCount = (): void => {
  if (canGoBack.value) {
    bookCount.value -= 2;
    pageCount.value -= 1;
  }
};

const upCount = (): void => {
  if (canGoForward.value) {
    bookCount.value += 2;
    pageCount.value += 1;
  }
};

const basicFavoriteList = computed(() => store.basicFavoriteList);
const basicPracticeList = computed(() => store.basicPracticeList);

const currentList = computed(() => {
  switch (currentSortOption.value) {
    case 0:
      return basicFavoriteList.value;
    case 1:
      return basicPracticeList.value;
    default:
      return [];
  }
});

// props로 전달받은 sortOption
const props = defineProps<{ sortOption: number }>();

watch(
  () => props.sortOption,
  (newSortOption) => {
    currentSortOption.value = newSortOption;
  }
);
</script>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  align-items: center; /* 수직 중앙 배치 */
  height: 68vh; /* 전체 화면 높이 */
}

.bookshelf1 {
  flex-direction: column;
  width: 70vw;
  height: 27vh;
  background-color: #d2b48c;
  padding: 5vh;
  /* padding-bottom: 0vh; */
}

.bookshelf2 {
  flex-direction: column;
  width: 70vw;
  height: 27vh;
  background-color: #e8c8a0;
  padding: 5vh;
  /* padding-bottom: 0vh; */
}

.shelf {
  display: flex;
  justify-content: flex-start;
}

.support {
  display: flex;
  justify-content: flex-start;
}

.book {
  width: 9vw;
  height: 20.75vh;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s linear; /* 악보 천천히 커지게 하는 효과 */
  margin-left: 2vw;
  margin-right: 2vw;
}

.title {
  width: 9vw;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-left: 2vw;
  margin-right: 2vw;
}

.book:hover {
  transform: scale(1.1); /* 악보 키우는 효과 */
}

.support1 {
  background-color: #e8c8a0;
  padding: 0.5vh;
  width: 70vw;
  height: 4vh;
  flex-direction: column;
  padding-left: 2.6vw;
}

.support2 {
  background-color: #d2b48c;
  padding: 0.5vh;
  width: 70vw;
  height: 4vh;
  flex-direction: column;
  padding-left: 2.6vw;
}

.router {
  text-decoration: none;
  color: black;
}

.book img {
  width: 18vw;
  height: 23vh;
}

.container {
  display: flex;
  align-items: center;
  justify-content: space-between; /* 각 요소 사이의 공간을 균등하게 배분 */
}

.line {
  margin-bottom: 4vh;
  box-shadow: 0.1vw 0.4vh 0.8vh gray;
}

.prev {
  border-bottom: 3vh solid transparent;
  border-top: 3vh solid transparent;
  border-left: 2vw solid transparent;
  border-right: 2vw solid #a48253;
  margin-right: 3vw;
}

.next {
  border-bottom: 3vh solid transparent;
  border-top: 3vh solid transparent;
  border-left: 2vw solid #a48253;
  border-right: 2vw solid transparent;
  margin-left: 3vw;
}

.hidden {
  opacity: 0; /* 버튼을 시각적으로 숨깁니다 */
  pointer-events: none; /* 버튼 클릭 방지 */
}
</style>
