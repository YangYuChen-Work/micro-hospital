package com.atguigu.yygh.help.service.impl;

import com.atguigu.yygh.help.mapper.HelpArticleMapper;
import com.atguigu.yygh.help.service.HelpArticleService;
import com.atguigu.yygh.model.help.HelpArticle;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.List;

@Service
public class HelpArticleServiceImpl extends ServiceImpl<HelpArticleMapper, HelpArticle> implements HelpArticleService {

    @Override
    public IPage<HelpArticle> pageQuery(Page<HelpArticle> page, Long categoryId, String keyword) {
        QueryWrapper<HelpArticle> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1);
        if (categoryId != null) {
            wrapper.eq("category_id", categoryId);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like("title", keyword).or().like("content", keyword));
        }
        wrapper.orderByDesc("sort_order").orderByDesc("view_count");
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public HelpArticle getDetail(Long id) {
        baseMapper.update(null, new LambdaUpdateWrapper<HelpArticle>()
                .eq(HelpArticle::getId, id)
                .setSql("view_count = view_count + 1"));
        return baseMapper.selectById(id);
    }

    @Override
    public List<HelpArticle> getHotArticles() {
        QueryWrapper<HelpArticle> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1).orderByDesc("view_count").last("LIMIT 10");
        return baseMapper.selectList(wrapper);
    }
}
