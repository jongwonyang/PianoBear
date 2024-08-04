<template>
  <div class="container">
    <div>
      <button class="prev"></button>
    </div>
    <div>
      <div class="searchbar">
        <SearchBar :currentTab="currentTab" @update-sort="updateSort" />
      </div>
      <div>
        <div class="card">
          <v-tabs v-model="tab" align-tabs="start" color="#D2B48C" hide-slider height="80px">
            <v-tab
              :value="1"
              @click="setCurrentTab('UserSheet')"
              :class="{ active: currentTab === 'UserSheet' }"
            >
              <v-tooltip text="oo이 악보">
                <template v-slot:activator="{ props }">
                  <span>
                    <img src="@/assets/characters/토니/토니머리.png" alt="" v-bind="props" />
                  </span>
                </template>
              </v-tooltip>
            </v-tab>
            <v-tab
              :value="2"
              @click="setCurrentTab('BasicSheet')"
              :class="{ active: currentTab === 'BasicSheet' }"
            >
              <v-tooltip text="기본 악보">
                <template v-slot:activator="{ props }">
                  <span>
                    <img src="@/assets/characters/피치/피치머리.png" alt="" v-bind="props" />
                  </span>
                </template>
              </v-tooltip>
            </v-tab>
          </v-tabs>
          <component :is="currentTabComponent" :sortOption="currentSortOption" />
        </div>
      </div>
      <div class="upload">
        <router-link to="/main/piano-sheet/upload">
          <v-btn variant="tonal" height="7vh" color="#81C784" size="x-large"> 악보 업로드 </v-btn>
        </router-link>
      </div>
    </div>
    <div>
      <button class="next"></button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from "vue";
import UserSheet from "@/components/PianoSheet/UserSheet.vue";
import BasicSheet from "@/components/PianoSheet/BasicSheet.vue";
import SearchBar from "@/components/PianoSheet/SearchBar.vue";
import { usePianoSheetStore } from "@/stores/pianosheet";

const store = usePianoSheetStore();
const tab = ref<number>(1);
const currentTab = ref<string>("UserSheet");
const currentSortOption = ref<number>(0);

const setCurrentTab = (tabName: string) => {
  currentTab.value = tabName;
};

const currentTabComponent = computed(() => {
  return currentTab.value === "UserSheet" ? UserSheet : BasicSheet;
});

const updateSort = (index: number) => {
  currentSortOption.value = index;
};

// onMounted(async () => {
//   await store.userSheetListfun();
//   await store.basicSheetListfun();
// });

// // Watch for sort option changes and update component data accordingly
// watch(currentSortOption, async (newOption) => {
//   // Depending on the newOption, you can fetch and filter data accordingly
//   // This is just an example, you may need to implement sorting logic in your components
// });
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
  margin-top: 10px;
  float: right;
}

a {
  text-decoration: none;
  color: #d2b48c;
}

.searchbar {
  width: 300px;
  margin-left: auto;
  margin-top: 50px;
  margin-bottom: -20px;
}

.container {
  display: flex;
  align-items: center;
  justify-content: space-between; /* 각 요소 사이의 공간을 균등하게 배분 */
}

.prev {
  margin-right: 10px;
  border-bottom: 20px solid transparent;
  border-top: 20px solid transparent;
  border-left: 20px solid transparent;
  border-right: 20px solid black;
}

.next {
  margin-left: 10px;
  border-bottom: 20px solid transparent;
  border-top: 20px solid transparent;
  border-left: 20px solid black;
  border-right: 20px solid transparent;
}
</style>
