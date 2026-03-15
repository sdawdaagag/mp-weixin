package com.kitchen.service;

import com.kitchen.common.PageResult;
import com.kitchen.dto.DishCreateDTO;
import com.kitchen.vo.DishVO;

public interface DishService {

    PageResult<DishVO> getDishList(String category, Integer page, Integer size, Integer status);

    DishVO getDishById(Long id);

    Long createDish(DishCreateDTO dto);

    void updateDish(Long id, DishCreateDTO dto);

    void deleteDish(Long id);

    void updateStatus(Long id, Integer status);

    void updateRecommend(Long id, Integer isRecommend);
}
