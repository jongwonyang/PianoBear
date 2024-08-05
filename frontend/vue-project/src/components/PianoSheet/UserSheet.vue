<template>
  <div>
    <div class="page" v-if="currentSortOption === 0">
      <v-card elevation="5">
        <div>
          <div class="bookshelf1">
            <div class="shelf">
              <router-link
                v-for="(book, index) in userFavoriteList.filter(
                  (book, index) => bookCount <= index / 5 && index / 5 < bookCount + 1
                )"
                :key="index"
                :to="`/main/piano-sheet/${book.id}`"
                class="router"
              >
                <div class="book">
                  <img src="@/assets/images/정수 거울셀카.jpg" alt="Book Image" />
                </div>
                <!-- <div class="book">
                  <img :src="book.musicImg" alt="Book Image" />
                  {{ book.musicImg }}
                </div> -->
              </router-link>
            </div>
          </div>
          <div class="support1">
            <div class="support">
              <div
                v-for="(book, index) in userFavoriteList.filter(
                  (book, index) => bookCount <= index / 5 && index / 5 < bookCount + 1
                )"
                :key="index"
                class="title"
              >
                {{ book.id }}번 악보
              </div>
            </div>
          </div>
        </div>
      </v-card>
      <div class="my-5"></div>
      <v-card elevation="5">
        <div>
          <div class="bookshelf2">
            <div class="shelf">
              <router-link
                v-for="(book, index) in userFavoriteList.filter(
                  (book, index) => bookCount + 1 <= index / 5 && index / 5 < bookCount + 2
                )"
                :key="index"
                :to="`/main/piano-sheet/${book.id}`"
                class="router"
              >
                <div class="book">
                  <img src="@/assets/images/정수 거울셀카.jpg" alt="Book Image" />
                </div>
                <!-- <div class="book">
                  <img :src="book.musicImg" alt="Book Image" />
                  {{ book.musicImg }}
                </div> -->
              </router-link>
            </div>
          </div>
          <div class="support2">
            <div class="support">
              <div
                v-for="(book, index) in userFavoriteList.filter(
                  (book, index) => bookCount + 1 <= index / 5 && index / 5 < bookCount + 2
                )"
                :key="index"
                class="title"
              >
                {{ book.id }}번 악보
              </div>
            </div>
          </div>
        </div>
      </v-card>
      <div>
        <v-btn @click="downCount">prev</v-btn>
        <v-btn @click="upCount">next</v-btn>
      </div>
    </div>

    <div class="page" v-if="currentSortOption === 1">
      <v-card elevation="5">
        <div>
          <div class="bookshelf1">
            <div class="shelf">
              <router-link
                v-for="(book, index) in userPracticeList.filter(
                  (book, index) => bookCount <= index / 5 && index / 5 < bookCount + 1
                )"
                :key="index"
                :to="`/main/piano-sheet/${book.id}`"
                class="router"
              >
                <div class="book">{{ book.musicImg }}</div>
              </router-link>
            </div>
          </div>
          <div class="support1">
            <div class="support">
              <div
                v-for="(book, index) in userPracticeList.filter(
                  (book, index) => bookCount <= index / 5 && index / 5 < bookCount + 1
                )"
                :key="index"
                class="title"
              >
                {{ book.id }}번 악보
              </div>
            </div>
          </div>
        </div>
      </v-card>
      <div class="my-5"></div>
      <v-card elevation="5">
        <div>
          <div class="bookshelf2">
            <div class="shelf">
              <router-link
                v-for="(book, index) in userPracticeList.filter(
                  (book, index) => bookCount + 1 <= index / 5 && index / 5 < bookCount + 2
                )"
                :key="index"
                :to="`/main/piano-sheet/${book.id}`"
                class="router"
              >
                <div class="book">{{ book.musicImg }}</div>
              </router-link>
            </div>
          </div>
          <div class="support2">
            <div class="support">
              <div
                v-for="(book, index) in userPracticeList.filter(
                  (book, index) => bookCount + 1 <= index / 5 && index / 5 < bookCount + 2
                )"
                :key="index"
                class="title"
              >
                {{ book.id }}번 악보
              </div>
            </div>
          </div>
        </div>
      </v-card>
      <div>
        <v-btn @click="downCount">prev</v-btn>
        <v-btn @click="upCount">next</v-btn>
      </div>
    </div>

    <div class="page" v-if="currentSortOption === 2">
      <v-card elevation="5">
        <div>
          <div class="bookshelf1">
            <div class="shelf">
              <router-link
                v-for="(book, index) in userUploadList.filter(
                  (book, index) => bookCount <= index / 5 && index / 5 < bookCount + 1
                )"
                :key="index"
                :to="`/main/piano-sheet/${book.id}`"
                class="router"
              >
                <div class="book">{{ book.musicImg }}</div>
              </router-link>
            </div>
          </div>
          <div class="support1">
            <div class="support">
              <div
                v-for="(book, index) in userUploadList.filter(
                  (book, index) => bookCount <= index / 5 && index / 5 < bookCount + 1
                )"
                :key="index"
                class="title"
              >
                {{ book.id }}번 악보
              </div>
            </div>
          </div>
        </div>
      </v-card>
      <div class="my-5"></div>
      <v-card elevation="5">
        <div>
          <div class="bookshelf2">
            <div class="shelf">
              <router-link
                v-for="(book, index) in userUploadList.filter(
                  (book, index) => bookCount + 1 <= index / 5 && index / 5 < bookCount + 2
                )"
                :key="index"
                :to="`/main/piano-sheet/${book.id}`"
                class="router"
              >
                <div class="book">{{ book.musicImg }}</div>
              </router-link>
            </div>
          </div>
          <div class="support2">
            <div class="support">
              <div
                v-for="(book, index) in userUploadList.filter(
                  (book, index) => bookCount + 1 <= index / 5 && index / 5 < bookCount + 2
                )"
                :key="index"
                class="title"
              >
                {{ book.id }}번 악보
              </div>
            </div>
          </div>
        </div>
      </v-card>
      <div>
        <v-btn @click="downCount">prev</v-btn>
        <v-btn @click="upCount">next</v-btn>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from "vue";
import { usePianoSheetStore } from "@/stores/pianosheet";
// import type { UserSheet } from "@/stores/pianosheet";

const store = usePianoSheetStore();
const bookCount = ref<number>(0);
const pageCount = ref<number>(0);
const currentSortOption = ref<number>(0);

const maxCount = computed<number>(() => Math.floor((store.userSheetList.length - 1) / 10));

const downCount = function (): void {
  if (bookCount.value > 0) {
    bookCount.value -= 2;
    pageCount.value -= 1;
  }
};
const upCount = function (): void {
  if (maxCount.value > pageCount.value) {
    bookCount.value += 2;
    pageCount.value += 1;
  }
};

// 즐겨찾기, 연습량, 등록날짜 기준으로 데이터 리스트 가져오기
const userFavoriteList = computed(() => store.userFavoriteList);
const userPracticeList = computed(() => store.userPracticeList);
const userUploadList = computed(() => store.userUploadList);

// const currentList = computed<UserSheet[]>(() => {
//   switch (currentSortOption.value) {
//     case 0: // 즐겨찾기
//       return store.userFavoriteList;
//     case 1: // 연습량
//       return store.userPracticeList;
//     case 2: // 등록날짜
//       return store.userUploadList;
//   }
// });

const props = defineProps<{
  sortOption: number;
}>();

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
  height: 70vh; /* 전체 화면 높이 */
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
</style>
