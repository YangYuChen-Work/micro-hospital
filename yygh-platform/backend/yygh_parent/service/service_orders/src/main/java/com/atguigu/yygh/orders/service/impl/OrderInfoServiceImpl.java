package com.atguigu.yygh.orders.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.enums.OrderStatusEnum;
import com.atguigu.yygh.hosp.client.HospitalFeignClient;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.orders.mapper.OrderInfoMapper;
import com.atguigu.yygh.orders.service.OrderInfoService;
import com.atguigu.yygh.orders.utils.HttpRequestHelper;
import com.atguigu.yygh.rabbit.RabbitService;
import com.atguigu.yygh.rabbit.constant.MqConst;
import com.atguigu.yygh.user.client.PatientFeignClient;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import com.atguigu.yygh.vo.msm.MsmVo;
import com.atguigu.yygh.vo.order.OrderCountQueryVo;
import com.atguigu.yygh.vo.order.OrderCountVo;
import com.atguigu.yygh.vo.order.OrderMqVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 订单表 服务实现类
 */
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {

    @Autowired
    private PatientFeignClient patientFeignClient;

    @Autowired
    private HospitalFeignClient hospitalFeignClient;

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private RabbitService rabbitService;

    @Override
    public OrderInfo getOrderInfo(String orderId) {
        OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
        if (orderInfo != null) {
            // 确保param不为null
            if (orderInfo.getParam() == null) {
                orderInfo.setParam(new HashMap<>());
            }
            orderInfo.getParam().put("orderStatusString",
                    OrderStatusEnum.getStatusNameByStatus(orderInfo.getOrderStatus()));
        }
        return orderInfo;
    }

    @Override
    public Boolean cancelOrder(Long orderId) {
        //1.判断时间是否超过预约时间
        OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
        if (orderInfo == null) {
            throw new YyghException(20001, "订单不存在");
        }
        Date quitTime = orderInfo.getQuitTime();
        DateTime dateTime = new DateTime(quitTime);
        if (dateTime.isBeforeNow()) {
            throw new YyghException(20001, "超过退号时间，无法取消订单");
        }
        //2.远程调用医院微服务：取消挂号
        Map<String, Object> map = new HashMap<>();//给远程接口传递的参数
        map.put("hosRecordId",orderInfo.getHosRecordId());
        map.put("hoscode",orderInfo.getHoscode());
        JSONObject result = HttpRequestHelper.sendRequest(map, "http://localhost:9998/order/updateCancelStatus");

        if(result.getInteger("code") ==200 ){
            //3.医院取消成功后，更新订单状态为已取消

            //3.1 修改自己的订单数据 -1
            orderInfo.setOrderStatus(OrderStatusEnum.CANCLE.getStatus());
            orderInfoMapper.updateById(orderInfo);

            //3.2 修改自己的排班数据,号源数量 MQ异步更新

            //3.3 MQ发送取消订单消息到队列
            OrderMqVo orderMqVo = new OrderMqVo();
            orderMqVo.setScheduleId(orderInfo.getScheduleId());

            MsmVo msmVo = new MsmVo();
            msmVo.setPhone(orderInfo.getPatientPhone());
            msmVo.setTemplateCode("908e94ccf08b4476ba6c876d13f084ad");

            orderMqVo.setMsmVo(msmVo);

            //发送预约挂号消息和预约取号消息用同一个队列与同一个交换机，消费者根据传递可预约数量与剩余数量就执行挂号数据更新
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_ORDER, MqConst.ROUTING_ORDER, orderMqVo);

        }else{
            throw new YyghException(20001, "医院取消订单失败");
        }

        //4.医院取消失败，返回false

        return true;
    }

    @Override
    public List<OrderInfo> searchOrders(String phone, String patientName) {
        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderInfo::getPatientPhone, phone);
        queryWrapper.eq(OrderInfo::getPatientName, patientName);
        queryWrapper.orderByDesc(OrderInfo::getCreateTime);
        List<OrderInfo> list = orderInfoMapper.selectList(queryWrapper);
        for (OrderInfo orderInfo : list) {
            if (orderInfo.getParam() == null) {
                orderInfo.setParam(new HashMap<>());
            }
            orderInfo.getParam().put("orderStatusString",
                    OrderStatusEnum.getStatusNameByStatus(orderInfo.getOrderStatus()));
        }
        return list;
    }

    /**
     * 预约提醒(给这一天的所有就诊人发送提醒消息)
     *
     * @param dateString
     */
    @Override
    public void patientTips(String dateString) {
        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderInfo::getReserveDate, dateString);
        queryWrapper.ne(OrderInfo::getOrderStatus, OrderStatusEnum.CANCLE.getStatus());
        List<OrderInfo> orderInfos = orderInfoMapper.selectList(queryWrapper);
        if(!CollectionUtils.isEmpty(orderInfos)){
            for (OrderInfo orderInfo : orderInfos) {
                String patientPhone = orderInfo.getPatientPhone();
                MsmVo msmVo = new MsmVo();
                msmVo.setPhone(patientPhone);
                msmVo.setTemplateCode("908e94ccf08b4476ba6c876d13f084ad");
                rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_MSM, MqConst.ROUTING_MSM_ITEM, msmVo);
            }
        }
    }

    /**
     * 获取订单统计数据
     *
     * @param orderCountQueryVo
     * @return
     */
    @Override
    public Map<String,Object > getCountMap(OrderCountQueryVo orderCountQueryVo) {
        List<OrderCountVo> orderCountList = orderInfoMapper.selectOrderCount(orderCountQueryVo);
        Map<String,Object> result = new HashMap<>();

        if(CollectionUtils.isEmpty(orderCountList)){
            result.put("dateList", Collections.emptyList());
            result.put("countList", Collections.emptyList());
            return result;
        }


        List<String> dateList = orderCountList.stream().map(OrderCountVo::getReserveDate).toList();
        List<Integer> countList = orderCountList.stream().map(OrderCountVo::getCount).toList();
        result.put("dateList",dateList);
        result.put("countList",countList);
        return result;
    }

    /**
     * 创建订单
     *
     * @param scheduleId
     * @param patientId
     * @return
     */
    @Override
    public String createOrder(String scheduleId, Long patientId) {
        //远程调用用户微服务：根据就诊人id获取就诊人信息(OpenFeign调用)
        Patient patient = patientFeignClient.getPatientInfo(patientId);

        //远程调用医院微服务：根据排班id获取schedule信息
        ScheduleOrderVo scheduleOrderVo = hospitalFeignClient.getScheduleOrderVo(scheduleId);

        //封装请求参数，远程调用医院模拟HIS挂号接口，完成下单
        Map<String, Object> paramMap = new HashMap<>();

        //订单信息
        paramMap.put("hoscode", scheduleOrderVo.getHoscode());//医院编号
        paramMap.put("depcode", scheduleOrderVo.getDepcode());//科室编号
        paramMap.put("hosScheduleId", scheduleOrderVo.getHosScheduleId());//排班编号
        paramMap.put("reserveDate", new DateTime(scheduleOrderVo.getReserveDate()).toString("yyyy-MM-dd"));//安排日期
        paramMap.put("reserveTime", scheduleOrderVo.getReserveTime());//安排时间
        paramMap.put("amount", scheduleOrderVo.getAmount()); //挂号费用

        //就诊人信息
        paramMap.put("name", patient.getName());//姓名
        paramMap.put("certificatesType", patient.getCertificatesType());//证件类型
        paramMap.put("certificatesNo", patient.getCertificatesNo());//证件号
        paramMap.put("sex", patient.getSex());//性别
        paramMap.put("birthdate", patient.getBirthdate());//出生日期
        paramMap.put("phone", patient.getPhone());//手机号
        paramMap.put("isMarry", patient.getIsMarry());//是否结婚
        paramMap.put("provinceCode", patient.getProvinceCode());//省份编码
        paramMap.put("cityCode", patient.getCityCode());//城市编码
        paramMap.put("districtCode", patient.getDistrictCode());//区县编码
        paramMap.put("address", patient.getAddress());//详细地址

        //联系人
        paramMap.put("contactsName", patient.getContactsName());//联系人姓名
        paramMap.put("contactsCertificatesType", patient.getContactsCertificatesType());//联系人证件类型
        paramMap.put("contactsCertificatesNo", patient.getContactsCertificatesNo());//联系人证件号
        paramMap.put("contactsPhone", patient.getContactsPhone());//联系人手机号
        paramMap.put("timestamp", HttpRequestHelper.getTimestamp()); //时间戳

        //TODO 签名，进行MD5加密，并且转为小写
        String sign = HttpRequestHelper.getSign(paramMap, "1a2b3c4d5e6f");
        paramMap.put("sign", sign);//签名

        //远程调用医院模拟HIS挂号接口，完场下单
        JSONObject result = HttpRequestHelper.sendRequest(paramMap, "http://localhost:9998/order/submitOrder");
        if (result.getJSONObject("data") != null) {
            //挂号成功，返回订单号
            //1.保存订单
            OrderInfo orderInfo = new OrderInfo();
            BeanUtils.copyProperties(scheduleOrderVo, orderInfo);
            //(1).由于是HIS模拟系统，直接写死模拟数据，真是开发需要JWT令牌中获取
            orderInfo.setUserId(26L);
            //(2).挂号平台的交易订单流水号
            String outTradeNo = System.currentTimeMillis() + "" + new Random().nextInt(999999999);
            orderInfo.setOutTradeNo(outTradeNo);
            //orderInfo.setScheduleId(scheduleOrderVo.getHosScheduleId());模拟HIS系统返回的的
            orderInfo.setScheduleId(scheduleId);//存储MongoDB中的scheduleID
            orderInfo.setPatientId(patientId);
            orderInfo.setPatientName(patient.getName());
            orderInfo.setPatientPhone(patient.getPhone());
            orderInfo.setHosRecordId(result.getJSONObject("data").getString("hosRecordId"));
            orderInfo.setNumber(result.getJSONObject("data").getInteger("number"));
            orderInfo.setFetchTime(result.getJSONObject("data").getString("fetchTime"));
            orderInfo.setFetchAddress(result.getJSONObject("data").getString("fetchAddress"));
            orderInfo.setOrderStatus(0);
            orderInfoMapper.insert(orderInfo);

            //2.修改排班数量MongoDB
            //系统优化：采用异步操作
            //排班可预约数
            Integer reservedNumber = result.getInteger("reservedNumber");
            //排班剩余预约数
            Integer availableNumber = result.getInteger("availableNumber");
            //发送mq信息更新号源和短信通知
            OrderMqVo orderMqVo = new OrderMqVo();
            orderMqVo.setScheduleId(scheduleId);
            orderMqVo.setReservedNumber(reservedNumber);
            orderMqVo.setAvailableNumber(availableNumber);

            //短信提示
            MsmVo msmVo = new MsmVo();
            msmVo.setPhone(orderInfo.getPatientPhone());
            String reserveDate =
                    new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd")
                            + (orderInfo.getReserveTime() == 0 ? "上午" : "下午");
            //短信模版参数
            Map<String, Object> param = new HashMap<String, Object>() {{
                put("title", orderInfo.getHosname() + "|" + orderInfo.getDepname() + "|" + orderInfo.getTitle());
                put("amount", orderInfo.getAmount());
                put("reserveDate", reserveDate);
                put("name", orderInfo.getPatientName());
                put("quitTime", new DateTime(orderInfo.getQuitTime()).toString("yyyy-MM-dd HH:mm"));
            }};
            msmVo.setParam(param);
            orderMqVo.setMsmVo(msmVo);
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_ORDER, MqConst.ROUTING_ORDER, orderMqVo);
            
            return orderInfo.getId().toString();
        } else {//下单失败
            System.out.println("下单失败");
            throw new YyghException(20001, "下单失败");
        }
        //返回订单号
        //3.发短信提醒就诊人

    }
}

