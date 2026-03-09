package com.kitchen.service;

import com.kitchen.dto.LoginDTO;
import com.kitchen.vo.UserVO;

public interface UserService {

    UserVO login(String username, String password);

    UserVO getUserById(Long id);

    UserVO getCurrentUser();
}
