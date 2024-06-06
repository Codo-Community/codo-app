import { defineConfig, presetAttributify, presetUno } from 'unocss'
import { presetFlowbite } from '@julr/unocss-preset-flowbite'
import transformerVariantGroup from '@unocss/transformer-variant-group'
import presetWebFonts from '@unocss/preset-web-fonts'
import presetIcons from '@unocss/preset-icons'

export default defineConfig({
 transformers: [
    transformerVariantGroup(),
  ],
  presets: [
    presetUno(),
    presetFlowbite(),
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
