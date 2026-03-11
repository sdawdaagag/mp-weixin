package com.kitchen.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户位置信息实体类
 * 用于存储用户的精确位置坐标信息
 */
@Data
@TableName("user_location")
public class UserLocation {

    /**
     * 主键ID，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID，关联sys_user表
     */
    private Long userId;

    /**
     * 纬度，使用BigDecimal保证精度
     * 范围: -90 到 90
     */
    private BigDecimal latitude;

    /**
     * 经度，使用BigDecimal保证精度
     * 范围: -180 到 180
     */
    private BigDecimal longitude;

    /**
     * 地址描述，通过逆地理编码获取的具体地址
     */
    private String address;

    /**
     * 创建时间，自动填充
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间，自动填充
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
