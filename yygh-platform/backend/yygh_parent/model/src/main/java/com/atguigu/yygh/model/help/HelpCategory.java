package com.atguigu.yygh.model.help;

import com.atguigu.yygh.model.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("help_category")
public class HelpCategory extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableField("name")
    private String name;

    @TableField("sort_order")
    private Integer sortOrder;

    @TableField("status")
    private Integer status;
}
