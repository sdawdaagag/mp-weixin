package com.kitchen.service;

import com.kitchen.dto.LocationDTO;
import com.kitchen.entity.UserLocation;

public interface UserLocationService {

    void saveLocation(Long userId, LocationDTO dto);

    UserLocation getLatestLocation(Long userId);
}
