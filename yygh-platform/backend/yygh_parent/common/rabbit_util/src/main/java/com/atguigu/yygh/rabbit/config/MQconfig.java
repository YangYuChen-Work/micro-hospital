package com.atguigu.yygh.rabbit.config;

import com.atguigu.yygh.rabbit.constant.MqConst;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Project   : yygh_parent
 *
 * @author LucianYoung
 * date      2026/4/23 17:23
 * description MQ配置类
 */
@Configuration
public class MQconfig {

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // ============ 预约下单相关 ============

    /**
     * 预约下单交换机
     */
    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(MqConst.EXCHANGE_DIRECT_ORDER, true, false);
    }

    /**
     * 预约下单队列
     */
    @Bean
    public Queue orderQueue() {
        return new Queue(MqConst.QUEUE_ORDER, true);
    }

    /**
     * 绑定预约下单队列到交换机
     */
    @Bean
    public Binding orderBinding() {
        return BindingBuilder.bind(orderQueue()).to(orderExchange()).with(MqConst.ROUTING_ORDER);
    }

    // ============ 短信相关 ============

    /**
     * 短信交换机
     */
    @Bean
    public DirectExchange msmExchange() {
        return new DirectExchange(MqConst.EXCHANGE_DIRECT_MSM, true, false);
    }

    /**
     * 短信队列
     */
    @Bean
    public Queue msmQueue() {
        return new Queue(MqConst.QUEUE_MSM_ITEM, true);
    }

    /**
     * 绑定短信队列到交换机
     */
    @Bean
    public Binding msmBinding() {
        return BindingBuilder.bind(msmQueue()).to(msmExchange()).with(MqConst.ROUTING_MSM_ITEM);
    }
}