import { defineConfig, presetAttributify, presetUno, transformerDirectives, transformerCompileClass, transformerVariantGroup } from 'unocss'
import presetWebFonts from '@unocss/preset-web-fonts'
import presetIcons from '@unocss/preset-icons'
import { presetWind } from '@unocss/preset-wind'

export default defineConfig({
 transformers: [
    transformerVariantGroup(),
    transformerDirectives(),
    transformerCompileClass(),
  ],
  content: {
    //filesystem: ['/home/daniel/repos/solid-squint/node_modules/.pnpm/flowbite@2.3.0/node_modules/flowbite/**/*{.js,.ts}'],
    pipeline: {
      include: [
        // the default
        /\.(vue|svelte|[jt]sx|mdx?|astro|elm|php|phtml|html)($|\?)/,
        // include js/ts files
        //'./**/*.{js,ts}',
        './node_modules/flowbite/**/*{.js,.ts}',
        './node_modules/flowbite-datepicker/dist/**/*{.js,.ts}'
      ],
      // exclude files
      // exclude: []
    },
  },
  presets: [
    presetUno(),
    presetWind(),
    //presetFlowbite(),
    presetIcons({ /* options */ }),
    presetWebFonts({
  provider: 'google', // default provider
  fonts: {
    // these will extend the default theme
    sans: 'Montserrat',
    mono: ['Fira Code', 'Fira Mono:400,700'],
    // custom ones
    lobster: 'Lobster',
    lato: [
      {
        name: 'Lato',
        weights: ['400', '700'],
        italic: true,
      },
      {
        name: 'sans-serif',
        provider: 'none',
      },
    ],
  },
})
  ],
})
