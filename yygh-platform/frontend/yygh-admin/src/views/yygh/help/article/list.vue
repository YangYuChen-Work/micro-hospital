<template>
  <div class="app-container">
    <!-- 搜索 -->
    <el-form :inline="true" :model="searchObj">
      <el-form-item label="分类">
        <el-select v-model="searchObj.categoryId" placeholder="全部" clearable style="width:160px">
          <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="关键词">
        <el-input v-model="searchObj.keyword" placeholder="标题/内容关键词" clearable style="width:200px"
          @keyup.enter.native="getList()" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="getList()">查询</el-button>
        <el-button @click="resetSearch()">清空</el-button>
      </el-form-item>
    </el-form>
    <!-- 工具栏 -->
    <div style="margin-bottom:12px">
      <router-link to="/help/article/form">
        <el-button type="primary">添加文章</el-button>
      </router-link>
    </div>
    <!-- 表格 -->
    <el-table v-loading="listLoading" :data="list" border stripe>
      <el-table-column type="index" width="60" label="序号" align="center" />
      <el-table-column prop="title" label="标题" min-width="180" show-overflow-tooltip />
      <el-table-column label="分类" width="120" align="center">
        <template slot-scope="scope">
          {{ getCategoryName(scope.row.categoryId) }}
        </template>
      </el-table-column>
      <el-table-column prop="viewCount" label="浏览量" width="90" align="center" />
      <el-table-column prop="sortOrder" label="排序" width="70" align="center" />
      <el-table-column label="状态" width="80" align="center">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'info'" size="small">
            {{ scope.row.status === 1 ? '已发布' : '草稿' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="170" align="center" />
      <el-table-column label="操作" width="180" align="center">
        <template slot-scope="scope">
          <router-link :to="'/help/article/form?id=' + scope.row.id">
            <el-button type="primary" size="mini">编辑</el-button>
          </router-link>
          <el-button type="danger" size="mini" @click="handleDelete(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <el-pagination
      :current-page="page"
      :page-size="limit"
      :total="total"
      layout="total, prev, pager, next"
      style="padding:20px 0;text-align:center"
      @current-change="getList"
    />
  </div>
</template>

<script>
import helpApi from '@/api/yygh/help'

export default {
  data() {
    return {
      list: [],
      listLoading: false,
      page: 1,
      limit: 10,
      total: 0,
      categories: [],
      searchObj: { categoryId: null, keyword: '' }
    }
  },
  created() {
    this.init()
  },
  methods: {
    init() {
      helpApi.getCategoryList().then(response => {
        this.categories = response.data.list
      })
      this.getList()
    },
    getList(page = 1) {
      this.page = page
      this.listLoading = true
      helpApi.getArticlePage(page, this.limit, this.searchObj).then(response => {
        this.list = response.data.records
        this.total = response.data.total
        this.listLoading = false
      })
    },
    resetSearch() {
      this.searchObj = { categoryId: null, keyword: '' }
      this.getList()
    },
    getCategoryName(categoryId) {
      const cat = this.categories.find(c => c.id === categoryId)
      return cat ? cat.name : '-'
    },
    handleDelete(id) {
      this.$confirm('确认删除该文章？', '提示', { type: 'warning' }).then(() => {
        helpApi.deleteArticle(id).then(() => {
          this.$message.success('删除成功')
          this.getList()
        })
      }).catch(() => {})
    }
  }
}
</script>
