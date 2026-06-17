package com.atguigu.yygh.help.service;

import com.atguigu.yygh.model.help.HelpFeedback;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface HelpFeedbackService extends IService<HelpFeedback> {

    IPage<HelpFeedback> pageQuery(Page<HelpFeedback> page, Integer status);
}
