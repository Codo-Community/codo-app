import { defineConfig } from 'vite';
import squint from "vite-plugin-squint";
import solid from 'vite-plugin-solid';
import devtools from 'solid-devtools/vite';
//import WindiCSS from 'vite-plugin-windicss';
//import formsPlugin from 'windicss/plugin/forms';
//import flowbite from 'flowbite/plugin-windicss';
//import flowbite from "flowbite";
import UnoCSS from 'unocss/vite'

export default defineConfig({
  root: 'dist',
  plugins: [UnoCSS({configFile: "./uno.config.ts"}),
  // WindiCSS({
    //                   scan: {
    //                     dirs: ["","./node_modules/flowbite/**/*.js"],
    //                     fileExtensions: ["js","jsx","mjs"],
    //                   },
    // theme: {
    //   fontFamily: {
    //     sans: ['Montserrat', 'sans-serif'],
    //     serif: ['Merriweather', 'serif'],
    //   },
    //   extend: {
    //     screens: {
    //       '3xl': '1920px'
    //     },
    //     colors: {
    //       'gray-600': '#1E1E25',
    //       'gray-700': '#101014',
    //       'gray-800': '#080708',
    //       'blue-700': '#4FA1FF',
    //     },
    //   },
    // },
    //              plugins: [formsPlugin,
    //                  flowbite.plugin]}),
    devtools({
                autoname: true, // e.g. enable autoname
              }),
//            squint(),
          solid()],
  server: {
    port: 3000,
  },
  optimizeDeps: { // 👈 optimizedeps
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
    outDir: "public/",
    target: 'esnext',
  },
});
