<template>
  <div class="app-container">
    <div id="main">
      <!-- 公共头 -->
      <myheader/>
      <div class="main-container">
        <el-scrollbar class='page-component__scroll'>
          <!-- 内容区域 -->
          <nuxt/>
        </el-scrollbar>
      </div>
      <!-- 公共底 -->
      <myfooter/>
    </div>
    <!-- AI 医疗助手悬浮球 -->
    <floating-ai-ball />
  </div>
</template>
<script>
import '~/assets/css/app.css'
import '~/assets/css/chunk.css'
import '~/assets/css/iconfont.css'
import '~/assets/css/main.css'

import myheader from './myheader'
import myfooter from './myfooter'
import FloatingAiBall from '~/components/FloatingAiBall.vue'

export default {

  components: {
    myheader, myfooter, FloatingAiBall
  },

  mounted() {
    window.document.getElementById("__nuxt").classList.add("app-container")
    window.document.getElementById("__layout").classList.add("app-container")
  }
}
</script>