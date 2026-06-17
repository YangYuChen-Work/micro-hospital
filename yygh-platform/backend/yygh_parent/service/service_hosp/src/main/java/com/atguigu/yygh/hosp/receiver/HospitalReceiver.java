package com.atguigu.yygh.hosp.receiver;

import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.rabbit.RabbitService;
import com.atguigu.yygh.rabbit.constant.MqConst;
import com.atguigu.yygh.vo.msm.MsmVo;
import com.atguigu.yygh.vo.order.OrderMqVo;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Project   : yygh_parent
 *
 * @author LucianYoung
 * date      2026/4/23 21:30
 * description
 */
@Component
public class HospitalReceiver {
    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private RabbitService rabbitService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_ORDER, durable = "true"),
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_ORDER),
            key = {MqConst.ROUTING_ORDER}
    ))
    public void receiver(OrderMqVo orderMqVo, Message message, Channel channel) throws IOException {
        //判断是否有预约数,预约挂号处理
        if(orderMqVo.getAvailableNumber()!=null){
            //下单成功更新预约数
            Schedule schedule = scheduleService.getScheduleId(orderMqVo.getScheduleId());
            schedule.setReservedNumber(orderMqVo.getReservedNumber());
            schedule.setAvailableNumber(orderMqVo.getAvailableNumber());
            scheduleService.update(schedule);

            //发送短信
            MsmVo msmVo = orderMqVo.getMsmVo();
            if(null != msmVo) {
                rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_MSM, MqConst.ROUTING_MSM_ITEM, msmVo);
            }
        }else{//取消挂号处理
            //下单成功更新预约数
            Schedule schedule = scheduleService.getScheduleId(orderMqVo.getScheduleId());
            Integer availableNumber = orderMqVo.getAvailableNumber();
            if (availableNumber == null) {
                availableNumber = schedule.getAvailableNumber();
            }
            if (availableNumber == null) {
                availableNumber = 0;
            }
            schedule.setAvailableNumber(availableNumber + 1);
            scheduleService.update(schedule);

            //发送短信
            MsmVo msmVo = orderMqVo.getMsmVo();
            if(null != msmVo) {
                rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_MSM, MqConst.ROUTING_MSM_ITEM, msmVo);
            }

        }

    }
}