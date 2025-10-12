import {createRouter, createWebHistory} from 'vue-router'

const routes = [
    {
        path: '/',
        name: 'chatHome',
        component: () => import('../views/chat/index.vue')
    },
    // {
    //     path: '/*',
    //     name: 'other',
    //     redirect: '/'
    // }
]

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
})

export default router