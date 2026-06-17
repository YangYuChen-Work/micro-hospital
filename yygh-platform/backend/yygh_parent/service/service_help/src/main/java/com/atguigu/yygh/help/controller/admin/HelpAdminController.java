package com.atguigu.yygh.help.controller.admin;

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
@RequestMapping("/admin/help")
public class HelpAdminController {

    @Autowired
    private HelpCategoryService helpCategoryService;

    @Autowired
    private HelpArticleService helpArticleService;

    @Autowired
    private HelpFeedbackService helpFeedbackService;

    // ========== 分类管理 ==========

    @GetMapping("category/list")
    public R getCategoryList() {
        List<HelpCategory> list = helpCategoryService.list();
        return R.ok().data("list", list);
    }

    @PostMapping("category/add")
    public R addCategory(@RequestBody HelpCategory category) {
        helpCategoryService.save(category);
        return R.ok();
    }

    @PutMapping("category/update")
    public R updateCategory(@RequestBody HelpCategory category) {
        helpCategoryService.updateById(category);
        return R.ok();
    }

    @DeleteMapping("category/delete/{id}")
    public R deleteCategory(@PathVariable Long id) {
        helpCategoryService.removeById(id);
        return R.ok();
    }

    // ========== 文章管理 ==========

    @GetMapping("article/page/{page}/{limit}")
    public R getArticlePage(@PathVariable Integer page,
                            @PathVariable Integer limit,
                            @RequestParam(required = false) Long categoryId,
                            @RequestParam(required = false) String keyword) {
        Page<HelpArticle> pageParam = new Page<>(page, limit);
        IPage<HelpArticle> result = helpArticleService.pageQuery(pageParam, categoryId, keyword);
        return R.ok().data("records", result.getRecords()).data("total", result.getTotal());
    }

    @GetMapping("article/get/{id}")
    public R getArticleById(@PathVariable Long id) {
        HelpArticle article = helpArticleService.getById(id);
        return R.ok().data("article", article);
    }

    @PostMapping("article/add")
    public R addArticle(@RequestBody HelpArticle article) {
        helpArticleService.save(article);
        return R.ok();
    }

    @PutMapping("article/update")
    public R updateArticle(@RequestBody HelpArticle article) {
        helpArticleService.updateById(article);
        return R.ok();
    }

    @DeleteMapping("article/delete/{id}")
    public R deleteArticle(@PathVariable Long id) {
        helpArticleService.removeById(id);
        return R.ok();
    }

    // ========== 反馈管理 ==========

    @GetMapping("feedback/page/{page}/{limit}")
    public R getFeedbackPage(@PathVariable Integer page,
                             @PathVariable Integer limit,
                             @RequestParam(required = false) Integer status) {
        Page<HelpFeedback> pageParam = new Page<>(page, limit);
        IPage<HelpFeedback> result = helpFeedbackService.pageQuery(pageParam, status);
        return R.ok().data("records", result.getRecords()).data("total", result.getTotal());
    }

    @PutMapping("feedback/updateStatus")
    public R updateFeedbackStatus(@RequestBody HelpFeedback feedback) {
        helpFeedbackService.updateById(feedback);
        return R.ok();
    }
}
