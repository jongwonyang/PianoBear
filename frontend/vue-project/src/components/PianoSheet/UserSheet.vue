<template>
  <div class="container">
    <div>
      <button class="prev" @click="downCount"></button>
    </div>
    <div class="page" v-if="currentList.length">
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
                <img src="@/assets/images/정수 거울셀카.jpg" alt="Book Image" />
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
              {{ book.id }}번 악보
            </div>
          </div>
        </div>
      </div>
    </div>
    <div>
      <button class="next" @click="upCount"></button>
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
const currentTab = ref<string>("UserSheet");

const maxCount = computed<number>(() => Math.floor((store.userSheetList.length - 1) / 10));

const downCount = (): void => {
  if (bookCount.value > 0) {
    bookCount.value -= 2;
    pageCount.value -= 1;
  }
};
const upCount = (): void => {
  if (maxCount.value > pageCount.value) {
    bookCount.value += 2;
    pageCount.value += 1;
  }
};

const userFavoriteList = computed(() => store.userFavoriteList);
const userPracticeList = computed(() => store.userPracticeList);
const userUploadList = computed(() => store.userUploadList);

const currentList = computed(() => {
  switch (currentSortOption.value) {
    case 0:
      return userFavoriteList.value;
    case 1:
      return userPracticeList.value;
    case 2:
      return userUploadList.value;
    default:
      return [];
  }
});

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
  width: 900px;
  height: 195px;
  background-color: #d2b48c;
  padding: 35px;
  padding-bottom: 0px;
}

.bookshelf2 {
  flex-direction: column;
  width: 900px;
  height: 195px;
  background-color: #e8c8a0;
  padding: 35px;
  padding-bottom: 0px;
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

.title {
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

.support1 {
  background-color: #e8c8a0;
  padding: 3px;
  width: 900px;
  height: 30px;
  flex-direction: column;
  padding-left: 35px;
  padding-right: 35px;
}

.support2 {
  background-color: #d2b48c;
  padding: 3px;
  width: 900px;
  height: 30px;
  flex-direction: column;
  padding-left: 35px;
  padding-right: 35px;
}

.router {
  text-decoration: none;
  color: black;
}

.book img {
  width: 126px;
  height: 160px;
}

.container {
  display: flex;
  align-items: center;
  justify-content: space-between; /* 각 요소 사이의 공간을 균등하게 배분 */
}

.line {
  margin-bottom: 20px;
  box-shadow: 3px 3px 3px gray;
}

.prev {
  margin-right: 30px;
  border-bottom: 20px solid transparent;
  border-top: 20px solid transparent;
  border-left: 20px solid transparent;
  border-right: 20px solid black;
}

.next {
  margin-left: 30px;
  border-bottom: 20px solid transparent;
  border-top: 20px solid transparent;
  border-left: 20px solid black;
  border-right: 20px solid transparent;
}
</style>
