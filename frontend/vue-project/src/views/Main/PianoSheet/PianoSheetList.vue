<template>
  <div>
    <div class="searchbar">
      <SearchBar
        :currentTab="currentTab"
        @update:sortOption="updateSort"
        @update:searchText="updateSort"
      />
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
    <div>
      <div class="upload">
        <router-link to="/main/piano-sheet/upload">
          <button>악보업로드</button>
          <!-- <v-btn variant="tonal" height="7vh" color="#81C784" size="x-large"> 악보 업로드 </v-btn> -->
        </router-link>
      </div>
      <div>
        <img src="@/assets/characters/토니/토니응원.png" alt="" class="bottom" />
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
  background-color: #d9f6d9;
  color: #73796e;
  height: 8vh;
  width: 12vw;
  font-size: large;
  font-weight: bold;
  border-radius: 10%;
  box-shadow: 0.2vw 0.3vh 0.6vh gray;
  margin-top: 1.5vh;
}

.searchbar {
  width: 20vw;
  margin-left: auto;
  margin-bottom: -5vh;
}

.container {
  display: flex;
  align-items: center;
  justify-content: space-between; /* 각 요소 사이의 공간을 균등하게 배분 */
}

.tabs {
  margin-left: 8vw;
}

.bottom {
  width: 5vw;
}
</style>
