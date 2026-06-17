<template>
  <div class="app-container">
    <!-- 搜索 -->
    <el-form :inline="true">
      <el-form-item>
        <el-button type="primary" @click="openAddDialog">添加分类</el-button>
      </el-form-item>
    </el-form>
    <!-- 表格 -->
    <el-table v-loading="listLoading" :data="list" border stripe>
      <el-table-column type="index" width="60" label="序号" align="center" />
      <el-table-column prop="name" label="分类名称" />
      <el-table-column prop="sortOrder" label="排序" width="100" align="center" />
      <el-table-column label="状态" width="100" align="center">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" size="small">
            {{ scope.row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" align="center" />
      <el-table-column label="操作" width="200" align="center">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="openEditDialog(scope.row)">修改</el-button>
          <el-button type="danger" size="mini" @click="handleDelete(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 添加/编辑对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="450px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="分类名称">
          <el-input v-model="form.name" placeholder="请输入分类名称" maxlength="50" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import helpApi from '@/api/yygh/help'

const defaultForm = { name: '', sortOrder: 0, status: 1 }

export default {
  data() {
    return {
      list: [],
      listLoading: false,
      dialogVisible: false,
      dialogTitle: '',
      form: { ...defaultForm },
      editingId: null
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.listLoading = true
      helpApi.getCategoryList().then(response => {
        this.list = response.data.list
        this.listLoading = false
      })
    },
    openAddDialog() {
      this.dialogTitle = '添加分类'
      this.editingId = null
      this.form = { ...defaultForm }
      this.dialogVisible = true
    },
    openEditDialog(row) {
      this.dialogTitle = '修改分类'
      this.editingId = row.id
      this.form = { name: row.name, sortOrder: row.sortOrder, status: row.status }
      this.dialogVisible = true
    },
    handleSave() {
      if (!this.form.name.trim()) {
        this.$message.warning('请输入分类名称')
        return
      }
      if (this.editingId) {
        helpApi.updateCategory({ id: this.editingId, ...this.form }).then(() => {
          this.$message.success('修改成功')
          this.dialogVisible = false
          this.getList()
        })
      } else {
        helpApi.addCategory(this.form).then(() => {
          this.$message.success('添加成功')
          this.dialogVisible = false
          this.getList()
        })
      }
    },
    handleDelete(id) {
      this.$confirm('确认删除该分类？', '提示', { type: 'warning' }).then(() => {
        helpApi.deleteCategory(id).then(() => {
          this.$message.success('删除成功')
          this.getList()
        })
      }).catch(() => {})
    }
  }
}
</script>
