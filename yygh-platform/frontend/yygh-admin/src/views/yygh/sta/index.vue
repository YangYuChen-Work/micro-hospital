<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="searchObj" class="search-form">
        <el-form-item label="医院名称">
          <el-select
            v-model="searchObj.hosname"
            placeholder="全部医院"
            clearable
            filterable
            style="width: 220px"
            @change="handleHosChange"
          >
            <el-option
              v-for="item in hospitalList"
              :key="item.hoscode"
              :label="item.hosname"
              :value="item.hosname"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="开始日期">
          <el-date-picker
            v-model="searchObj.reserveDateBegin"
            type="date"
            placeholder="选择开始日期"
            value-format="yyyy-MM-dd"
          />
        </el-form-item>
        <el-form-item label="截止日期">
          <el-date-picker
            v-model="searchObj.reserveDateEnd"
            type="date"
            placeholder="选择截止日期"
            value-format="yyyy-MM-dd"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            icon="el-icon-search"
            :loading="loading"
            @click="showChart"
          >
            查询
          </el-button>
          <el-button icon="el-icon-refresh" @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 统计汇总卡片 -->
    <el-row :gutter="20" class="summary-row" v-if="yData.length > 0">
      <el-col :span="6">
        <el-card shadow="hover" class="summary-card">
          <div class="summary-inner">
            <div class="summary-icon total-icon">
              <i class="el-icon-s-order" />
            </div>
            <div class="summary-info">
              <div class="summary-label">预约总量</div>
              <div class="summary-value">{{ totalCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="summary-card">
          <div class="summary-inner">
            <div class="summary-icon avg-icon">
              <i class="el-icon-s-data" />
            </div>
            <div class="summary-info">
              <div class="summary-label">日均预约</div>
              <div class="summary-value">{{ avgCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="summary-card">
          <div class="summary-inner">
            <div class="summary-icon peak-icon">
              <i class="el-icon-s-flag" />
            </div>
            <div class="summary-info">
              <div class="summary-label">单日峰值</div>
              <div class="summary-value">{{ peakCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="summary-card">
          <div class="summary-inner">
            <div class="summary-icon trend-icon">
              <i class="el-icon-s-marketing" />
            </div>
            <div class="summary-info">
              <div class="summary-label">统计天数</div>
              <div class="summary-value">{{ xData.length }}天</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20">
      <!-- 折线图 -->
      <el-col :span="24">
        <el-card shadow="never" class="chart-card">
          <div slot="header" class="chart-header">
            <span>预约趋势</span>
          </div>
          <div id="lineChart" ref="lineChart" class="chart-container" />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <!-- 柱状图 -->
      <el-col :span="24">
        <el-card shadow="never" class="chart-card">
          <div slot="header" class="chart-header">
            <span>每日预约量</span>
          </div>
          <div id="barChart" ref="barChart" class="chart-container" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 无数据提示 -->
    <el-empty
      v-if="!loading && yData.length === 0 && searched"
      description="暂无统计数据"
    />
  </div>
</template>

<script>
import echarts from 'echarts'
import statisticsApi from '@/api/yygh/sta'
import hospApi from '@/api/yygh/hosp'

export default {
  name: 'StatisticsOrder',
  data() {
    const end = new Date()
    const start = new Date()
    start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
    return {
      searchObj: {
        hosname: '',
        reserveDateBegin: this.formatDate(start),
        reserveDateEnd: this.formatDate(end)
      },
      hospitalList: [],
      loading: false,
      searched: false,
      xData: [],
      yData: [],
      lineChart: null,
      barChart: null
    }
  },
  computed: {
    totalCount() {
      return this.yData.reduce((sum, v) => sum + v, 0)
    },
    avgCount() {
      if (this.xData.length === 0) return 0
      return Math.round(this.totalCount / this.xData.length)
    },
    peakCount() {
      if (this.yData.length === 0) return 0
      return Math.max(...this.yData)
    }
  },
  mounted() {
    this.fetchHospitals()
    this.$nextTick(() => {
      this.showChart()
    })
  },
  methods: {
    formatDate(date) {
      const y = date.getFullYear()
      const m = String(date.getMonth() + 1).padStart(2, '0')
      const d = String(date.getDate()).padStart(2, '0')
      return `${y}-${m}-${d}`
    },
    fetchHospitals() {
      hospApi.getPageList(1, 500, {}).then(res => {
        this.hospitalList = res.data.records || []
      })
    },
    handleHosChange() {
      // hospital name change — will re-query on button click
    },
    resetSearch() {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
      this.searchObj = {
        hosname: '',
        reserveDateBegin: this.formatDate(start),
        reserveDateEnd: this.formatDate(end)
      }
      this.showChart()
    },
    showChart() {
      this.loading = true
      this.searched = true
      statisticsApi.getCountMap(this.searchObj).then(response => {
        this.yData = response.data.countList || []
        this.xData = response.data.dateList || []
        this.loading = false
        this.$nextTick(() => {
          this.initLineChart()
          this.initBarChart()
        })
      }).catch(() => {
        this.yData = []
        this.xData = []
        this.loading = false
        this.$message.warning('获取统计数据失败，请确认查询条件是否正确')
      })
    },
    getColor(index) {
      const colors = [
        '#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de',
        '#3ba272', '#fc8452', '#9a60b4', '#ea7ccc', '#60c0dd'
      ]
      return colors[index % colors.length]
    },
    initLineChart() {
      if (this.lineChart) {
        this.lineChart.dispose()
      }
      const dom = document.getElementById('lineChart')
      if (!dom) return
      this.lineChart = echarts.init(dom)
      const option = {
        color: ['#5470c6', '#91cc75'],
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'cross' },
          backgroundColor: 'rgba(255,255,255,0.95)',
          borderColor: '#e4e7ed',
          textStyle: { color: '#303133' },
          extraCssText: 'box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1)'
        },
        grid: {
          left: '3%',
          right: '8%',
          bottom: '3%',
          top: '12%',
          containLabel: true
        },
        legend: {
          data: ['预约数量', '趋势线'],
          top: 0,
          textStyle: { fontSize: 13 }
        },
        xAxis: {
          type: 'category',
          data: this.xData,
          boundaryGap: false,
          axisLine: { lineStyle: { color: '#dcdfe6' } },
          axisLabel: { color: '#909399', rotate: this.xData.length > 15 ? 35 : 0 }
        },
        yAxis: {
          type: 'value',
          name: '预约数',
          minInterval: 1,
          splitLine: { lineStyle: { color: '#f2f3f5', type: 'dashed' } },
          axisLabel: { color: '#909399' }
        },
        dataZoom: this.xData.length > 15 ? [
          { type: 'slider', start: 0, end: 100, height: 20, bottom: 0 }
        ] : [],
        series: [
          {
            name: '预约数量',
            type: 'line',
            smooth: true,
            symbol: 'circle',
            symbolSize: 6,
            lineStyle: { width: 2.5 },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(84, 112, 198, 0.25)' },
                { offset: 1, color: 'rgba(84, 112, 198, 0.02)' }
              ])
            },
            data: this.yData,
            markPoint: {
              data: [
                { type: 'max', name: '最大值', symbol: 'pin', symbolSize: 35 },
                { type: 'min', name: '最小值', symbol: 'pin', symbolSize: 35 }
              ]
            },
            markLine: {
              silent: true,
              lineStyle: { color: '#fc8452', type: 'dashed' },
              data: [{ type: 'average', name: '平均值' }]
            }
          },
          {
            name: '趋势线',
            type: 'line',
            smooth: true,
            symbol: 'none',
            lineStyle: { width: 1.5, type: 'dashed', color: '#91cc75' },
            data: this.calcTrendLine()
          }
        ]
      }
      this.lineChart.setOption(option)
      window.addEventListener('resize', () => this.lineChart && this.lineChart.resize())
    },
    calcTrendLine() {
      if (this.yData.length < 2) return this.yData
      const n = this.yData.length
      const xMean = (n - 1) / 2
      const yMean = this.yData.reduce((a, b) => a + b, 0) / n
      let num = 0
      let den = 0
      for (let i = 0; i < n; i++) {
        num += (i - xMean) * (this.yData[i] - yMean)
        den += (i - xMean) * (i - xMean)
      }
      const slope = den === 0 ? 0 : num / den
      const intercept = yMean - slope * xMean
      return this.yData.map((_, i) => Math.round((slope * i + intercept) * 100) / 100)
    },
    initBarChart() {
      if (this.barChart) {
        this.barChart.dispose()
      }
      const dom = document.getElementById('barChart')
      if (!dom) return
      this.barChart = echarts.init(dom)
      const option = {
        color: ['#5470c6'],
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' },
          backgroundColor: 'rgba(255,255,255,0.95)',
          borderColor: '#e4e7ed',
          textStyle: { color: '#303133' },
          extraCssText: 'box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1)',
          formatter: params => {
            const p = params[0]
            const color = this.getColor(p.dataIndex)
            return `<span style="display:inline-block;width:8px;height:8px;border-radius:50%;background:${color};margin-right:6px;"></span>
              <b>${p.name}</b><br/>
              预约量：<b style="color:#5470c6">${p.value}</b> 单`
          }
        },
        grid: {
          left: '3%',
          right: '8%',
          bottom: '3%',
          top: '12%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: this.xData,
          axisLine: { lineStyle: { color: '#dcdfe6' } },
          axisLabel: { color: '#909399', rotate: this.xData.length > 15 ? 35 : 0 },
          axisTick: { alignWithLabel: true }
        },
        yAxis: {
          type: 'value',
          name: '预约数',
          minInterval: 1,
          splitLine: { lineStyle: { color: '#f2f3f5', type: 'dashed' } },
          axisLabel: { color: '#909399' }
        },
        dataZoom: this.xData.length > 15 ? [
          { type: 'slider', start: 0, end: 100, height: 20, bottom: 0 }
        ] : [],
        series: [{
          type: 'bar',
          barWidth: '50%',
          data: this.yData.map((v, i) => ({
            value: v,
            itemStyle: {
              borderRadius: [4, 4, 0, 0],
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: this.getColor(i) },
                { offset: 1, color: this.lightenColor(this.getColor(i), 0.3) }
              ])
            }
          })),
          emphasis: {
            itemStyle: { shadowBlur: 10, shadowColor: 'rgba(0,0,0,0.15)' }
          }
        }]
      }
      this.barChart.setOption(option)
      window.addEventListener('resize', () => this.barChart && this.barChart.resize())
    },
    lightenColor(color, amount) {
      const hex = color.replace('#', '')
      const r = Math.min(255, parseInt(hex.slice(0, 2), 16) + Math.round(255 * amount))
      const g = Math.min(255, parseInt(hex.slice(2, 4), 16) + Math.round(255 * amount))
      const b = Math.min(255, parseInt(hex.slice(4, 6), 16) + Math.round(255 * amount))
      return `rgb(${r},${g},${b})`
    }
  }
}
</script>

<style scoped>
.search-card {
  margin-bottom: 20px;
}
.search-form {
  padding: 4px 0;
}
.summary-row {
  margin-bottom: 24px;
}
.summary-row .el-col {
  margin-bottom: 16px;
}
.summary-card {
  cursor: default;
}
.summary-card >>> .el-card__body {
  padding: 24px 20px;
}
.summary-inner {
  display: flex;
  align-items: center;
  gap: 20px;
}
.summary-icon {
  width: 60px;
  height: 60px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #fff;
  flex-shrink: 0;
}
.total-icon {
  background: linear-gradient(135deg, #5470c6, #7b93e0);
}
.avg-icon {
  background: linear-gradient(135deg, #91cc75, #aee39a);
}
.peak-icon {
  background: linear-gradient(135deg, #fac858, #fcdb7a);
}
.trend-icon {
  background: linear-gradient(135deg, #ee6666, #f48b8b);
}
.summary-info {
  flex: 1;
}
.summary-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 6px;
}
.summary-value {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  line-height: 1.3;
}
.chart-card {
  margin-bottom: 0;
}
.chart-card >>> .el-card__body {
  padding: 20px 24px;
}
.chart-header {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}
.chart-header::before {
  content: '';
  display: inline-block;
  width: 4px;
  height: 16px;
  background: #5470c6;
  border-radius: 2px;
  margin-right: 8px;
  vertical-align: middle;
  margin-top: -2px;
}
.chart-container {
  width: 100%;
  height: 420px;
}
</style>
