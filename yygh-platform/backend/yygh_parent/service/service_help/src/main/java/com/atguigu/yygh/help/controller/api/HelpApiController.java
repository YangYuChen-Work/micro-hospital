package com.atguigu.yygh.help.controller.api;

import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.help.service.HelpArticleService;
import com.atguigu.yygh.help.service.HelpCategoryService;
import com.atguigu.yygh.help.service.HelpFeedbackService;
import com.atguigu.yygh.model.help.HelpArticle;
import com.atguigu.yygh.model.help.HelpCategory;
import com.atguigu.yygh.model.help.HelpFeedback;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/help")
public class HelpApiController {

    @Autowired
    private HelpCategoryService helpCategoryService;

    @Autowired
    private HelpArticleService helpArticleService;

    @Autowired
    private HelpFeedbackService helpFeedbackService;

    @GetMapping("category/list")
    public R getCategories() {
        List<HelpCategory> list = helpCategoryService.getActiveCategories();
        return R.ok().data("list", list);
    }

    @GetMapping("article/list")
    public R getArticleList(@RequestParam(defaultValue = "1") Integer page,
                            @RequestParam(defaultValue = "10") Integer limit,
                            @RequestParam(required = false) Long categoryId,
                            @RequestParam(required = false) String keyword) {
        Page<HelpArticle> pageParam = new Page<>(page, limit);
        IPage<HelpArticle> result = helpArticleService.pageQuery(pageParam, categoryId, keyword);
        return R.ok().data("records", result.getRecords()).data("total", result.getTotal());
    }

    @GetMapping("article/search")
    public R searchArticles(@RequestParam String keyword,
                            @RequestParam(defaultValue = "1") Integer page,
                            @RequestParam(defaultValue = "10") Integer limit) {
        Page<HelpArticle> pageParam = new Page<>(page, limit);
        IPage<HelpArticle> result = helpArticleService.pageQuery(pageParam, null, keyword);
        return R.ok().data("records", result.getRecords()).data("total", result.getTotal());
    }

    @GetMapping("article/detail/{id}")
    public R getArticleDetail(@PathVariable Long id) {
        HelpArticle article = helpArticleService.getDetail(id);
        return R.ok().data("article", article);
    }

    @GetMapping("article/hot")
    public R getHotArticles() {
        List<HelpArticle> list = helpArticleService.getHotArticles();
        return R.ok().data("list", list);
    }

    @PostMapping("feedback/submit")
    public R submitFeedback(@RequestBody HelpFeedback feedback) {
        helpFeedbackService.save(feedback);
        return R.ok();
    }
}
