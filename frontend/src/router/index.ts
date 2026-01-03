import { createRouter, createWebHistory } from 'vue-router'
import VerifyView from "@/views/VerifyView.vue";
import SignView from "@/views/SignView.vue";

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            redirect: '/verify'
        },
        {
            path: '/verify',
            name: 'verify',
            component: VerifyView,
        },
        {
            path: '/sign',
            name: 'sign',
            component: SignView,
        },
    ],
})

export default router
