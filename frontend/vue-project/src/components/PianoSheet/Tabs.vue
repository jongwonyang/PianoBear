<template>
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
</template>

<script setup lang="ts">
import { ref, watch } from "vue";

const props = defineProps<{ currentTab: string }>();
const emit = defineEmits<{ (e: "update:currentTab", value: string): void }>();

const tab = ref<number>(props.currentTab === "UserSheet" ? 1 : 2);

watch(
  () => props.currentTab,
  (newVal) => {
    tab.value = newVal === "UserSheet" ? 1 : 2;
  }
);

const setCurrentTab = (tabName: string) => {
  emit("update:currentTab", tabName);
};
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
</style>
