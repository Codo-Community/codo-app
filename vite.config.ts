import { defineConfig } from 'vite';
import squint from "./vite-plugin/vite_plugin.js";
import solidPlugin from 'vite-plugin-solid';
import devtools from 'solid-devtools/vite';
//import WindiCSS from 'vite-plugin-windicss';
//import formsPlugin from 'windicss/plugin/forms';
//import flowbite from 'flowbite/plugin-windicss';
import flowbite from "flowbite";
import UnoCSS from 'unocss/vite'

export default defineConfig({
server: {
cors: { origin:     ["http://localhost:3000", "http://localhost:7007"] },
watch: {
      ignored: ['**/.clj-kondo/**',
                '**/node_modules/**'],
    },
port: 3000,
    fs: {
      strict: true,
      exclude: ['.clj-kondo/**/*']
    },
    //middlewareMode: true,
  },
  plugins: [
             UnoCSS({configFile: "./uno.config.ts"}),
    devtools({
                autoname: true, // e.g. enable autoname
              }),
              squint({options: {scan: "true"}}),
              solidPlugin(),
          ],
  esbuild: {
      drop: ['console', 'debugger'],
  },
  optimizeDeps: {
      esbuildOptions: {
        target: "esnext",
        // Node.js global to browser globalThis
        define: {
          global: 'globalThis'
        },
        supported: {
          bigint: true
        },
      },
  },
  build: {
    outDir: "dist/",
    rollupOptions: {
      external: ['/src/dev/.*']
    },
    target: 'esnext',
  },
});
