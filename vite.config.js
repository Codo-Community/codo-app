import { defineConfig } from 'vite';
import solidPlugin from 'vite-plugin-solid';
import devtools from 'solid-devtools/vite';
import WindiCSS from 'vite-plugin-windicss';

export default defineConfig({
  root: 'dist',
  plugins: [WindiCSS({
              scan: {
                dirs: [""],
                fileExtensions: ["js","jsx","mjs"],
              }}),
            devtools({
              autoname: true, // e.g. enable autoname
            }),
            solidPlugin()
           ],
  server: {
    port: 3000,
  },
  build: {
    outDir: "public/js",
    target: 'esnext',
  },
});
