import { defineConfig } from 'vite';
import squint from "./vite-plugin-squint/index.mjs";
//import squint from "vite-plugin-squint";
import solid from 'vite-plugin-solid';
import devtools from 'solid-devtools/vite';
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
              squint({scan: true}),
              solid(),
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
    copyPublicDir: false,
    rollupOptions: {
      external: ['/src/dev/.*']
    },
    target: 'esnext',
  },
});
