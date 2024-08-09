import { fileURLToPath, URL } from "node:url";

import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import VueDevTools from "vite-plugin-vue-devtools";
// https://vitejs.dev/config/
export default defineConfig({
  build: {
    emptyOutDir: true, // also necessary
  },
  plugins: [
    vue({
      template: {
        compilerOptions: { isCustomElement: (tag) => {
          if (tag.startsWith("md-")) return true;
          if (["v-list-item-content", "html-midi-player"].includes(tag)) return true;
          return false;
        }},
      },
    }),
    VueDevTools(),
  ],
  resolve: {
    alias: {
      "@": fileURLToPath(new URL("./src", import.meta.url)),
    },
  },
  define: { global: "window" },
});
