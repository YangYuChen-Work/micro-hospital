package com.atguigu.yygh.help.service;

import com.atguigu.yygh.model.help.HelpCategory;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

public interface HelpCategoryService extends IService<HelpCategory> {

    List<HelpCategory> getActiveCategories();
}
