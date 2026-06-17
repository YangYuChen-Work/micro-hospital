package com.atguigu.yygh.model.help;

import com.atguigu.yygh.model.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("help_feedback")
public class HelpFeedback extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableField("user_id")
    private Long userId;

    @TableField("content")
    private String content;

    @TableField("contact")
    private String contact;

    @TableField("status")
    private Integer status;
}
