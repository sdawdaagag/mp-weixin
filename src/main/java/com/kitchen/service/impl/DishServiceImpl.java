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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DishServiceImpl implements DishService {

    private final DishMapper dishMapper;
    private final ObjectMapper objectMapper;

    @Override
    public PageResult<DishVO> getDishList(String category, Integer page, Integer size, Integer status) {
        LambdaQueryWrapper<DishMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishMenu::getDeleted, 0);
        
        if (status != null) {
            wrapper.eq(DishMenu::getStatus, status);
        }

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
        dish.setIsRecommend(dto.getIsRecommend() != null ? dto.getIsRecommend() : 0);
        
        // 处理多图片
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            try {
                dish.setImages(objectMapper.writeValueAsString(dto.getImages()));
                // 设置第一张图片为封面
                if (!StringUtils.hasText(dto.getImageUrl()) && !dto.getImages().isEmpty()) {
                    dish.setImageUrl(dto.getImages().get(0));
                }
            } catch (Exception e) {
                log.error("序列化图片列表失败", e);
            }
        }
        
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
        
        if (dto.getIsRecommend() != null) {
            dish.setIsRecommend(dto.getIsRecommend());
        }
        
        // 处理多图片
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            try {
                dish.setImages(objectMapper.writeValueAsString(dto.getImages()));
                // 设置第一张图片为封面
                if (!StringUtils.hasText(dto.getImageUrl()) && !dto.getImages().isEmpty()) {
                    dish.setImageUrl(dto.getImages().get(0));
                }
            } catch (Exception e) {
                log.error("序列化图片列表失败", e);
            }
        }
        
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

    @Override
    public void updateRecommend(Long id, Integer isRecommend) {
        if (isRecommend == null || (isRecommend != 0 && isRecommend != 1)) {
            throw new BusinessException("推荐状态非法");
        }

        DishMenu dish = dishMapper.selectById(id);
        if (dish == null) {
            throw new BusinessException("菜品不存在");
        }
        dish.setIsRecommend(isRecommend);
        dishMapper.updateById(dish);
    }

    /**
     * 将实体转换为VO
     */
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
        vo.setIsRecommend(dish.getIsRecommend());
        
        // 解析多图片JSON
        if (StringUtils.hasText(dish.getImages())) {
            try {
                List<String> images = objectMapper.readValue(dish.getImages(), new TypeReference<List<String>>() {});
                vo.setImages(images);
            } catch (Exception e) {
                log.error("解析图片列表失败", e);
                vo.setImages(Collections.emptyList());
            }
        } else if (StringUtils.hasText(dish.getImageUrl())) {
            // 兼容旧数据，将单张图片转为列表
            vo.setImages(Collections.singletonList(dish.getImageUrl()));
        } else {
            vo.setImages(Collections.emptyList());
        }
        
        return vo;
    }
}
