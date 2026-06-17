package com.atguigu.yygh.order.client.service_order_client.fallback;


import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.order.client.service_order_client.OrderFeignClient;
import com.atguigu.yygh.vo.order.OrderCountQueryVo;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Project   : yygh_parent
 * @author    LucianYoung
 * date      2026/5/11 21:42
 * description 
 */
@Component
public class OrderDegradeFeignClient implements OrderFeignClient {

    @Override
    public Map<String, Object> getCountMap(OrderCountQueryVo orderCountQueryVo) {
        throw new YyghException(20001,"远程调用失败");
    }
}
