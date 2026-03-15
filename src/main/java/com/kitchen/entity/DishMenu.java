package com.kitchen.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 菜品菜单实体类
 */
@Data
@TableName("dish_menu")
public class DishMenu {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 菜品名称
     */
    private String name;

    /**
     * 菜品描述
     */
    private String description;

    /**
     * 单张图片URL（兼容旧数据）
     */
    private String imageUrl;

    /**
     * 多图片URL（JSON数组格式）
     * 例如: ["url1", "url2", "url3"]
     */
    private String images;

    /**
     * 分类
     */
    private String category;

    /**
     * 口味
     */
    private String taste;

    /**
     * 烹饪时间（分钟）
     */
    private Integer cookTime;

    /**
     * 状态：0-下架，1-供应中，2-售罄
     */
    private Integer status;

    /**
     * 是否推荐：0-否，1-是
     */
    private Integer isRecommend;

    /**
     * 排序
     */
    private Integer sortOrder;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
