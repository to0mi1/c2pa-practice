import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vuetify from "vite-plugin-vuetify";
import { fileURLToPath, URL } from "node:url";

// https://vite.dev/config/
export default defineConfig({
    plugins: [
        vue(),
        vuetify(),
    ],
    server: {
        proxy: {
            '/api': 'http://localhost:8080'
        },
        fs: {
            // プロジェクトルート（frontend）だけでなく、
            // モノレポ構成などで上位にある node_modules も許可する
            allow: ['..']
        },
    },
    resolve: {
        alias: {
            '@': fileURLToPath(new URL('./src', import.meta.url))
        },
    },
})
