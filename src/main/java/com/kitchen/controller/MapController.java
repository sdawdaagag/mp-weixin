package com.kitchen.controller;

import com.kitchen.common.Result;
import com.kitchen.util.GaodeMapUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 地图控制器
 * 提供位置相关接口
 */
@Slf4j
@RestController
@RequestMapping("/api/map")
@RequiredArgsConstructor
public class MapController {

    private final GaodeMapUtil gaodeMapUtil;

    /**
     * 根据经纬度获取地址
     * 
     * @param longitude 经度
     * @param latitude 纬度
     * @return 地址信息
     */
    @GetMapping("/geocode")
    public Result<Map<String, Object>> geocode(
            @RequestParam Double longitude,
            @RequestParam Double latitude) {
        
        log.info("获取地址: longitude={}, latitude={}", longitude, latitude);
        
        String address = gaodeMapUtil.getAddressByLocation(longitude, latitude);
        GaodeMapUtil.AddressInfo detailedAddress = gaodeMapUtil.getDetailedAddress(longitude, latitude);
        
        Map<String, Object> result = new HashMap<>();
        result.put("address", address);
        
        if (detailedAddress != null) {
            result.put("province", detailedAddress.getProvince());
            result.put("city", detailedAddress.getCity());
            result.put("district", detailedAddress.getDistrict());
            result.put("street", detailedAddress.getStreet());
            result.put("streetNumber", detailedAddress.getStreetNumber());
            result.put("formattedAddress", detailedAddress.getFormattedAddress());
        }
        
        return Result.success(result);
    }
}
