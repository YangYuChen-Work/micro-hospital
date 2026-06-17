package com.atguigu.yygh.rabbit;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Project   : yygh_parent
 *
 * @author LucianYoung
 * date      2026/4/23 17:27
 * description Rabbit服务
 */
@Service
public class RabbitService {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public boolean sendMessage(String exchange, String routingKey, Object message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
        return true;
    }

}
