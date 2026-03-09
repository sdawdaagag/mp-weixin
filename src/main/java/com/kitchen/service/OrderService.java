package com.kitchen.service;

import com.kitchen.common.PageResult;
import com.kitchen.dto.OrderCreateDTO;
import com.kitchen.vo.OrderVO;

public interface OrderService {

    Long createOrder(Long userId, OrderCreateDTO dto);

    PageResult<OrderVO> getOrderList(Integer status, Integer page, Integer size);

    PageResult<OrderVO> getMyOrders(Long userId, Integer page, Integer size);

    OrderVO getOrderById(Long id);

    void updateStatus(Long id, Integer status);
}
