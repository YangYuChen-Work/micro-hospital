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
      <div class="nav-item selected">
        <span class="v-link selected dark"> 预约须知 </span>
      </div>
      <div class="nav-item">
        <span class="v-link clickable dark"
          :onclick="'javascript:window.location=\'/hospital/suspend/'+hoscode+'\''"> 停诊信息 </span>
      </div>
      <div class="nav-item">
        <span class="v-link clickable dark"
          :onclick="'javascript:window.location=\'/hospital/query/'+hoscode+'\''"> 查询/取消 </span>
      </div>
    </div>
    <div class="page-container">
      <div class="hospital-detail">
        <div class="common-header">
          <div class="title-wrapper">
            <span class="hospital-title">{{ hospital.hosname }}</span>
            <div class="icon-wrapper">
              <span class="iconfont"></span>{{ hospital.param.hostypeString }}
            </div>
          </div>
        </div>
        <div class="title mt40">预约规则</div>
        <div class="info-wrapper mt20">
          <div class="content-wrapper">
            <div class="rule-item">
              <span class="label">预约周期：</span><span>{{ bookingRule.cycle }}天</span>
            </div>
            <div class="rule-item">
              <span class="label">放号时间：</span><span>每天{{ bookingRule.releaseTime }}</span>
            </div>
            <div class="rule-item">
              <span class="label">停挂时间：</span><span>每天{{ bookingRule.stopTime }}</span>
            </div>
            <div class="rule-item">
              <span class="label">退号规则：</span>
              <span v-if="bookingRule.quitDay == -1">就诊前一工作日{{ bookingRule.quitTime }}前可在线退号</span>
              <span v-if="bookingRule.quitDay == 0">就诊当天{{ bookingRule.quitTime }}前可在线退号</span>
              <span v-if="bookingRule.quitDay == 1">就诊前一天{{ bookingRule.quitTime }}前可在线退号</span>
            </div>
          </div>
        </div>
        <div class="title mt40">预约规则详情</div>
        <div class="rule-wrapper mt20">
          <ol v-if="bookingRule.rule && bookingRule.rule.length > 0">
            <li v-for="(item, index) in bookingRule.rule" :key="index">{{ item }}</li>
          </ol>
          <div v-else class="empty-tip">暂无详细规则</div>
        </div>
        <div class="title mt40">预约流程</div>
        <div class="process-wrapper mt20">
          <div class="process-step">
            <div class="step-num">1</div>
            <div class="step-text">选择医院科室</div>
          </div>
          <div class="process-arrow">→</div>
          <div class="process-step">
            <div class="step-num">2</div>
            <div class="step-text">选择就诊日期</div>
          </div>
          <div class="process-arrow">→</div>
          <div class="process-step">
            <div class="step-num">3</div>
            <div class="step-text">确认挂号信息</div>
          </div>
          <div class="process-arrow">→</div>
          <div class="process-step">
            <div class="step-num">4</div>
            <div class="step-text">支付挂号费用</div>
          </div>
          <div class="process-arrow">→</div>
          <div class="process-step">
            <div class="step-num">5</div>
            <div class="step-text">按时到院取号</div>
          </div>
        </div>
        <div class="title mt40">注意事项</div>
        <div class="rule-wrapper mt20">
          <ol>
            <li>请确认就诊人信息是否准确，若填写错误将无法取号就诊，损失由本人承担；</li>
            <li>【取号】就诊当天请携带有效身份证件在医院取号，未取号视为爽约，该号不退不换；</li>
            <li>【退号】在退号截止时间前可在线退号，逾期将不可办理退号退费；</li>
            <li>预约挂号支持自费患者使用身份证预约，同时支持医保患者使用社保卡预约；</li>
            <li>医保患者在住院期间不能使用社保卡在门诊取号。</li>
          </ol>
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
      hospital: {
        param: {}
      },
      bookingRule: {}
    }
  },
  created() {
    this.hoscode = this.$route.params.hoscode
    this.init()
  },
  methods: {
    init() {
      hospitalApi.show(this.hoscode).then(response => {
        var hosp = response.data.hospital
        hosp.logoData = hosp.logoData ? 'data:image/jpeg;base64,' + hosp.logoData : hosp.logoData
        this.hospital = hosp
        this.bookingRule = response.data.bookingRule || {}
      })
    }
  }
}
</script>
<style>
  .rule-item {
    margin-bottom: 12px;
    font-size: 14px;
    color: #333;
  }
  .rule-item .label {
    color: #666;
    display: inline-block;
    width: 100px;
  }
  .rule-wrapper {
    padding-left: 20px;
  }
  .rule-wrapper ol {
    padding-left: 16px;
    line-height: 2;
    color: #333;
    font-size: 14px;
  }
  .empty-tip {
    color: #999;
    padding: 20px 0;
  }
  .process-wrapper {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    gap: 8px;
  }
  .process-step {
    display: flex;
    flex-direction: column;
    align-items: center;
    width: 100px;
  }
  .step-num {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    background: #4A90D9;
    color: #fff;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 16px;
    font-weight: 600;
  }
  .step-text {
    margin-top: 8px;
    font-size: 13px;
    color: #333;
    text-align: center;
  }
  .process-arrow {
    color: #ccc;
    font-size: 20px;
    margin: 0 4px;
  }
  .info-wrapper {
    padding-left: 0;
    padding-top: 0;
    margin-top: 0;
  }
</style>
