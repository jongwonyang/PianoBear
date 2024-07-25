<template>
    <RouterView />
</template>

<script setup lang="ts">
import { RouterView } from 'vue-router'
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;700&display=swap');

* {
  font-family: 'Inter', sans-serif;
}

html,
body {
  position: relative;
  width: 100%;
  height: 100%;
  background: #FFFFF8;
  display: flex;
  justify-content: center;
  align-items: center;
  font-family: 'Arial', sans-serif;
  margin: 0;
  padding: 0;
  --md-sys-color-outline: #FFFFFF; /* 기본상태 테두리 색상 */
  --md-sys-color-on-surface: #947650; /* 호버상태 테두리 색상 */
  --md-sys-color-primary: #947650; /* 포커스상태 테두리 색상 */
  --md-elevation-level: 5; /* 그림자 레벨 */
  --md-sys-color-shadow: #d2b659; /* 그림자 색상 */
}
</style>
