const {defineConfig} = require('@vue/cli-service')

module.exports = defineConfig({
    transpileDependencies: true,
    devServer: {
        proxy: {
            '/api': {
                target: 'http://localhost:8000',
                changeOrigin: true,  // 允许跨域
                pathRewrite: {
                    '^/api': '' // 非api开头的请求置空
                }
            }
        }
    }
})