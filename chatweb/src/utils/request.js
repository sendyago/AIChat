import axios from 'axios'

const service = axios.create({
    baseURL: process.env.VUE_APP_API_BASE_URL || '/api', // 后端API的基本URL 
    // baseUrl: process.env.BASE_API, // 后端API的基本URL 
    timeout: 60000,
    headers: {
        'Content-Type': 'application/json;charset=utf-8'
    }
})

// 请求拦截器
service.interceptors.request.use(
    config => {
        return config
    },
    error => {
        console.error('请求拦截器错误:', error);
        return Promise.reject(error);
    }
)

// 响应拦截器
service.interceptors.response.use(
    response => {
        return response
    },
    error => {
        console.error('响应拦截器错误:', error.response || error.message);
        let message = '网络或服务器错误';
        if (error.response) {
        switch (error.response.status) {
            case 400: message = '请求错误'; break;
            case 401: message = '未授权，请登录'; /* router.push('/login'); */ break;
            case 403: message = '拒绝访问'; break;
            case 404: message = `请求地址出错: ${error.response.config.url}`; break;
            case 500: message = '服务器内部错误'; break;
            case 502: message = '网关错误'; break;
            case 503: message = '服务不可用'; break;
            case 504: message = '网关超时'; break;
        }
        }
        return Promise.reject(error);
    }
)

export default service