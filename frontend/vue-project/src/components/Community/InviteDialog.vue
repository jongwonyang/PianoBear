<template>
  <v-dialog max-width="500">
    <template v-slot:activator="{ props: activatorProps }">
      <v-btn
        v-bind="activatorProps"
        variant="text"
        prepend-icon="mdi-account"
        append-icon="mdi-plus"
      >
        &nbsp; {{ props.user.name }}</v-btn
      >
    </template>

    <template v-slot:default="{ isActive }">
      <v-card title="알림">
        <v-card-text> {{ props.user.name }}을(를) 초대할까요? </v-card-text>

        <v-card-actions>
          <v-spacer></v-spacer>

          <v-btn
            @click="
              invite(props.user.id);
              isActive.value = false;
            "
            >초대</v-btn
          >
          <v-btn @click="isActive.value = false">취소</v-btn>
        </v-card-actions>
      </v-card>
    </template>
  </v-dialog>
</template>
<script lang="ts" setup>
import { useRouter, useRoute } from "vue-router";
import { defineProps, defineEmits, type PropType } from "vue";
import { useOpenviduStore } from "@/stores/community";
import { ref } from "vue";

type Friend = {
  id: string;
  name: string;
  profilePic: string;
  statusMessage: string;
};

const route = useRoute();
const router = useRouter();
const props = defineProps({
  user: {
    type: Object as PropType<Friend>,
    required: true,
  },
  disabled: {
    type: Boolean,
    required: true,
  },
});
const invitedTemp = ref(false);

const communityStore = useOpenviduStore();

function invite(FriendId: string) {
  communityStore.inviteFriend(FriendId);
}
</script>
<style scoped></style>
