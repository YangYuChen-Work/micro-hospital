package com.atguigu.yygh.help.service.impl;

import com.atguigu.yygh.help.mapper.HelpFeedbackMapper;
import com.atguigu.yygh.help.service.HelpFeedbackService;
import com.atguigu.yygh.model.help.HelpFeedback;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class HelpFeedbackServiceImpl extends ServiceImpl<HelpFeedbackMapper, HelpFeedback> implements HelpFeedbackService {

    @Override
    public IPage<HelpFeedback> pageQuery(Page<HelpFeedback> page, Integer status) {
        QueryWrapper<HelpFeedback> wrapper = new QueryWrapper<>();
        if (status != null) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("create_time");
        return baseMapper.selectPage(page, wrapper);
    }
}
