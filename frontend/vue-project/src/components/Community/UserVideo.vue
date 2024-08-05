<template>
  <!-- <Bear class="bear" /> -->
  <OvVideo
    class="video"
    :stream-manager="streamManager"
    :elevation="3"
    rounded="lg"
  ></OvVideo>
</template>

<script>
import Menu from "./Menu.vue";
import OvVideo from "./OvVideo.vue";

export default {
  name: "UserVideo",

  components: {
    OvVideo,
  },

  props: {
    streamManager: Object,
  },

  computed: {
    clientData() {
      const { clientData } = this.getConnectionData();
      return clientData;
    },
  },

  methods: {
    getConnectionData() {
      const { connection } = this.streamManager.stream;
      return JSON.parse(connection.data);
    },
  },
};
</script>

<style scoped></style>
