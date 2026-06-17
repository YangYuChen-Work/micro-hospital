package com.atguigu.yygh.help.service.impl;

import com.atguigu.yygh.help.mapper.HelpCategoryMapper;
import com.atguigu.yygh.help.service.HelpCategoryService;
import com.atguigu.yygh.model.help.HelpCategory;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class HelpCategoryServiceImpl extends ServiceImpl<HelpCategoryMapper, HelpCategory> implements HelpCategoryService {

    @Override
    public List<HelpCategory> getActiveCategories() {
        QueryWrapper<HelpCategory> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1).orderByAsc("sort_order");
        return baseMapper.selectList(wrapper);
    }
}
