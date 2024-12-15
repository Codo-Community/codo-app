import { defineConfig } from 'vite';
import squint from "@w3t-ab/vite-plugin-squint"
import solid from 'vite-plugin-solid';
import devtools from 'solid-devtools/vite';
import UnoCSS from 'unocss/vite'
import path from "path";
import basicSsl from '@vitejs/plugin-basic-ssl';
import { nodePolyfills } from 'vite-plugin-node-polyfills'

const isDev = () => process.env.NODE_ENV === 'development';
const isPreview = () => process.env.NODE_ENV === 'preview';


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
//              (isDev() || isPreview()) && basicSsl(),
                nodePolyfills(),
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
  resolve: {
    alias: {
      "~": path.resolve(__dirname, "./src")
    }
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
