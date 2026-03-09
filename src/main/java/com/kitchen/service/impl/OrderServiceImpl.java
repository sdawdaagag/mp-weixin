package com.kitchen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kitchen.common.PageResult;
import com.kitchen.dto.OrderCreateDTO;
import com.kitchen.entity.DishMenu;
import com.kitchen.entity.OrderDetail;
import com.kitchen.entity.OrderInfo;
import com.kitchen.entity.SysUser;
import com.kitchen.exception.BusinessException;
import com.kitchen.mapper.DishMapper;
import com.kitchen.mapper.OrderDetailMapper;
import com.kitchen.mapper.OrderMapper;
import com.kitchen.mapper.UserMapper;
import com.kitchen.service.OrderService;
import com.kitchen.util.OrderNoGenerator;
import com.kitchen.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final DishMapper dishMapper;
    private final UserMapper userMapper;
    private final OrderNoGenerator orderNoGenerator;

    @Override
    @Transactional
    public Long createOrder(Long userId, OrderCreateDTO dto) {
        String orderNo = orderNoGenerator.generate();

        int totalCount = dto.getItems().stream()
                .mapToInt(OrderCreateDTO.OrderItemDTO::getQuantity)
                .sum();

        OrderInfo order = new OrderInfo();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setStatus(0);
        order.setRemark(dto.getRemark());
        order.setTotalCount(totalCount);
        orderMapper.insert(order);

        for (OrderCreateDTO.OrderItemDTO item : dto.getItems()) {
            DishMenu dish = dishMapper.selectById(item.getDishId());
            if (dish == null) {
                throw new BusinessException("菜品不存在: " + item.getDishId());
            }

            OrderDetail detail = new OrderDetail();
            detail.setOrderId(order.getId());
            detail.setDishId(item.getDishId());
            detail.setDishName(dish.getName());
            detail.setQuantity(item.getQuantity());
            orderDetailMapper.insert(detail);
        }

        return order.getId();
    }

    @Override
    public PageResult<OrderVO> getOrderList(Integer status, Integer page, Integer size) {
        LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(OrderInfo::getStatus, status);
        }
        wrapper.orderByAsc(OrderInfo::getStatus)
               .orderByDesc(OrderInfo::getCreateTime);

        Page<OrderInfo> pageResult = orderMapper.selectPage(new Page<>(page, size), wrapper);
        List<OrderVO> voList = convertToVOList(pageResult.getRecords());

        return new PageResult<>(voList, pageResult.getTotal(), page, size);
    }

    @Override
    public PageResult<OrderVO> getMyOrders(Long userId, Integer page, Integer size) {
        LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderInfo::getUserId, userId)
               .orderByDesc(OrderInfo::getCreateTime);

        Page<OrderInfo> pageResult = orderMapper.selectPage(new Page<>(page, size), wrapper);
        List<OrderVO> voList = convertToVOList(pageResult.getRecords());

        return new PageResult<>(voList, pageResult.getTotal(), page, size);
    }

    @Override
    public OrderVO getOrderById(Long id) {
        OrderInfo order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        return convertToVO(order);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        if (status == null || status < 0 || status > 3) {
            throw new BusinessException("订单状态非法");
        }

        OrderInfo order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        order.setStatus(status);
        orderMapper.updateById(order);
    }

    private List<OrderVO> convertToVOList(List<OrderInfo> orders) {
        if (orders == null || orders.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> userIds = orders.stream().map(OrderInfo::getUserId).collect(Collectors.toSet());
        Map<Long, SysUser> userMap = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(SysUser::getId, Function.identity()));

        List<Long> orderIds = orders.stream().map(OrderInfo::getId).collect(Collectors.toList());
        LambdaQueryWrapper<OrderDetail> detailWrapper = new LambdaQueryWrapper<>();
        detailWrapper.in(OrderDetail::getOrderId, orderIds);

        Map<Long, List<OrderDetail>> detailMap = orderDetailMapper.selectList(detailWrapper).stream()
                .collect(Collectors.groupingBy(OrderDetail::getOrderId));

        List<OrderVO> result = new ArrayList<>(orders.size());
        for (OrderInfo order : orders) {
            result.add(convertToVO(order, userMap.get(order.getUserId()), detailMap.get(order.getId())));
        }
        return result;
    }

    private OrderVO convertToVO(OrderInfo order) {
        SysUser user = userMapper.selectById(order.getUserId());
        LambdaQueryWrapper<OrderDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderDetail::getOrderId, order.getId());
        List<OrderDetail> details = orderDetailMapper.selectList(wrapper);
        return convertToVO(order, user, details);
    }

    private OrderVO convertToVO(OrderInfo order, SysUser user, List<OrderDetail> details) {
        OrderVO vo = new OrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setUserId(order.getUserId());
        vo.setStatus(order.getStatus());
        vo.setRemark(order.getRemark());
        vo.setTotalCount(order.getTotalCount());
        vo.setCreateTime(order.getCreateTime());

        if (user != null) {
            vo.setNickname(user.getNickname());
        }

        List<OrderVO.OrderDetailVO> detailVOs = (details == null ? Collections.<OrderDetail>emptyList() : details).stream()
                .map(d -> {
                    OrderVO.OrderDetailVO detailVO = new OrderVO.OrderDetailVO();
                    detailVO.setDishId(d.getDishId());
                    detailVO.setDishName(d.getDishName());
                    detailVO.setQuantity(d.getQuantity());
                    return detailVO;
                })
                .collect(Collectors.toList());
        vo.setDetails(detailVOs);

        return vo;
    }
}
