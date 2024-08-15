<template>
  <div>
    <div class="searchbar">
      <SearchBar
        :currentTab="currentTab"
        @update:sortOption="updateSort"
        @update:searchText="updateSort"
      />
    </div>

    <div class="tabs">
      <Tabs :currentTab="currentTab" @update:currentTab="setCurrentTab" />
    </div>
    <div>
      <component :is="currentTabComponent" :sortOption="currentSortOption" />
    </div>

    <div>
      <div class="upload">
        <router-link to="/main/piano-sheet/upload">
          <button>악보 업로드</button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { usePianoSheetStore } from "@/stores/pianosheet";
import UserSheet from "@/components/PianoSheet/UserSheet.vue";
import BasicSheet from "@/components/PianoSheet/BasicSheet.vue";
import SearchBar from "@/components/PianoSheet/SearchBar.vue";
import Tabs from "@/components/PianoSheet/Tabs.vue";

const store = usePianoSheetStore();
const currentTab = ref<string>("UserSheet");
const currentSortOption = ref<number>(0);

const updateSort = (index: number) => {
  currentSortOption.value = index;
  store.sortOption = index;
};

const setCurrentTab = (tabName: string) => {
  currentTab.value = tabName;
};

const currentTabComponent = computed(() => {
  return currentTab.value === "UserSheet" ? UserSheet : BasicSheet;
});

onMounted(() => {
  const savedSortOption = store.sortOption;
  if (savedSortOption) {
    currentSortOption.value = savedSortOption;
  }
});

onMounted(async () => {
  await store.userSheetListfun();
  await store.basicSheetListfun();
});
</script>

<style scoped>
.upload button {
  float: right;
  background-color: #fffff8;
  color: #73796e;
  height: 8vh;
  width: 12vw;
  font-size: 130%;
  font-weight: bold;
  border-radius: 5%;
  box-shadow: 0.2vw 0.3vh 0.6vh gray;
  margin-right: 5vw;
}

.searchbar {
  width: 20vw;
  margin-left: auto;
  margin-bottom: -6vh;
  margin-right: 5vw;
}

.container {
  display: flex;
  align-items: center;
  justify-content: space-between; /* 각 요소 사이의 공간을 균등하게 배분 */
}

.tabs {
  margin-left: 6vw;
}
</style>
