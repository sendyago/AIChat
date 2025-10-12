import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import mdParser from './components/MarkdownIt';


import 'highlight.js/styles/github-dark.css';
import '@/assets/styles/main.css'
import '@/assets/styles/home.css'

const app = createApp(App)

// const mdParserSymbol = Symbol('mdParser');
// app.provide(mdParserSymbol, mdParser);

app.config.globalProperties.$mdParser = mdParser;

app.use(router)
app.mount('#app')
