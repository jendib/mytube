import vue from 'eslint-plugin-vue'
import prettier from 'eslint-plugin-prettier'
import configPrettier from 'eslint-config-prettier'

export default [
  ...vue.configs['flat/recommended'],
  configPrettier,
  {
    plugins: {
      prettier: prettier
    },
    rules: {
      'prettier/prettier': 'error',
      'vue/multi-word-component-names': 'off'
    }
  }
]
