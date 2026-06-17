package com.atguigu.yygh.statistics.controller;


import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.order.client.service_order_client.OrderFeignClient;
import com.atguigu.yygh.vo.order.OrderCountQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Project   : yygh_parent
 *
 * @author LucianYoung
 * date      2026/5/11 21:21
 * description
 */
@RestController
@RequestMapping("/admin/statistics")
public class orderInfoStaController {
    @Autowired
    OrderFeignClient orderFeignClient;

    @GetMapping("getCountMap")
    public R getCountMap(OrderCountQueryVo orderCountQueryVo) {
        Map<String, Object> countMap = orderFeignClient.getCountMap(orderCountQueryVo);
        return R.ok().data(countMap);
    }
}
