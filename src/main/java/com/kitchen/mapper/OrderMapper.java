package com.kitchen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kitchen.entity.OrderInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<OrderInfo> {
}
