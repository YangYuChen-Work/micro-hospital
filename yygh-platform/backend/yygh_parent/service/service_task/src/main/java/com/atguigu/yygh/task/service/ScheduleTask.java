package com.atguigu.yygh.task.service;

import com.atguigu.yygh.rabbit.RabbitService;
import com.atguigu.yygh.rabbit.constant.MqConst;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Project   : yygh_parent
 *
 * @author LucianYoung
 * date      2026/5/11 14:45
 *
 * description 定时任务服务
 */
@Component
@EnableScheduling
public class ScheduleTask {
    @Autowired
    RabbitService rabbitService;

    //定时表达式由7个部分组成：秒,分,时,日,月,周,年份(可选)
    @Scheduled(cron = "0/10 * * * * ?")
    public void taskSchedule(){
        DateTime now = new DateTime();
        DateTime dateTime = now.plusDays(1);//计算从订单表查询，需要预约提醒的订单，并给用户发送提醒消息
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_TASK,
                MqConst.ROUTING_TASK_8,
                dateTime.toString("yyyy-MM-dd"));
    }
}
