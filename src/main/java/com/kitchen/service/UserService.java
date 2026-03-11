package com.kitchen.service;

import com.kitchen.dto.LocationDTO;
import com.kitchen.dto.LoginDTO;
import com.kitchen.dto.WxLoginDTO;
import com.kitchen.entity.UserLocation;
import com.kitchen.vo.UserVO;

import java.util.List;

public interface UserService {

    UserVO login(String username, String password);

    UserVO wxLogin(WxLoginDTO dto);

    UserVO getUserById(Long id);

    UserVO getCurrentUser();

    void updateLocation(Long userId, LocationDTO dto);

    UserLocation getUserLocation(Long userId);

    List<UserVO> getUserList();

    void deleteUser(Long id);

    void setFamilyRole(Long id, String familyRole);
}
