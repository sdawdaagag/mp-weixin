package com.kitchen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kitchen.entity.DishMenu;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<DishMenu> {
}
