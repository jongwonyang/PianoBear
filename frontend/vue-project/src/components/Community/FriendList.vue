<template>
  <div>
    <v-menu location="top">
      <template v-slot:activator="{ props }">
        <v-btn v-bind="props" icon="mdi-account-plus" @click="getFriends()">
        </v-btn>
      </template>

      <v-list lines="one">
        <v-list-subheader v-if="friends.length != 0"
          >친구 초대</v-list-subheader
        >
        <v-list-subheader v-if="friends.length == 0"
          >온라인인 친구가 없어요.</v-list-subheader
        >
        <v-list-item v-for="(item, index) in friends" :key="item.id">
          <InviteDialog
            :user="item"
            :disabled="invitedFriends.includes(item.id)"
          ></InviteDialog>
        </v-list-item>
      </v-list>
    </v-menu>
  </div>
</template>
<script lang="ts" setup>
import { useDashboardStore } from "@/stores/dashboard";
import { ref } from "vue";
import InviteDialog from "./InviteDialog.vue";
import { useOpenviduStore } from "@/stores/community";

type Friend = {
  id: string;
  name: string;
  profilePic: string;
  statusMessage: string;
};

const dashboardStore = useDashboardStore();
const communityStore = useOpenviduStore();

const friends = ref<Friend[]>([]);
const invitedFriends = ref<String[]>([]);

function getFriends() {
  communityStore.getInvitedFriends().then((data) => {
    invitedFriends.value = data.data;
    dashboardStore.GetOnlineFriends().then((data) => {
      friends.value = data.data;
    });
  });
}
</script>
<style scoped></style>
