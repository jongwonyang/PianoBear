<template>
  <v-text-field
    density="compact"
    label="악보를 검색해봐요!"
    variant="outlined"
    @click:prepend-inner="toggleMenu"
    v-model="store.searchText"
  >
    <template v-slot:prepend-inner>
      <v-menu v-model:menu="menu" location="start">
        <template v-slot:activator="{ props }">
          <v-icon v-bind="props">mdi-menu</v-icon>
        </template>
        <v-list class="list" style="background-color: #fff9e0" elevation="2" font-weight="bold">
          <v-list-item
            v-for="(item, index) in displayedItems"
            :key="index"
            @click="changeSort(index)"
          >
            <v-list-item-title>{{ item.title }}</v-list-item-title>
          </v-list-item>
        </v-list>
      </v-menu>
    </template>
  </v-text-field>
</template>

<script lang="ts" setup>
import { defineEmits, ref, computed } from "vue";
import { usePianoSheetStore } from "@/stores/pianosheet";

const emit = defineEmits<{
  (event: "update:sortOption", sortOption: number): void;
}>();

const props = defineProps<{
  currentTab: string;
}>();

const store = usePianoSheetStore();
const menu = ref(false);
const searchText = ref("");
store.searchText = searchText.value;

const userSheetItems = [{ title: "즐겨찾기 순" }, { title: "연습량 순" }, { title: "등록 순" }];

const basicSheetItems = [{ title: "즐겨찾기 순" }, { title: "연습량 순" }];

const displayedItems = computed(() => {
  return props.currentTab === "UserSheet" ? userSheetItems : basicSheetItems;
});

const toggleMenu = () => {
  menu.value = !menu.value;
};

const changeSort = (index: number) => {
  emit("update:sortOption", index);
};
</script>

<style scoped>
.list {
  color: #947650;
}
</style>
