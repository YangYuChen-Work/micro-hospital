<template>
  <div class="app-container">
    <!-- 搜索 -->
    <el-form :inline="true" :model="searchObj">
      <el-form-item label="状态">
        <el-select v-model="searchObj.status" placeholder="全部" clearable style="width:140px">
          <el-option label="未读" :value="0" />
          <el-option label="已读" :value="1" />
          <el-option label="已解决" :value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="getList()">查询</el-button>
        <el-button @click="resetSearch()">清空</el-button>
      </el-form-item>
    </el-form>
    <!-- 表格 -->
    <el-table v-loading="listLoading" :data="list" border stripe>
      <el-table-column type="index" width="60" label="序号" align="center" />
      <el-table-column prop="content" label="反馈内容" min-width="250" show-overflow-tooltip />
      <el-table-column prop="contact" label="联系方式" width="150" align="center">
        <template slot-scope="scope">
          {{ scope.row.contact || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100" align="center">
        <template slot-scope="scope">
          <el-tag :type="statusType(scope.row.status)" size="small">
            {{ statusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="提交时间" width="170" align="center" />
      <el-table-column label="操作" width="180" align="center">
        <template slot-scope="scope">
          <el-button type="text" @click="showDetail(scope.row)">详情</el-button>
          <el-dropdown @command="(cmd) => handleStatusChange(scope.row, cmd)" style="margin-left:8px">
            <el-button type="text">状态 <i class="el-icon-arrow-down" /></el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item :command="1" :disabled="scope.row.status === 1">标记已读</el-dropdown-item>
              <el-dropdown-item :command="2" :disabled="scope.row.status === 2">标记已解决</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
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
    <!-- 详情对话框 -->
    <el-dialog title="反馈详情" :visible.sync="detailVisible" width="500px">
      <div style="margin-bottom:12px">
        <span style="color:#999">内容：</span>
        <p style="margin-top:8px;line-height:1.6;white-space:pre-wrap">{{ detailRow.content }}</p>
      </div>
      <div>
        <span style="color:#999">联系方式：</span>
        <span>{{ detailRow.contact || '未填写' }}</span>
      </div>
    </el-dialog>
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
      searchObj: { status: null },
      detailVisible: false,
      detailRow: {}
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList(page = 1) {
      this.page = page
      this.listLoading = true
      helpApi.getFeedbackPage(page, this.limit, this.searchObj).then(response => {
        this.list = response.data.records
        this.total = response.data.total
        this.listLoading = false
      })
    },
    resetSearch() {
      this.searchObj = { status: null }
      this.getList()
    },
    showDetail(row) {
      this.detailRow = row
      this.detailVisible = true
    },
    handleStatusChange(row, newStatus) {
      helpApi.updateFeedbackStatus({ id: row.id, status: newStatus }).then(() => {
        this.$message.success('状态更新成功')
        row.status = newStatus
      })
    },
    statusType(status) {
      return status === 0 ? 'danger' : status === 1 ? 'warning' : 'success'
    },
    statusText(status) {
      return status === 0 ? '未读' : status === 1 ? '已读' : '已解决'
    }
  }
}
</script>
