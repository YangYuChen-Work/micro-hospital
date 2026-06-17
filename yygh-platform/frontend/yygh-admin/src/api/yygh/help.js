import request from '@/utils/request'
const api_name = '/admin/help'

export default {
    // 分类管理
    getCategoryList() {
        return request({
            url: `${api_name}/category/list`,
            method: 'get'
        })
    },
    addCategory(data) {
        return request({
            url: `${api_name}/category/add`,
            method: 'post',
            data
        })
    },
    updateCategory(data) {
        return request({
            url: `${api_name}/category/update`,
            method: 'put',
            data
        })
    },
    deleteCategory(id) {
        return request({
            url: `${api_name}/category/delete/${id}`,
            method: 'delete'
        })
    },
    // 文章管理
    getArticlePage(page, limit, params) {
        return request({
            url: `${api_name}/article/page/${page}/${limit}`,
            method: 'get',
            params
        })
    },
    getArticleById(id) {
        return request({
            url: `${api_name}/article/get/${id}`,
            method: 'get'
        })
    },
    addArticle(data) {
        return request({
            url: `${api_name}/article/add`,
            method: 'post',
            data
        })
    },
    updateArticle(data) {
        return request({
            url: `${api_name}/article/update`,
            method: 'put',
            data
        })
    },
    deleteArticle(id) {
        return request({
            url: `${api_name}/article/delete/${id}`,
            method: 'delete'
        })
    },
    // 反馈管理
    getFeedbackPage(page, limit, params) {
        return request({
            url: `${api_name}/feedback/page/${page}/${limit}`,
            method: 'get',
            params
        })
    },
    updateFeedbackStatus(data) {
        return request({
            url: `${api_name}/feedback/updateStatus`,
            method: 'put',
            data
        })
    }
}
