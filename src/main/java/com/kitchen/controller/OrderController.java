package com.kitchen.controller;

import com.kitchen.common.PageResult;
import com.kitchen.common.Result;
import com.kitchen.dto.OrderCreateDTO;
import com.kitchen.dto.StatusUpdateDTO;
import com.kitchen.exception.BusinessException;
import com.kitchen.service.OrderService;
import com.kitchen.service.UserService;
import com.kitchen.util.JwtUtil;
import com.kitchen.vo.OrderVO;
import com.kitchen.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Validated
public class OrderController {

    private final OrderService orderService;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    @PostMapping
    public Result<Long> create(@Valid @RequestBody OrderCreateDTO dto) {
        Long userId = getCurrentUserIdOrThrow();
        Long orderId = orderService.createOrder(userId, dto);
        return Result.success(orderId);
    }

    @GetMapping("/list")
    public Result<PageResult<OrderVO>> list(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        assertAdmin();
        PageResult<OrderVO> result = orderService.getOrderList(status, page, size);
        return Result.success(result);
    }

    @GetMapping("/my")
    public Result<PageResult<OrderVO>> myOrders(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = getCurrentUserIdOrThrow();
        PageResult<OrderVO> result = orderService.getMyOrders(userId, page, size);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<OrderVO> getById(@PathVariable Long id) {
        Long userId = getCurrentUserIdOrThrow();
        UserVO currentUser = userService.getCurrentUser();

        OrderVO order = orderService.getOrderById(id);
        boolean isAdmin = currentUser.getRole() != null && currentUser.getRole() == 1;
        if (!isAdmin && !userId.equals(order.getUserId())) {
            throw new BusinessException("无权查看该订单");
        }
        return Result.success(order);
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdateDTO dto) {
        assertAdmin();
        orderService.updateStatus(id, dto.getStatus());
        return Result.success();
    }

    private Long getCurrentUserIdOrThrow() {
        Long userId = jwtUtil.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException("请先登录");
        }
        return userId;
    }

    private void assertAdmin() {
        UserVO currentUser = userService.getCurrentUser();
        if (currentUser.getRole() == null || currentUser.getRole() != 1) {
            throw new BusinessException("仅大厨可操作");
        }
    }
}
