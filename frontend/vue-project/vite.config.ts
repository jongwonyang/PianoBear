import { fileURLToPath, URL } from "node:url";

import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";

// https://vitejs.dev/config/
export default defineConfig({
  build: {
    outDir: "../../backend/application/src/main/resources/static",
    emptyOutDir: true, // also necessary
  },
  plugins: [vue({template: {compilerOptions: {isCustomElement: (tag) => tag.startsWith("md-")}}})], 
  resolve: {
    alias: {
      "@": fileURLToPath(new URL("./src", import.meta.url)),
    },
  },
  define: {
    global: "window",
  }
});
