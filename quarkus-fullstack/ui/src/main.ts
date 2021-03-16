import { createApp } from 'vue'
import App from './App.vue'
import './index.css'
import router from './router'
//import store from './store'
import { store, key } from './store'
import axios from 'axios'
//import VueAxios from 'vue-axios'

axios.defaults.baseURL = process.env.VUE_APP_API

createApp(App)
    .use(store, key)
    .use(router)
    .mount('#app')
