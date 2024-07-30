<template>
  <div>
    <v-menu location="top">
      <template v-slot:activator="{ props }">
        <v-btn v-bind="props" icon="mdi-account-plus">
        </v-btn>
      </template>
      <v-list lines="one">
        <v-list-item v-for="(item, index) in friends" :key="index">
            <v-btn variant="text" prepend-icon="mdi-account" append-icon="mdi-plus"> &nbsp; {{ item }}</v-btn>
        </v-list-item>
      </v-list>
    </v-menu>
  </div>
</template>
<script lang="ts" setup>
    import {ref} from 'vue';

    const friends = ref(['김태성', '서근범', '하정수']);

</script>
<style scoped>
</style>