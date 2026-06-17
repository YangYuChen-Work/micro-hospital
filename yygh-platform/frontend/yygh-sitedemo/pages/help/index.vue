<template>
  <div class="nav-container page-component">
    <!-- 左侧导航 -->
    <div class="nav left-nav">
      <div class="nav-item" :class="{ selected: currentCategoryId === null }">
        <span class="v-link clickable dark" @click="selectCategory(null)">全部</span>
      </div>
      <div class="nav-item" v-for="cat in categories" :key="cat.id" :class="{ selected: currentCategoryId === cat.id }">
        <span class="v-link clickable dark" @click="selectCategory(cat.id)">{{ cat.name }}</span>
      </div>
    </div>
    <!-- 右侧内容 -->
    <div class="page-container">
      <div class="help-content">
        <!-- 搜索框 -->
        <div class="search-wrapper">
          <el-input v-model="keyword" placeholder="输入关键词搜索问题" class="search-input"
            @keyup.enter.native="doSearch" clearable @clear="resetSearch">
            <el-button slot="append" icon="el-icon-search" @click="doSearch">搜索</el-button>
          </el-input>
        </div>
        <!-- 热门文章 -->
        <div class="section" v-if="!isSearchMode && currentCategoryId === null">
          <div class="section-title">热门问题</div>
          <div class="article-list">
            <div class="article-card" v-for="item in hotArticles" :key="item.id"
              @click="goDetail(item.id)">
              <div class="article-title">{{ item.title }}</div>
              <div class="article-summary">{{ item.summary || '暂无摘要' }}</div>
              <div class="article-meta">
                <span><i class="el-icon-view"></i> {{ item.viewCount || 0 }}</span>
                <span>{{ item.createTime }}</span>
              </div>
            </div>
            <div v-if="hotArticles.length === 0" class="empty-tip">暂无热门文章</div>
          </div>
        </div>
        <!-- 分类文章列表 / 搜索结果 -->
        <div class="section" v-if="isSearchMode || currentCategoryId !== null">
          <div class="section-title">
            {{ isSearchMode ? '搜索结果' : currentCategoryName }}
            <span class="result-count" v-if="isSearchMode">找到 {{ total }} 条相关结果</span>
          </div>
          <div class="article-list">
            <div class="article-card" v-for="item in articleList" :key="item.id"
              @click="goDetail(item.id)">
              <div class="article-title" v-html="highlightKeyword(item.title)"></div>
              <div class="article-summary">{{ item.summary || '暂无摘要' }}</div>
              <div class="article-meta">
                <span><i class="el-icon-view"></i> {{ item.viewCount || 0 }}</span>
                <span>{{ item.createTime }}</span>
              </div>
            </div>
            <div v-if="articleList.length === 0" class="empty-tip">暂无文章</div>
          </div>
          <!-- 分页 -->
          <el-pagination
            v-if="total > pageSize"
            :current-page="currentPage"
            :page-size="pageSize"
            :total="total"
            layout="prev, pager, next"
            @current-change="handlePageChange"
            class="pagination"
          />
        </div>
        <!-- 意见反馈 -->
        <div class="section feedback-section">
          <div class="section-title">意见反馈</div>
          <el-form :model="feedbackForm" class="feedback-form">
            <el-form-item>
              <el-input v-model="feedbackForm.content" type="textarea" :rows="4"
                placeholder="请详细描述您的问题或建议..." maxlength="500" show-word-limit />
            </el-form-item>
            <el-form-item>
              <el-input v-model="feedbackForm.contact" placeholder="联系方式（选填，方便我们回复您）" maxlength="100" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="submitFeedback" :disabled="!feedbackForm.content.trim()">
                {{ submitText }}
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import '~/assets/css/hospital_personal.css'
import '~/assets/css/hospital.css'
import helpApi from '@/api/help'

export default {
  data() {
    return {
      categories: [],
      hotArticles: [],
      articleList: [],
      currentCategoryId: null,
      currentCategoryName: '',
      keyword: '',
      isSearchMode: false,
      currentPage: 1,
      pageSize: 10,
      total: 0,
      feedbackForm: { content: '', contact: '' },
      submitText: '提交反馈'
    }
  },
  created() {
    this.init()
  },
  methods: {
    init() {
      helpApi.getCategories().then(response => {
        this.categories = response.data.list
      })
      helpApi.getHotArticles().then(response => {
        this.hotArticles = response.data.list
      })
    },
    selectCategory(catId) {
      this.currentCategoryId = catId
      this.isSearchMode = false
      this.keyword = ''
      this.currentPage = 1
      if (catId !== null) {
        const cat = this.categories.find(c => c.id === catId)
        this.currentCategoryName = cat ? cat.name : ''
      }
      this.loadArticles()
    },
    doSearch() {
      if (!this.keyword.trim()) return
      this.isSearchMode = true
      this.currentCategoryId = null
      this.currentPage = 1
      this.loadArticles()
    },
    resetSearch() {
      this.isSearchMode = false
      this.currentCategoryId = null
      this.currentPage = 1
      this.articleList = []
      this.total = 0
    },
    loadArticles() {
      const params = { page: this.currentPage, limit: this.pageSize }
      const apiMethod = this.isSearchMode
        ? helpApi.searchArticles({ ...params, keyword: this.keyword })
        : helpApi.getArticleList({ ...params, categoryId: this.currentCategoryId })

      apiMethod.then(response => {
        this.articleList = response.data.records
        this.total = response.data.total
      })
    },
    handlePageChange(page) {
      this.currentPage = page
      this.loadArticles()
    },
    goDetail(id) {
      window.location.href = '/help/detail?id=' + id
    },
    highlightKeyword(text) {
      if (!this.isSearchMode || !this.keyword) return text
      const reg = new RegExp(this.keyword.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'), 'gi')
      return text.replace(reg, '<em style="color:#e1251b;font-style:normal">$&</em>')
    },
    submitFeedback() {
      if (this.submitText === '正在提交...') return
      this.submitText = '正在提交...'
      helpApi.submitFeedback(this.feedbackForm).then(() => {
        this.$message.success('感谢您的反馈！')
        this.feedbackForm = { content: '', contact: '' }
        this.submitText = '提交反馈'
      }).catch(() => {
        this.submitText = '提交反馈'
      })
    }
  }
}
</script>

<style scoped>
.help-content {
  padding: 0 10px;
}
.search-wrapper {
  margin-bottom: 30px;
}
.search-input {
  max-width: 600px;
}
.section {
  margin-bottom: 30px;
}
.section-title {
  font-size: 18px;
  font-weight: 700;
  color: #333;
  margin-bottom: 16px;
  padding-bottom: 10px;
  border-bottom: 2px solid #409eff;
}
.result-count {
  font-size: 14px;
  font-weight: 400;
  color: #999;
  margin-left: 10px;
}
.article-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.article-card {
  padding: 16px 20px;
  border: 1px solid #eee;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
}
.article-card:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64,158,255,0.15);
}
.article-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin-bottom: 6px;
}
.article-summary {
  font-size: 13px;
  color: #999;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.article-meta {
  font-size: 12px;
  color: #bbb;
  display: flex;
  gap: 16px;
}
.empty-tip {
  text-align: center;
  color: #999;
  padding: 40px 0;
}
.pagination {
  margin-top: 20px;
  text-align: center;
}
.feedback-section {
  margin-top: 40px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}
.feedback-form {
  max-width: 500px;
}
</style>
