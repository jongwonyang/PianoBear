
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue';
import { createVuetify } from 'vuetify';
import 'vuetify/styles';
import { aliases, mdi } from 'vuetify/iconsets/mdi';
import * as components from 'vuetify/components';
import * as directives from 'vuetify/directives';
import router from './router'

const vuetify = createVuetify({
    components,
    directives,
    icons: {
      defaultSet: 'mdi',
      aliases,
      sets: {
        mdi,
      },
    },
});
  
const app = createApp(App);

app.use(vuetify);
app.use(createPinia())
app.use(router)

app.mount('#app')
