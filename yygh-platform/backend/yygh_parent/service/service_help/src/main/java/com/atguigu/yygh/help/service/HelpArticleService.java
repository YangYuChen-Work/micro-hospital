package com.atguigu.yygh.help.service;

import com.atguigu.yygh.model.help.HelpArticle;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

public interface HelpArticleService extends IService<HelpArticle> {

    IPage<HelpArticle> pageQuery(Page<HelpArticle> page, Long categoryId, String keyword);

    HelpArticle getDetail(Long id);

    List<HelpArticle> getHotArticles();
}
