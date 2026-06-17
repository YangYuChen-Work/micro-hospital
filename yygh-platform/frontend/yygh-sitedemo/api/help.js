import request from '@/utils/request'
const api_name = '/api/help'
export default {
    getCategories() {
        return request({
            url: `${api_name}/category/list`,
            method: 'get'
        })
    },
    getArticleList(params) {
        return request({
            url: `${api_name}/article/list`,
            method: 'get',
            params
        })
    },
    searchArticles(params) {
        return request({
            url: `${api_name}/article/search`,
            method: 'get',
            params
        })
    },
    getArticleDetail(id) {
        return request({
            url: `${api_name}/article/detail/${id}`,
            method: 'get'
        })
    },
    getHotArticles() {
        return request({
            url: `${api_name}/article/hot`,
            method: 'get'
        })
    },
    submitFeedback(data) {
        return request({
            url: `${api_name}/feedback/submit`,
            method: 'post',
            data
        })
    }
}
