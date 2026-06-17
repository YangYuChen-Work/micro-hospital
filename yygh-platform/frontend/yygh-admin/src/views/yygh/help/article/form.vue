<template>
  <div class="app-container">
    <h4 style="margin-bottom:20px">{{ isEdit ? '编辑文章' : '添加文章' }}</h4>
    <el-form ref="form" :model="article" label-width="80px" style="max-width:900px">
      <el-form-item label="标题">
        <el-input v-model="article.title" placeholder="请输入文章标题" maxlength="100" />
      </el-form-item>
      <el-form-item label="分类">
        <el-select v-model="article.categoryId" placeholder="请选择分类" style="width:200px">
          <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="摘要">
        <el-input v-model="article.summary" type="textarea" :rows="2" placeholder="文章摘要（选填）" maxlength="200" />
      </el-form-item>
      <el-form-item label="排序">
        <el-input-number v-model="article.sortOrder" :min="0" :max="999" />
      </el-form-item>
      <el-form-item label="状态">
        <el-radio-group v-model="article.status">
          <el-radio :label="1">发布</el-radio>
          <el-radio :label="0">草稿</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="内容">
        <div ref="editor" style="border:1px solid #dcdfe6;border-radius:4px;min-height:400px"></div>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSave">{{ saveText }}</el-button>
        <el-button @click="goBack">返回</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import helpApi from '@/api/yygh/help'
import E from 'wangeditor'

export default {
  data() {
    return {
      article: { title: '', categoryId: null, summary: '', sortOrder: 0, status: 1, content: '' },
      categories: [],
      editor: null,
      isEdit: false,
      saveText: '保存'
    }
  },
  created() {
    this.isEdit = !!this.$route.query.id
    helpApi.getCategoryList().then(response => {
      this.categories = response.data.list
    })
    if (this.isEdit) {
      helpApi.getArticleById(this.$route.query.id).then(response => {
        this.article = response.data.article
        this.$nextTick(() => {
          this.initEditor()
        })
      })
    } else {
      this.$nextTick(() => {
        this.initEditor()
      })
    }
  },
  beforeDestroy() {
    if (this.editor) {
      this.editor.destroy()
      this.editor = null
    }
  },
  methods: {
    initEditor() {
      this.editor = new E(this.$refs.editor)
      this.editor.config.zIndex = 100
      this.editor.config.placeholder = '请输入文章内容...'
      this.editor.config.menus = [
        'head', 'bold', 'fontSize', 'fontName', 'italic', 'underline', 'strikeThrough',
        'foreColor', 'backColor', 'link', 'list', 'justify', 'quote', 'image', 'table', 'code'
      ]
      this.editor.create()
      if (this.article.content) {
        this.editor.txt.html(this.article.content)
      }
    },
    handleSave() {
      if (!this.article.title.trim()) {
        this.$message.warning('请输入文章标题')
        return
      }
      if (!this.article.categoryId) {
        this.$message.warning('请选择分类')
        return
      }
      const content = this.editor.txt.html()
      if (!content || content === '<p><br></p>') {
        this.$message.warning('请输入文章内容')
        return
      }
      this.article.content = content
      this.saveText = '保存中...'
      const api = this.isEdit ? helpApi.updateArticle(this.article) : helpApi.addArticle(this.article)
      api.then(() => {
        this.$message.success(this.isEdit ? '修改成功' : '添加成功')
        this.$router.push('/help/article/list')
      }).catch(() => {
        this.saveText = '保存'
      })
    },
    goBack() {
      this.$router.push('/help/article/list')
    }
  }
}
</script>
