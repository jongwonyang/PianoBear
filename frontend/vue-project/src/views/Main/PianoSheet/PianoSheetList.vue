<template>
  <div>
    <div class="searchbar">
      <SearchBar :currentTab="currentTab" @update:sortOption="updateSort" />
    </div>
    <div class="container">
      <div class="card">
        <div class="tabs">
          <Tabs :currentTab="currentTab" @update:currentTab="setCurrentTab" />
        </div>
        <div>
          <component :is="currentTabComponent" :sortOption="currentSortOption" />
        </div>
      </div>
    </div>
    <div class="upload">
      <router-link to="/main/piano-sheet/upload">
        <v-btn variant="tonal" height="7vh" color="#81C784" size="x-large"> 악보 업로드 </v-btn>
      </router-link>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from "vue";
import { usePianoSheetStore } from "@/stores/pianosheet";
import UserSheet from "@/components/PianoSheet/UserSheet.vue";
import BasicSheet from "@/components/PianoSheet/BasicSheet.vue";
import SearchBar from "@/components/PianoSheet/SearchBar.vue";
import Tabs from "@/components/PianoSheet/Tabs.vue";

const store = usePianoSheetStore();
const tab = ref<number>(1);
const currentTab = ref<string>("UserSheet");
const currentSortOption = ref<number>(0);
const bookCount = ref<number>(0);
const pageCount = ref<number>(0);

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

// 페이지 로드 시 localStorage에서 정렬 기준을 불러옵니다.
onMounted(() => {
  const savedSortOption = localStorage.getItem("sortOption");
  if (savedSortOption) {
    currentSortOption.value = parseInt(savedSortOption, 10);
  }
});

// 사용자가 정렬 기준을 변경하면 localStorage에 저장합니다.
const updateSort = (index: number) => {
  console.log("Parent Component: updateSort index:", index);
  currentSortOption.value = index;
  localStorage.setItem("sortOption", index.toString());
};

const setCurrentTab = (tabName: string) => {
  currentTab.value = tabName;
};

const currentTabComponent = computed(() => {
  return currentTab.value === "UserSheet" ? UserSheet : BasicSheet;
});

onMounted(async () => {
  await store.userSheetListfun();
  // await store.basicSheetListfun();
});
</script>

<style scoped>
img {
  width: 80px;
  height: 80px;
  margin-top: 30px;
}

.v-tab.active img {
  margin-top: 5px;
  width: 80px;
  height: 80px;
}

.upload {
  float: right;
}

a {
  text-decoration: none;
  color: #d2b48c;
}

.searchbar {
  width: 300px;
  margin-left: auto;
  margin-bottom: -20px;
}

.container {
  display: flex;
  align-items: center;
  justify-content: space-between; /* 각 요소 사이의 공간을 균등하게 배분 */
}

.tabs {
  margin-left: 70px;
}
</style>
