package com.kitchen.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("dish_menu")
public class DishMenu {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String description;

    private String imageUrl;

    private String category;

    private String taste;

    private Integer cookTime;

    private Integer status;

    private Integer sortOrder;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
