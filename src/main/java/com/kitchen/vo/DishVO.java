package com.kitchen.vo;

import lombok.Data;
import java.util.List;

/**
 * 菜品展示VO
 */
@Data
public class DishVO {

    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private List<String> images;
    private String category;
    private String taste;
    private Integer cookTime;
    private Integer status;
}
