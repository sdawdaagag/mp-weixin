package com.kitchen.controller;

import com.kitchen.common.Result;
import com.kitchen.dto.LoginDTO;
import com.kitchen.service.UserService;
import com.kitchen.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public Result<UserVO> login(@Valid @RequestBody LoginDTO dto) {
        UserVO user = userService.login(dto.getUsername(), dto.getPassword());
        return Result.success(user);
    }

    @GetMapping("/info")
    public Result<UserVO> getCurrentUser() {
        UserVO user = userService.getCurrentUser();
        return Result.success(user);
    }
}
