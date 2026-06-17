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
      <div class="nav-item">
        <span class="v-link clickable dark"
          :onclick="'javascript:window.location=\'/hospital/suspend/'+hoscode+'\''"> 停诊信息 </span>
      </div>
      <div class="nav-item selected">
        <span class="v-link selected dark"> 查询/取消 </span>
      </div>
    </div>
    <div class="page-container">
      <div class="hospital-source-list">
        <div class="header-wrapper" style="justify-content:normal">
          <span class="v-link clickable"
            :onclick="'javascript:window.location=\'/hospital/'+hoscode+'\''">{{ hosname }}</span>
          <div class="split"></div>
          <div>查询/取消</div>
        </div>
        <div class="title mt20">查询预约订单</div>
        <div class="mt40">
          <el-form :inline="true" :model="searchForm" class="search-form">
            <el-form-item label="手机号">
              <el-input v-model="searchForm.phone" placeholder="请输入就诊人手机号" style="width:200px;"></el-input>
            </el-form-item>
            <el-form-item label="就诊人姓名">
              <el-input v-model="searchForm.patientName" placeholder="请输入就诊人姓名" style="width:200px;"></el-input>
            </el-form-item>
            <el-form-item>
              <div class="v-button" @click="search()" style="display:inline-block;padding:8px 24px;">查询</div>
            </el-form-item>
          </el-form>
        </div>
        <div class="mt40" v-if="searched">
          <div v-if="orderList.length === 0" style="text-align:center;padding:60px 0;color:#999;">
            未查询到相关订单
          </div>
          <div v-for="item in orderList" :key="item.id" class="list-item">
            <div class="item-wrapper">
              <div class="title-wrapper">
                <div class="title">{{ item.hosname }} | {{ item.depname }}</div>
                <div class="split"></div>
                <div class="name">{{ item.title }}</div>
              </div>
              <div class="mt10" style="font-size:13px;color:#666;">
                就诊人：{{ item.patientName }}
                <span style="margin-left:16px;">日期：{{ item.reserveDate }} {{ item.reserveTime == 0 ? '上午' : '下午' }}</span>
              </div>
              <div class="mt5" style="font-size:13px;color:#666;">
                订单号：{{ item.outTradeNo }}
                <span style="margin-left:16px;">状态：<span :style="item.orderStatus == -1 ? 'color:#e64242;' : ''">{{ item.param.orderStatusString }}</span></span>
              </div>
            </div>
            <div class="right-wrapper">
              <div class="fee">￥{{ item.amount }}</div>
              <div class="button-wrapper" v-if="item.orderStatus == 0 || item.orderStatus == 1">
                <div class="v-button" style="background-color:#e64242;" @click="cancelOrder(item.id)">取消预约</div>
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
import orderInfoApi from '@/api/orderinfo'
import weixinApi from '@/api/wx'
import hospitalApi from '@/api/hospital'

export default {
  data() {
    return {
      hoscode: null,
      hosname: '',
      searchForm: {
        phone: '',
        patientName: ''
      },
      orderList: [],
      searched: false
    }
  },
  created() {
    this.hoscode = this.$route.params.hoscode
    hospitalApi.show(this.hoscode).then(response => {
      this.hosname = response.data.hospital.hosname
    })
  },
  methods: {
    search() {
      if (!this.searchForm.phone || !this.searchForm.patientName) {
        this.$message.warning('请输入手机号和就诊人姓名')
        return
      }
      orderInfoApi.searchOrders(this.searchForm.phone, this.searchForm.patientName).then(response => {
        this.orderList = response.data.list || []
        this.searched = true
      })
    },
    cancelOrder(orderId) {
      this.$confirm('确定取消预约吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return weixinApi.cancelOrder(orderId)
      }).then(() => {
        this.$message.success('取消成功')
        this.search()
      })
    }
  }
}
</script>
