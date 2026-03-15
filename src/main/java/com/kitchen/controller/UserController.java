package com.kitchen.controller;

import com.kitchen.common.Result;
import com.kitchen.dto.LocationDTO;
import com.kitchen.dto.LoginDTO;
import com.kitchen.dto.WxLoginDTO;
import com.kitchen.entity.UserLocation;
import com.kitchen.exception.BusinessException;
import com.kitchen.service.UserService;
import com.kitchen.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 用户控制器
 * 处理用户登录、微信登录、位置上报、用户管理等接口
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    /**
     * 账号密码登录接口
     * 主要用于管理员登录
     * 
     * @param dto 登录信息，包含用户名和密码
     * @return 登录成功后的用户信息（含token）
     * 1
     */
    @PostMapping("/login")
    public Result<UserVO> login(@Valid @RequestBody LoginDTO dto) {
        UserVO user = userService.login(dto.getUsername(), dto.getPassword());
        return Result.success(user);
    }

    /**
     * 微信登录接口
     * 用户通过微信一键登录，首次登录会自动注册
     * 
     * 注意: 当前实现使用前端传来的code作为openid，生产环境需要:
     * 1. 配置微信小程序AppID和AppSecret
     * 2. 调用微信API code2Session获取真实openid
     * 
     * @param dto 微信登录信息，包含code、昵称、头像等
     * @return 登录成功后的用户信息（含token）
     */
    @PostMapping("/wx-login")
    public Result<UserVO> wxLogin(@Valid @RequestBody WxLoginDTO dto) {
        UserVO user = userService.wxLogin(dto);
        return Result.success(user);
    }

    /**
     * 获取当前登录用户信息
     * 
     * @return 当前用户信息1
     */
    @GetMapping("/info")
    public Result<UserVO> getCurrentUser() {
        UserVO user = userService.getCurrentUser();
        return Result.success(user);
    }

    /**
     * 上报用户位置信息
     * 用户登录后上报其精确位置坐标
     * 
     * @param dto 位置信息，包含经纬度和地址
     * @return 成功响应
     */
    @PostMapping("/location")
    public Result<Void> updateLocation(@Valid @RequestBody LocationDTO dto) {
        UserVO currentUser = userService.getCurrentUser();
        userService.updateLocation(currentUser.getId(), dto);
        return Result.success();
    }

    /**
     * 获取指定用户的位置信息（管理员专用）
     * 
     * @param userId 用户ID
     * @return 用户最新位置信息
     */
    @GetMapping("/location/{userId}")
    public Result<UserLocation> getUserLocation(@PathVariable Long userId) {
        assertAdmin();
        UserLocation location = userService.getUserLocation(userId);
        return Result.success(location);
    }

    /**
     * 获取用户列表（管理员专用）
     * 
     * @return 用户列表
     */
    @GetMapping("/list")
    public Result<List<UserVO>> getUserList() {
        assertAdmin();
        List<UserVO> users = userService.getUserList();
        return Result.success(users);
    }

    /**
     * 删除用户（管理员专用）
     * 不能删除管理员账号
     * 
     * @param id 用户ID
     * @return 成功响应
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        assertAdmin();
        userService.deleteUser(id);
        return Result.success();
    }

    /**
     * 设置用户家庭成员角色（管理员专用）
     * 可设置: 爸爸、妈妈、老婆
     * 
     * @param id 用户ID
     * @param
     * @return 成功响应1
     */
    @PutMapping("/{id}/family-role")
    public Result<Void> setFamilyRole(@PathVariable Long id, @RequestBody FamilyRoleRequest request) {
        assertAdmin();
        userService.setFamilyRole(id, request.getFamilyRole());
        return Result.success();
    }

    /**
     * 家庭成员角色请求DTO
     */
    @lombok.Data
    public static class FamilyRoleRequest {
        private String familyRole;
    }

    /**
     * 校验当前用户是否为管理员
     * 非管理员会抛出BusinessException
     * 
     * @throws BusinessException 非管理员时抛出
     */
    private void assertAdmin() {
        UserVO currentUser = userService.getCurrentUser();
        if (currentUser.getRole() == null || currentUser.getRole() != 1) {
            throw new BusinessException("仅管理员可操作");
        }
    }
}
