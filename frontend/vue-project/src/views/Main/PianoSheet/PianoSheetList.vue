<template>
    <div>
      <div>
        <div class="searchbar">
          <SearchBar />
        </div>
        <div class="card">
          <v-tabs v-model="tab" align-tabs="start" color="#D2B48C" hide-slider height="80px">
            <v-tab :value="1" @click="setCurrentTab('UserSheet')" :class="{ active: currentTab === 'UserSheet' }">
              <v-tooltip text="oo이 악보">
                <template v-slot:activator="{ props }">
                  <span><img src="@/assets/characters/토니/토니머리.png" alt="" v-bind="props"/></span>
                </template>
              </v-tooltip>
            </v-tab>
            <v-tab :value="2" @click="setCurrentTab('BasicSheet')" :class="{ active: currentTab === 'BasicSheet' }">
              <v-tooltip text="기본 악보">
                <template v-slot:activator="{ props }">
                  <span><img src="@/assets/characters/피치/피치머리.png" alt="" v-bind="props"/></span>
                </template>
              </v-tooltip>
            </v-tab>
          </v-tabs>
          <component :is="currentTabComponent" />
        </div>
      </div>
      <div class="upload">
        <router-link to="/main/piano-sheet/upload">
          <v-btn variant="tonal" height="7vh" color="#81C784" size="x-large">
            <!-- <v-icon prepend>mdi-book-plus-outline</v-icon> -->
            악보 업로드
          </v-btn>
        </router-link>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, computed, onMounted } from 'vue';
  import UserSheet from '@/components/PianoSheet/UserSheet.vue';
  import BasicSheet from '@/components/PianoSheet/BasicSheet.vue';
  import SearchBar from '@/components/PianoSheet/SearchBar.vue';
  import { usePianoSheetStore } from "@/stores/pianosheet";

  const store = usePianoSheetStore();
  const tab = ref(1); 
  const currentTab = ref('UserSheet'); 
  
  const setCurrentTab = (tabName) => {
    currentTab.value = tabName;
  };
  
  const currentTabComponent = computed(() => {
    return currentTab.value === 'UserSheet' ? UserSheet : BasicSheet;
  });

  onMounted(async () => {
    await store.fetchPianoSheets();
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
  
  .a {
    margin-top: 17px;
  }
  
  .upload {
    margin-top: 10px;
    float: right;
  }
  
  a {
    text-decoration: none;
    color: #D2B48C;
  }
  
  .searchbar {
    width: 300px;
    margin-left: auto;
    margin-top: 50px;
    margin-bottom: -20px;
  }
  </style>
  