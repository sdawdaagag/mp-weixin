package com.kitchen.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("order_detail")
public class OrderDetail {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderId;

    private Long dishId;

    private String dishName;

    private Integer quantity;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
