package com.kitchen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kitchen.dto.LocationDTO;
import com.kitchen.entity.UserLocation;
import com.kitchen.mapper.UserLocationMapper;
import com.kitchen.service.UserLocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 用户位置服务实现类
 * 负责用户位置信息的存储和查询
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserLocationServiceImpl implements UserLocationService {

    private final UserLocationMapper userLocationMapper;

    /**
     * 保存用户位置信息
     * 将用户的位置坐标和地址信息存储到数据库
     * 
     * @param userId 用户ID
     * @param dto 位置信息DTO，包含经纬度和地址
     */
    @Override
    public void saveLocation(Long userId, LocationDTO dto) {
        log.info("保存用户位置: userId={}, latitude={}, longitude={}, address={}", 
                userId, dto.getLatitude(), dto.getLongitude(), dto.getAddress());
        
        UserLocation location = new UserLocation();
        location.setUserId(userId);
        location.setLatitude(dto.getLatitude());
        location.setLongitude(dto.getLongitude());
        location.setAddress(dto.getAddress());
        userLocationMapper.insert(location);
    }

    /**
     * 获取用户最新的位置信息
     * 根据用户ID查询最近一次上报的位置
     * 
     * @param userId 用户ID
     * @return 用户最新的位置信息，如果没有则返回null
     */
    @Override
    public UserLocation getLatestLocation(Long userId) {
        LambdaQueryWrapper<UserLocation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserLocation::getUserId, userId)
               .orderByDesc(UserLocation::getCreateTime)
               .last("LIMIT 1");
        return userLocationMapper.selectOne(wrapper);
    }
}
