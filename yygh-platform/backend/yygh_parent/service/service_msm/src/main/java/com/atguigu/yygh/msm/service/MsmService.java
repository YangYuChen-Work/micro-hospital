package com.atguigu.yygh.msm.service;

import com.atguigu.yygh.vo.msm.MsmVo;

public interface MsmService {
    //发送短信接口
    boolean send(MsmVo msmVo);


}
