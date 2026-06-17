package com.atguigu.yygh.msm.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.msm.service.MsmService;
import com.atguigu.yygh.msm.utils.HttpUtils;
import com.atguigu.yygh.msm.utils.RandomUtil;
import com.atguigu.yygh.vo.msm.MsmVo;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MsmServiceImpl implements MsmService {
    //发送短信实现
    @Override
    public boolean send(MsmVo msmVo) {
        if (StringUtils.isEmpty(msmVo.getPhone())) {
            return false;
        }
        String templateCode = msmVo.getTemplateCode();
        if (!StringUtils.isEmpty(templateCode) && msmVo.getParam() != null) {
            return this.sendTemplateMessage(msmVo.getPhone(), templateCode, msmVo.getParam());
        }
        String code = RandomUtil.getFourBitRandom();
        return this.sendMessage(msmVo.getPhone(), code);
    }

    //模板短信发送
    private boolean sendTemplateMessage(String phone, String templateCode, Map<String, Object> param) {
        String host = "https://gyytz.market.alicloudapi.com";
        String path = "/sms/smsSend";
        String method = "POST";
        String appcode = "5d9487a7572243fd834fc873759bf9e4";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("mobile", phone);
        querys.put("param", JSONObject.toJSONString(param));
        querys.put("smsSignId", "2e65b1bb3d054466b82f0c9d125465e2");
        querys.put("templateId", templateCode);
        Map<String, String> bodys = new HashMap<String, String>();

        try {
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                String responseBody = EntityUtils.toString(response.getEntity());
                System.out.println(responseBody);
                return true;
            }
            System.out.println("SMS API returned status: " + statusCode);
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //发送短信方法
    private boolean sendMessage(String phone, String verifyCode) {
        String host = "https://gyytz.market.alicloudapi.com";
        String path = "/sms/smsSend";
        String method = "POST";
        String appcode = "5d9487a7572243fd834fc873759bf9e4";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("mobile", phone);
        querys.put("param", "{\"code\":\"" + verifyCode + "\",\"minute\":\"5\"}");
        //smsSignId（短信前缀）和templateId（短信模板），可登录国阳云控制台自助申请。参考文档：http://help.guoyangyun.com/Problem/Qm.html
        querys.put("smsSignId", "2e65b1bb3d054466b82f0c9d125465e2");
        querys.put("templateId", "908e94ccf08b4476ba6c876d13f084ad");
        Map<String, String> bodys = new HashMap<String, String>();

        try {
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys,
                    bodys);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                String responseBody = EntityUtils.toString(response.getEntity());
                System.out.println(responseBody);
                // 可以在这里解析 responseBody 判断是否真正成功
                return true;
            }
            System.out.println("SMS API returned status: " + statusCode);
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
