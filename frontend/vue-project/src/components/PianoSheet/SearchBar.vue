<template>
  <v-text-field
    :loading="loading"
    append-inner-icon="mdi-magnify"
    density="compact"
    label="악보를 검색해봐요!"
    variant="outlined"
    @click:prepend-inner="toggleMenu"
    @click:append-inner="onClickAppend"
  >
    <template v-slot:prepend-inner>
      <v-menu v-model:menu="menu" location="start">
        <template v-slot:activator="{ props }">
          <v-icon v-bind="props">mdi-menu</v-icon>
        </template>
        <v-list class="list" style="background-color: #fff9e0" elevation="2">
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

const emit = defineEmits<{
  (event: "update:sortOption", sortOption: number): void;
}>();

const props = defineProps<{
  currentTab: string;
}>();

const menu = ref(false);
const loading = ref(false);
const loaded = ref(false);

const userSheetItems = [{ title: "즐겨찾기 순" }, { title: "연습량 순" }, { title: "등록 순" }];

const basicSheetItems = [{ title: "즐겨찾기 순" }, { title: "연습량 순" }];

const displayedItems = computed(() => {
  return props.currentTab === "UserSheet" ? userSheetItems : basicSheetItems;
});

const toggleMenu = () => {
  menu.value = !menu.value;
};

const onClickAppend = () => {
  loading.value = true;
  setTimeout(() => {
    loading.value = false;
    loaded.value = true;
  }, 2000);
};

const changeSort = (index: number) => {
  console.log("Child Component: changeSort index:", index);
  emit("update:sortOption", index);
};
</script>

<style scoped>
.list {
  color: #947650;
}
</style>
