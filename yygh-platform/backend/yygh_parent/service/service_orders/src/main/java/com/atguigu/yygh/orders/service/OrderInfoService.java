package com.atguigu.yygh.orders.service;

import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.vo.order.OrderCountQueryVo;
import com.atguigu.yygh.vo.order.OrderCountVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 订单表 服务类
 */
public interface OrderInfoService extends IService<OrderInfo> {

    /**
     * 创建订单
     * @param scheduleId
     * @param patientId
     * @return
     */
    String createOrder(String scheduleId, Long patientId);

    OrderInfo getOrderInfo(String orderId);

     /**
     * 取消订单
     * @param orderId
     */
    Boolean cancelOrder(Long orderId);

     /**
     * 预约提醒(给这一天的所有就诊人发送提醒消息)
     * @param dateString
     */
    void patientTips(String dateString);

    /**
     * 获取订单统计数据
     * @param orderCountQueryVo
     * @return
     */
    Map<String,Object> getCountMap(OrderCountQueryVo orderCountQueryVo);

    /**
     * 根据手机号和就诊人姓名查询订单
     * @param phone
     * @param patientName
     * @return
     */
    List<OrderInfo> searchOrders(String phone, String patientName);
}
