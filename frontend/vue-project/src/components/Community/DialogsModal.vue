<template>
<v-dialog max-width="500">
  <template v-slot:activator="{ props: activatorProps }">
    <v-btn
      v-bind="activatorProps"
      :text="props.text"
    ></v-btn>
  </template>

  <template v-slot:default="{ isActive }">
    <v-card title="알림">
      <v-card-text>
        {{props.content}}
      </v-card-text>

      <v-card-actions>
        <v-spacer></v-spacer>

        <v-btn
          :text="props.text"
          @click="emit('join')"
        ></v-btn>
        <v-btn
          text="취소"
          @click="isActive.value = false"
        ></v-btn>
      </v-card-actions>
    </v-card>
  </template>
</v-dialog>
  </template>
<script lang="ts" setup>
    import {useRouter, useRoute} from 'vue-router';
    import {defineProps, defineEmits} from 'vue';
    const route = useRoute();
    const router = useRouter();
    const props = defineProps({
        text: String,
        content: String,
    });
    const emit = defineEmits([
        'join'
    ]);

</script>
<style scoped>
</style>