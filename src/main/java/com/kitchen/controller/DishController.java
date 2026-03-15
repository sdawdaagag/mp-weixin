package com.kitchen.controller;

import com.kitchen.common.PageResult;
import com.kitchen.common.Result;
import com.kitchen.dto.DishCreateDTO;
import com.kitchen.dto.StatusUpdateDTO;
import com.kitchen.exception.BusinessException;
import com.kitchen.service.DishService;
import com.kitchen.service.UserService;
import com.kitchen.vo.DishVO;
import com.kitchen.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/dish")
@RequiredArgsConstructor
@Validated
public class DishController {

    private final DishService dishService;
    private final UserService userService;

    @GetMapping("/list")
    public Result<PageResult<DishVO>> list(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        PageResult<DishVO> result = dishService.getDishList(category, page, size, status);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<DishVO> getById(@PathVariable Long id) {
        DishVO dish = dishService.getDishById(id);
        return Result.success(dish);
    }

    @PostMapping
    public Result<Long> create(@Valid @RequestBody DishCreateDTO dto) {
        assertAdmin();
        Long id = dishService.createDish(dto);
        return Result.success(id);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody DishCreateDTO dto) {
        assertAdmin();
        dishService.updateDish(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        assertAdmin();
        dishService.deleteDish(id);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdateDTO dto) {
        assertAdmin();
        dishService.updateStatus(id, dto.getStatus());
        return Result.success();
    }

    @PutMapping("/{id}/recommend")
    public Result<Void> updateRecommend(@PathVariable Long id, @RequestBody RecommendDTO dto) {
        assertAdmin();
        dishService.updateRecommend(id, dto.getIsRecommend());
        return Result.success();
    }

    private void assertAdmin() {
        UserVO currentUser = userService.getCurrentUser();
        if (currentUser.getRole() == null || currentUser.getRole() != 1) {
            throw new BusinessException("仅大厨可操作");
        }
    }
}

@lombok.Data
class RecommendDTO {
    private Integer isRecommend;
}
