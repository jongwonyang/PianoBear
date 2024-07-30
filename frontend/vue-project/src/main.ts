import { createApp } from "vue";
import { createPinia } from "pinia";
import axios from "axios";
import vuetify from "./plugins/vuetify";
import { createRouter, createWebHistory } from "vue-router";
import App from "./App.vue";
import router from "./router";

const app = createApp(App);


app.use(createPinia());
app.use(router);
app.use(vuetify);
app.mount("#app");

