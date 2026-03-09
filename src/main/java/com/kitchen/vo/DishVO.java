package com.kitchen.vo;

import lombok.Data;

@Data
public class DishVO {

    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private String category;
    private String taste;
    private Integer cookTime;
    private Integer status;
}
