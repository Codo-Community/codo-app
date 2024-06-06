import { defineConfig } from 'vite-plugin-windicss'
import formsPlugin from 'windicss/plugin/forms'

export default defineConfig({
       scan: {
                dirs: [""],
                fileExtensions: ["js","jsx","mjs"],
              },
  theme: {
    fontFamily: {
      sans: ['Montserrat', 'sans-serif'],
      serif: ['Merriweather', 'serif'],
    },
    extend: {
     screens: {
        '3xl': '1920px'
      },
      colors: {
        'gray-600': '#1E1E25',
        'gray-700': '#101014',
        'gray-800': '#080708',
        'blue-700': '#4FA1FF',
      },

    },
  },
  plugins: [formsPlugin,
            require('flowbite/plugin-windicss')]
})
