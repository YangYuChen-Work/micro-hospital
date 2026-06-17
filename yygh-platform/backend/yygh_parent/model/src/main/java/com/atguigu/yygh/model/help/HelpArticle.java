package com.atguigu.yygh.model.help;

import com.atguigu.yygh.model.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("help_article")
public class HelpArticle extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableField("category_id")
    private Long categoryId;

    @TableField("title")
    private String title;

    @TableField("content")
    private String content;

    @TableField("summary")
    private String summary;

    @TableField("sort_order")
    private Integer sortOrder;

    @TableField("view_count")
    private Integer viewCount;

    @TableField("status")
    private Integer status;
}
