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
          <v-list>
            <v-list-item v-for="(item, index) in items" :key="index">
              <v-list-item-title>{{ item.title }}</v-list-item-title>
            </v-list-item>
          </v-list>
        </v-menu>
      </template>
    </v-text-field>
  </template>
  
  <script setup>
    import { ref } from 'vue'
  
    const loading = ref(false)
    const loaded = ref(false)
    const menu = ref(false)
  
    const items = ref([
      { title: '연습량 순' },
      { title: '가나다 순' },
      { title: '악보등록 순' },
      { title: '즐겨찾기 순' },
    ])
  
    const toggleMenu = () => {
      menu.value = !menu.value
    }
  
    const onClickAppend = () => {
      loading.value = true
  
      setTimeout(() => {
        loading.value = false
        loaded.value = true
      }, 2000)
    }
  </script>
  
  <style scoped>
    
  </style>
  