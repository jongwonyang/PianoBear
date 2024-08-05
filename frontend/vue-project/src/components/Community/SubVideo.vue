<template>
  <!-- <Bear class="bear" /> -->
  <OvVideo
    :stream-manager="streamManager"
    :elevation="3"
    rounded="lg"
  ></OvVideo>
  <div class="menus">
    <Menu :next="'1'" />
  </div>
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
    surface: String,
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

<style scoped>
.menus {
  display: flex;
  justify-content: right;
  padding: 2%;
  opacity: 0.7;
  z-index: 100;
}
</style>
