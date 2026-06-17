<template>
  <div class="home page-component">
    <el-carousel indicator-position="outside">
      <el-carousel-item v-for="item in 2" :key="item">
        <img src="~assets/images/web-banner1.png" alt="">
      </el-carousel-item>
    </el-carousel>
    <!-- 搜索 -->
    <div class="search-container">
    <div class="search-wrapper">
    <div class="hospital-search">
      <el-autocomplete
      class="search-input"
      prefix-icon="el-icon-search"
      v-model="hosname"
      :fetch-suggestions="querySearchAsync"
      placeholder="点击输入医院名称"
      @select="handleSelect"
      >
        <span slot="suffix" class="search-btn v-link highlight clickable selected">搜索 </span>
      </el-autocomplete>
    </div>
    </div>
    </div>
    <!-- bottom -->
    <div class="bottom">
    <div class="left">
    <div class="home-filter-wrapper">
    <div class="title"> 医院</div>
    <div>
      <div class="filter-wrapper">
        <span
        class="label">等级：</span>
        <div class="condition-wrapper">
          <span class="item v-link clickable" 
            :class="hostypeActiveIndex == index ? 'selected' : ''"
             v-for="(item,index) in hostypeList" :key="item.id" 
             @click="hostypeSelect(item.value, index)">{{ item.name }}</span>
       </div>
      </div>
    <div class="filter-wrapper">
      <span
      class="label">地区：</span>
      <div class="condition-wrapper">
        <span class="item v-link clickable"
          :class="provinceActiveIndex == index ? 'selected' : ''"
          v-for="(item,index) in districtList" :key="item.id"
          @click="districtSelect(item.value, index)">{{ item.name }}</span>
      </div>
      </div>
    </div>
    </div>
    <div class="v-scroll-list hospital-list">
      <div class="v-card clickable list-item" v-for="item in list" :key="item.id" :class="{ 'decor-item': item.decorative }">
        <div class="">
          <span v-if="item.decorative" class="decor-badge">即将上线</span>
          <div class="hospital-list-item hos-item" index="0" @click="show(item.hoscode)">
            <div class="wrapper">
            <div class="hospital-title"> {{ item.hosname }}</div>
            <div class="bottom-container">
            <div class="icon-wrapper">
              <span class="iconfont"></span>{{ item.param.hostypeString }}
            </div>
          <div class="icon-wrapper">
          <span class="iconfont"></span>每天{{ item.bookingRule.releaseTime }}放号
          </div>
          </div>
          </div>
          <img :src="item.logoData"
           :alt="item.hosname"
           class="hospital-img">
      </div>
    </div>
    </div>
    </div>
    </div>
    <div class="right">
      <div class="common-dept">
      <div class="header-wrapper">
      <div class="title"> 常见科室</div>
      <div class="all-wrapper"><span>全部</span>
      <span class="iconfont icon"></span>
      </div>
      </div>
      <div class="content-wrapper">
      <span class="item v-link clickable dark">神经内科 </span>
      <span class="item v-link clickable dark">消化内科 </span>
      <span class="item v-link clickable dark">呼吸内科 </span>
      <span class="item v-link clickable dark">内科 </span>
      <span class="item v-link clickable dark">神经外科 </span>
      <span class="item v-link clickable dark">妇科 </span>
      <span class="item v-link clickable dark"> 产科 </span>
      <span class="item v-link clickable dark">儿科 </span>
      </div>
    </div>
    <div class="space">
      <div class="header-wrapper">
      <div class="title-wrapper">
      <div class="icon-wrapper"><span
      class="iconfont title-icon"></span>
      </div>
      <span class="title">平台公告</span>
      </div>
      <div class="all-wrapper">
      <span>全部</span>
      <span class="iconfont icon"></span>
      </div>
      </div>
      <div class="content-wrapper">
      <div class="notice-wrapper">
      <div class="point"></div>
      <span class="notice v-link clickable dark">关于延长北京大学国际医院放假的通知 </span>
      </div>
      <div class="notice-wrapper">
      <div class="point"></div>
      <span class="notice v-link clickable dark">北京中医药大学东方医院部分科室医生门诊医 </span>
      </div>
      <div class="notice-wrapper">
      <div class="point"></div>
      <span class="notice v-link clickable dark"> 武警总医院号源暂停更新通知 </span>
      </div>
      </div>
    </div>
    <div class="suspend-notice-list space">
    <div class="header-wrapper">
    <div class="title-wrapper">
      <div class="icon-wrapper">
      <span class="iconfont title-icon"></span>
      </div>
      <span class="title">停诊公告</span>
      </div>
      <div class="all-wrapper">
      <span>全部</span>
      <span class="iconfont icon"></span>
      </div>
      </div>
      <div class="content-wrapper">
      <div class="notice-wrapper">
      <div class="point"></div>
      <span class="notice v-link clickable dark"> 中国人民解放军总医院第六医学中心(原海军总医院)呼吸内科门诊停诊公告 </span>
      </div>
      <div class="notice-wrapper">
      <div class="point"></div>
    <span class="notice v-link clickable dark"> 首都医科大学附属北京潞河医院老年医学科门诊停诊公告 </span>
    </div>
      <div class="notice-wrapper">
        <div class="point"></div>
        <span class="notice v-link clickable dark">中日友好医院中西医结合心内科门诊停诊公告 </span>
      </div>
    </div>
    </div>
    </div>
    </div>
  </div>
</template>
<script>
import hospApi from '@/api/hospital.js'
import dictApi from '@/api/dict.js'

function toBase64(str) {
  if (typeof btoa !== 'undefined') {
    return btoa(unescape(encodeURIComponent(str)))
  }
  return Buffer.from(str).toString('base64')
}

function hospitalLogo(color, crossColor, text) {
  var svg = '<svg xmlns="http://www.w3.org/2000/svg" width="120" height="120">'
  svg += '<rect width="120" height="120" rx="24" fill="' + color + '"/>'
  svg += '<circle cx="60" cy="52" r="28" fill="rgba(255,255,255,0.25)"/>'
  svg += '<rect x="52" y="40" width="16" height="24" rx="3" fill="' + crossColor + '"/>'
  svg += '<rect x="44" y="48" width="32" height="8" rx="3" fill="' + crossColor + '"/>'
  if (text.length <= 4) {
    svg += '<text x="60" y="106" text-anchor="middle" fill="white" font-size="12" font-weight="500" font-family="sans-serif">' + text + '</text>'
  } else {
    var line1 = text.substring(0, Math.ceil(text.length / 2))
    var line2 = text.substring(Math.ceil(text.length / 2))
    svg += '<text x="60" y="100" text-anchor="middle" fill="white" font-size="11" font-weight="500" font-family="sans-serif">' + line1 + '</text>'
    svg += '<text x="60" y="114" text-anchor="middle" fill="white" font-size="11" font-weight="500" font-family="sans-serif">' + line2 + '</text>'
  }
  svg += '</svg>'
  return 'data:image/svg+xml;base64,' + toBase64(svg)
}

var HOSPITAL_IMAGES = {
  '中国人民解放军总医院': require('../assets/images/中国人民解放军总医院.png'),
  '北京大学第一医院': require('../assets/images/北京大学第一医院.png'),
  '首都医科大学附属天坛医院': require('../assets/images/北京天坛医院.png'),
  '北京积水潭医院': require('../assets/images/北京积水潭医院.png'),
  '中日友好医院': require('../assets/images/中日友好医院.png'),
  '北京朝阳医院': require('../assets/images/北京朝阳医院.png'),
  '北京大学第三医院': require('../assets/images/北京三院.png'),
  '北京安贞医院': require('../assets/images/北京安贞医院.png'),
  '首都医科大学宣武医院': require('../assets/images/首都医科大学医院.png'),
  '北京同仁医院': require('../assets/images/北京仁和医院.png'),
}

function getHospitalLogo(hosname, color, crossColor, text) {
  if (HOSPITAL_IMAGES[hosname]) {
    return HOSPITAL_IMAGES[hosname]
  }
  return hospitalLogo(color, crossColor, text)
}

var DECORATIVE_HOSPITALS = [
  { hosname: '中国人民解放军总医院', hostype: '三级甲等', release: '08:00',
    logo: getHospitalLogo('中国人民解放军总医院', '#C62828', '#fff', '301医院') },
  { hosname: '北京大学第一医院', hostype: '三级甲等', release: '07:30',
    logo: getHospitalLogo('北京大学第一医院', '#0D47A1', '#fff', '北大医院') },
  { hosname: '首都医科大学附属天坛医院', hostype: '三级甲等', release: '08:00',
    logo: getHospitalLogo('首都医科大学附属天坛医院', '#00695C', '#fff', '天坛医院') },
  { hosname: '北京积水潭医院', hostype: '三级甲等', release: '09:00',
    logo: getHospitalLogo('北京积水潭医院', '#37474F', '#fff', '积水潭医院') },
  { hosname: '中日友好医院', hostype: '三级甲等', release: '07:00',
    logo: getHospitalLogo('中日友好医院', '#BF360C', '#fff', '中日友好') },
  { hosname: '首都医科大学宣武医院', hostype: '三级甲等', release: '08:30',
    logo: getHospitalLogo('首都医科大学宣武医院', '#4A148C', '#fff', '宣武医院') },
  { hosname: '北京朝阳医院', hostype: '三级甲等', release: '07:30',
    logo: getHospitalLogo('北京朝阳医院', '#E65100', '#fff', '朝阳医院') },
  { hosname: '北京同仁医院', hostype: '三级甲等', release: '08:00',
    logo: getHospitalLogo('北京同仁医院', '#1B5E20', '#fff', '同仁医院') },
  { hosname: '北京大学第三医院', hostype: '三级甲等', release: '08:00',
    logo: getHospitalLogo('北京大学第三医院', '#01579B', '#fff', '北医三院') },
  { hosname: '北京安贞医院', hostype: '三级甲等', release: '07:00',
    logo: getHospitalLogo('北京安贞医院', '#B71C1C', '#fff', '安贞医院') },
]

function buildDecoratives() {
  return DECORATIVE_HOSPITALS.map((h, i) => ({
    id: 'decor-' + i,
    hoscode: '__decor__' + i,
    hosname: h.hosname,
    logoData: h.logo,
    param: { hostypeString: h.hostype },
    bookingRule: { releaseTime: h.release },
    decorative: true
  }))
}

export default {
  //服务端渲染异步，显示医院列表
  asyncData({ params, error }) {
    return hospApi.getPageList(1, 10, null)
      .then(response => {
        const realList = (response.data.pages.content || []).map(item => ({
          ...item,
          logoData: item.logoData ? 'data:image/jpeg;base64,' + item.logoData : item.logoData
        }))
        const decoratives = buildDecoratives()
        const list = realList.length >= 6 ? realList : [...realList, ...decoratives]
        return {
          list,
          pages: response.data.pages.totalPages
        }
      })
      .catch(() => {
        return {
          list: buildDecoratives(),
          pages: 1
        }
      })
  },
  data() {
    return {
      searchObj: {},
      page: 1,
      limit: 10,
      hosname: '', //医院名称
      hostypeList: [], //医院等级集合
      districtList: [], //地区集合
      hostypeActiveIndex: 0,
      provinceActiveIndex: 0
    }
  },
  created() {
    this.init()
  },
  methods:{
    //查询医院等级列表 和 所有地区列表
    init() {
      //查询医院等级列表
      dictApi.findByDictCode('Hostype')
        .then(response => {
          //hostypeList清空
          this.hostypeList = []
          //向hostypeList添加全部值
          this.hostypeList.push({"name":"全部","value":""})
          //把接口返回数据，添加到hostypeList
          for(var i=0;i<response.data.list.length;i++) {
              this.hostypeList.push(response.data.list[i])
          }
      })
      //查询地区数据
      dictApi.findByDictCode('Beijing')
        .then(response => {
          this.districtList = []
          this.districtList.push({"name":"全部","value":""})
          for(let i in response.data.list) {
            this.districtList.push(response.data.list[i])
          }
        })
    },
    //查询医院列表
    getList() {
      hospApi.getPageList(this.page,this.limit,this.searchObj)
        .then(response => {
          for(let i in response.data.pages.content) {
            var apiItem = response.data.pages.content[i]
            apiItem.logoData = apiItem.logoData ? 'data:image/jpeg;base64,' + apiItem.logoData : apiItem.logoData
            this.list.push(apiItem)
          }
          this.page = response.data.pages.totalPages
          if (this.list.length < 6) {
            this.list.push(...buildDecoratives())
          }
        })
        .catch(() => {
          if (this.list.length === 0) {
            this.list = buildDecoratives()
          }
        })
    },
    //根据医院等级查询
    hostypeSelect(hostype,index) {
      //准备数据
      this.list = []
      this.page = 1
      this.hostypeActiveIndex = index
      this.searchObj.hostype = hostype
      //调用查询医院列表方法
      this.getList()
    },
    //根据地区查询医院
    districtSelect(districtCode, index) {
      this.list = []
      this.page = 1
      this.provinceActiveIndex = index
      this.searchObj.districtCode = districtCode
      this.getList();
    },
    //在输入框输入值，弹出下拉框，显示相关内容 callback
    querySearchAsync(queryString, cb) {
      this.searchObj = []
      if(queryString == '') return
      hospApi.getByHosname(queryString).then(response => {
        for (let i = 0, len = response.data.list.length; i <len; i++) {
          response.data.list[i].value = response.data.list[i].hosname
        }
        cb(response.data.list)
      })
    },
    //在下拉框选择某一个内容，执行下面方法，跳转到详情页面中
    handleSelect(item) {
      window.location.href = '/hospital/' + item.hoscode
    },
    //点击某个医院名称，跳转到详情页面中
    show(hoscode) {
      if (hoscode.startsWith('__decor__')) {
        this.$message.info('该医院信息正在接入中，敬请期待')
        return
      }
      window.location.href = '/hospital/' + hoscode
    }
  }
}
</script>