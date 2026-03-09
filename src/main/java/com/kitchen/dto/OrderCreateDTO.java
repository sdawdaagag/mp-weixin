package com.kitchen.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class OrderCreateDTO {

    @NotEmpty(message = "订单至少包含一个菜品")
    @Valid
    private List<OrderItemDTO> items;
    private String remark;

    @Data
    public static class OrderItemDTO {
        @NotNull(message = "菜品ID不能为空")
        private Long dishId;

        @NotNull(message = "菜品数量不能为空")
        @Min(value = 1, message = "菜品数量至少为1")
        @Max(value = 5, message = "单个菜品最多5份")
        private Integer quantity;
    }
}
