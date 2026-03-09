package com.kitchen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kitchen.common.PageResult;
import com.kitchen.dto.DishCreateDTO;
import com.kitchen.entity.DishMenu;
import com.kitchen.exception.BusinessException;
import com.kitchen.mapper.DishMapper;
import com.kitchen.service.DishService;
import com.kitchen.vo.DishVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DishServiceImpl implements DishService {

    private final DishMapper dishMapper;

    @Override
    public PageResult<DishVO> getDishList(String category, Integer page, Integer size) {
        LambdaQueryWrapper<DishMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishMenu::getDeleted, 0)
               .in(DishMenu::getStatus, 1, 2);

        if (StringUtils.hasText(category)) {
            wrapper.eq(DishMenu::getCategory, category);
        }

        wrapper.orderByDesc(DishMenu::getSortOrder)
               .orderByDesc(DishMenu::getCreateTime);

        Page<DishMenu> pageResult = dishMapper.selectPage(new Page<>(page, size), wrapper);

        List<DishVO> voList = pageResult.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return new PageResult<>(voList, pageResult.getTotal(), page, size);
    }

    @Override
    public DishVO getDishById(Long id) {
        DishMenu dish = dishMapper.selectById(id);
        if (dish == null) {
            throw new BusinessException("菜品不存在");
        }
        return convertToVO(dish);
    }

    @Override
    public Long createDish(DishCreateDTO dto) {
        DishMenu dish = new DishMenu();
        dish.setName(dto.getName());
        dish.setDescription(dto.getDescription());
        dish.setImageUrl(dto.getImageUrl());
        dish.setCategory(dto.getCategory());
        dish.setTaste(dto.getTaste());
        dish.setCookTime(dto.getCookTime());
        dish.setStatus(0);
        dish.setSortOrder(0);
        dishMapper.insert(dish);
        return dish.getId();
    }

    @Override
    public void updateDish(Long id, DishCreateDTO dto) {
        DishMenu dish = dishMapper.selectById(id);
        if (dish == null) {
            throw new BusinessException("菜品不存在");
        }
        dish.setName(dto.getName());
        dish.setDescription(dto.getDescription());
        dish.setImageUrl(dto.getImageUrl());
        dish.setCategory(dto.getCategory());
        dish.setTaste(dto.getTaste());
        dish.setCookTime(dto.getCookTime());
        dishMapper.updateById(dish);
    }

    @Override
    public void deleteDish(Long id) {
        dishMapper.deleteById(id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        if (status == null || status < 0 || status > 2) {
            throw new BusinessException("菜品状态非法");
        }

        DishMenu dish = dishMapper.selectById(id);
        if (dish == null) {
            throw new BusinessException("菜品不存在");
        }
        dish.setStatus(status);
        dishMapper.updateById(dish);
    }

    private DishVO convertToVO(DishMenu dish) {
        DishVO vo = new DishVO();
        vo.setId(dish.getId());
        vo.setName(dish.getName());
        vo.setDescription(dish.getDescription());
        vo.setImageUrl(dish.getImageUrl());
        vo.setCategory(dish.getCategory());
        vo.setTaste(dish.getTaste());
        vo.setCookTime(dish.getCookTime());
        vo.setStatus(dish.getStatus());
        return vo;
    }
}
