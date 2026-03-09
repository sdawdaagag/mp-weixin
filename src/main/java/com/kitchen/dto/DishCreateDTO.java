package com.kitchen.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
public class DishCreateDTO {

    @NotBlank(message = "菜品名称不能为空")
    private String name;

    private String description;

    private String imageUrl;

    @NotBlank(message = "菜品分类不能为空")
    private String category;

    private String taste;

    @Min(value = 1, message = "烹饪时间必须大于0")
    private Integer cookTime;
}
