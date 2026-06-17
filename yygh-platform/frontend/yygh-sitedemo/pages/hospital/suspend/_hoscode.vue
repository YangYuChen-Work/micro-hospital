<template>
  <div class="nav-container page-component">
    <div class="nav left-nav">
      <div class="nav-item">
        <span class="v-link clickable dark"
          :onclick="'javascript:window.location=\'/hospital/'+hoscode+'\''">预约挂号 </span>
      </div>
      <div class="nav-item">
        <span class="v-link clickable dark"
          :onclick="'javascript:window.location=\'/hospital/detail/'+hoscode+'\''"> 医院详情 </span>
      </div>
      <div class="nav-item">
        <span class="v-link clickable dark"
          :onclick="'javascript:window.location=\'/hospital/notice/'+hoscode+'\''"> 预约须知 </span>
      </div>
      <div class="nav-item selected">
        <span class="v-link selected dark"> 停诊信息 </span>
      </div>
      <div class="nav-item">
        <span class="v-link clickable dark"
          :onclick="'javascript:window.location=\'/hospital/query/'+hoscode+'\''"> 查询/取消 </span>
      </div>
    </div>
    <div class="page-container">
      <div class="hospital-source-list">
        <div class="header-wrapper" style="justify-content:normal">
          <span class="v-link clickable"
            :onclick="'javascript:window.location=\'/hospital/'+hoscode+'\''">{{ hosname }}</span>
          <div class="split"></div>
          <div>停诊信息</div>
        </div>
        <div class="title mt20">停诊公告</div>
        <div class="mt60">
          <div v-if="suspendList.length === 0" style="text-align:center;padding:60px 0;color:#999;">
            该医院暂无停诊信息
          </div>
          <div v-for="item in suspendList" :key="item.id" class="list-item">
            <div class="item-wrapper">
              <div class="title-wrapper">
                <div class="title">{{ item.title }}</div>
                <div class="split"></div>
                <div class="name">{{ item.param.depname }} | {{ item.docname }}</div>
              </div>
              <div class="special-wrapper">{{ item.skill }}</div>
              <div class="mt10" style="color:#999;font-size:12px;">
                {{ item.workDate }} {{ item.workTime == 0 ? '上午' : '下午' }}
                <span style="color:#e64242;margin-left:10px;">停诊</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import '~/assets/css/hospital_personal.css'
import '~/assets/css/hospital.css'
import hospitalApi from '@/api/hospital'

export default {
  data() {
    return {
      hoscode: null,
      hosname: '',
      suspendList: []
    }
  },
  created() {
    this.hoscode = this.$route.params.hoscode
    this.init()
  },
  methods: {
    init() {
      hospitalApi.getSuspendSchedule(this.hoscode).then(response => {
        this.suspendList = response.data.list || []
      })
      hospitalApi.show(this.hoscode).then(response => {
        this.hosname = response.data.hospital.hosname
      })
    }
  }
}
</script>
