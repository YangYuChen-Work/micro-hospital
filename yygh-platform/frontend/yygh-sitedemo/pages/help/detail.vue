<template>
  <div class="nav-container page-component">
    <!-- 左侧导航 -->
    <div class="nav left-nav">
      <div class="nav-item">
        <span class="v-link clickable dark" onclick="javascript:window.location='/help'">返回帮助中心</span>
      </div>
      <div class="nav-item" v-for="cat in categories" :key="cat.id"
        :class="{ selected: article.categoryId === cat.id }">
        <span class="v-link clickable dark" onclick="javascript:window.location='/help'">{{ cat.name }}</span>
      </div>
    </div>
    <!-- 右侧内容 -->
    <div class="page-container">
      <div class="detail-content" v-if="article.title">
        <!-- 面包屑 -->
        <div class="breadcrumb">
          <span class="v-link clickable" onclick="javascript:window.location='/help'">帮助中心</span>
          <span class="separator">/</span>
          <span>{{ currentCategoryName }}</span>
          <span class="separator">/</span>
          <span>文章详情</span>
        </div>
        <!-- 文章内容 -->
        <div class="article-detail">
          <h2 class="detail-title">{{ article.title }}</h2>
          <div class="detail-meta">
            <span>分类：{{ currentCategoryName }}</span>
            <span><i class="el-icon-view"></i> {{ article.viewCount || 0 }} 次浏览</span>
            <span>发布时间：{{ article.createTime }}</span>
          </div>
          <div class="detail-body" v-html="article.content"></div>
        </div>
        <!-- 底部操作 -->
        <div class="detail-footer">
          <el-button type="primary" onclick="javascript:window.location='/help'">返回帮助中心</el-button>
        </div>
      </div>
      <div v-else class="loading-wrapper">
        <p v-if="!loadError">加载中...</p>
        <p v-else>文章不存在或已下架</p>
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
      article: {},
      categories: [],
      loadError: false
    }
  },
  computed: {
    currentCategoryName() {
      if (!this.article.categoryId || !this.categories.length) return ''
      const cat = this.categories.find(c => c.id === this.article.categoryId)
      return cat ? cat.name : ''
    }
  },
  created() {
    const articleId = this.$route.query.id
    if (articleId) {
      this.loadArticle(articleId)
    }
    this.loadCategories()
  },
  methods: {
    loadArticle(id) {
      helpApi.getArticleDetail(id).then(response => {
        this.article = response.data.article
      }).catch(() => {
        this.loadError = true
      })
    },
    loadCategories() {
      helpApi.getCategories().then(response => {
        this.categories = response.data.list
      })
    }
  }
}
</script>

<style scoped>
.detail-content {
  padding: 0 10px;
}
.breadcrumb {
  font-size: 14px;
  color: #999;
  margin-bottom: 24px;
}
.breadcrumb .separator {
  margin: 0 8px;
}
.article-detail {
  background: #fff;
  padding: 30px;
  border-radius: 6px;
  border: 1px solid #eee;
}
.detail-title {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  margin-bottom: 12px;
  line-height: 1.4;
}
.detail-meta {
  font-size: 13px;
  color: #bbb;
  display: flex;
  gap: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
  margin-bottom: 24px;
}
.detail-body {
  font-size: 15px;
  color: #444;
  line-height: 1.8;
}
.detail-body >>> h3 {
  font-size: 18px;
  margin: 20px 0 12px;
}
.detail-body >>> p {
  margin-bottom: 12px;
}
.detail-body >>> ul, .detail-body >>> ol {
  padding-left: 20px;
  margin-bottom: 12px;
}
.detail-body >>> li {
  margin-bottom: 6px;
}
.detail-footer {
  margin-top: 30px;
  text-align: center;
}
.loading-wrapper {
  text-align: center;
  padding: 80px 0;
  color: #999;
}
</style>
