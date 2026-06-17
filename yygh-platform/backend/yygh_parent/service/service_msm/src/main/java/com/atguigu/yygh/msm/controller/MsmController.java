package com.atguigu.yygh.msm.controller;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.msm.service.MsmService;
import com.atguigu.yygh.msm.utils.RandomUtil;
import com.atguigu.yygh.vo.msm.MsmVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/msm")
public class MsmController {

    @Autowired
    private MsmService msmService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    //根据手机号发送短信验证码
    @GetMapping(value = "/send/{phone}")
    public R code(@PathVariable String phone) {

        String code = redisTemplate.opsForValue().get(phone);
        if(!StringUtils.isEmpty(code)) return R.ok();
        //生成验证码
        code = RandomUtil.getFourBitRandom();
        MsmVo msmVo = new MsmVo();
        msmVo.setPhone(phone);

// 如果后续短信服务按模板参数发验证码，可以加上：
        Map<String, Object> param = new HashMap<>();
        param.put("code", code);
        msmVo.setParam(param);

        //调用service方法发送
        boolean is_success = msmService.send(msmVo);
        //如果发送成功，把验证码放到redis，设置有效时长
        if(is_success) {
            //key：手机号  value：验证码
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);
            return R.ok();
        } else {
            return R.error();
        }
    }
}
