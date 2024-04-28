import { defineConfig } from 'vite';
import solidPlugin from 'vite-plugin-solid';
import devtools from 'solid-devtools/vite';
import WindiCSS from 'vite-plugin-windicss';

export default defineConfig({
  plugins: [WindiCSS({
              scan: {
                dirs: ['public/js'],
                fileExtensions: ["js","jsx"],
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
    target: 'esnext',
  },
});
