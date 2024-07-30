<template>
  <div>
    <v-menu location="end">
      <template v-slot:activator="{ props }">
        <v-btn v-bind="props" icon="mdi-dots-horizontal" :width="25" :height="25">
        </v-btn>
      </template>
      <v-list lines="one">
        <v-list-item v-for="(item, index) in items" :key="index">
          <v-list-item-title>{{ item }}</v-list-item-title>
        </v-list-item>
      </v-list>
    </v-menu>
  </div>
</template>
<script lang="ts" setup>
    import {ref, defineProps} from 'vue';

    const props = defineProps({
      next: String
    });
    
    const items = ref(['음량조절', '음소거', '친구추가'])

</script>

<style scoped>
</style>