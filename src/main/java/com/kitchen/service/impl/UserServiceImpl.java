package com.kitchen.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kitchen.dto.LocationDTO;
import com.kitchen.dto.LoginDTO;
import com.kitchen.dto.WxLoginDTO;
import com.kitchen.entity.SysUser;
import com.kitchen.entity.UserLocation;
import com.kitchen.exception.BusinessException;
import com.kitchen.mapper.UserMapper;
import com.kitchen.service.UserLocationService;
import com.kitchen.service.UserService;
import com.kitchen.util.JwtUtil;
import com.kitchen.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 * 处理用户登录、注册、位置管理、用户管理等核心业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final UserLocationService userLocationService;

    /**
     * 账号密码登录
     * 根据用户名查询用户，校验密码
     * 支持BCrypt加密密码和明文密码（兼容旧数据）
     * 
     * @param username 用户名
     * @param password 密码（明文）
     * @return 登录成功后的用户信息
     * @throws BusinessException 用户不存在或密码错误
     */
    @Override
    public UserVO login(String username, String password) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        SysUser user = userMapper.selectOne(wrapper);

        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        String storedPassword = user.getPassword();
        boolean passwordMatch = false;

        // 1. 先尝试BCrypt校验（新密码格式）
        if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$")) {
            passwordMatch = BCrypt.checkpw(password, storedPassword);
        } else {
            // 2. 兼容明文密码（旧数据）
            passwordMatch = password.equals(storedPassword);
            
            // 3. 如果明文密码匹配，自动升级为BCrypt加密
            if (passwordMatch) {
                user.setPassword(BCrypt.hashpw(password));
                userMapper.updateById(user);
                log.info("用户密码已自动升级为BCrypt加密: username={}", username);
            }
        }

        if (!passwordMatch) {
            throw new BusinessException("密码错误");
        }

        return convertToVO(user);
    }

    /**
     * 微信登录
     * 根据openid查询用户，首次登录自动注册
     * 
     * 注意: 当前实现直接使用dto.getCode()作为openid
     * 生产环境需要调用微信API获取真实openid:
     * GET https://api.weixin.qq.com/sns/jscode2session?
     *     appid=APPID&secret=SECRET&js_code=CODE&grant_type=authorization_code
     * 
     * @param dto 微信登录信息
     * @return 登录成功后的用户信息
     */
    @Override
    public UserVO wxLogin(WxLoginDTO dto) {
        // TODO: 生产环境需要调用微信API获取真实openid
        // 当前使用前端传来的code作为openid，仅用于开发测试
        String openid = dto.getCode();
        
        // 根据openid查询用户
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getOpenid, openid);
        SysUser user = userMapper.selectOne(wrapper);

        if (user == null) {
            // 首次登录，自动注册新用户
            user = new SysUser();
            user.setOpenid(openid);
            user.setNickname(dto.getNickname() != null ? dto.getNickname() : "微信用户");
            user.setAvatar(dto.getAvatarUrl());
            user.setRole(0); // 普通用户角色
            user.setUsername("wx_" + System.currentTimeMillis()); // 生成唯一用户名
            user.setPassword(BCrypt.hashpw("123456")); // 设置默认密码
            userMapper.insert(user);
            log.info("微信登录创建新用户: openid={}, nickname={}", openid, user.getNickname());
        } else {
            // 已注册用户，更新昵称和头像
            if (dto.getNickname() != null) {
                user.setNickname(dto.getNickname());
            }
            if (dto.getAvatarUrl() != null) {
                user.setAvatar(dto.getAvatarUrl());
            }
            userMapper.updateById(user);
        }

        return convertToVO(user);
    }

    /**
     * 根据ID获取用户信息
     * 
     * @param id 用户ID
     * @return 用户信息
     * @throws BusinessException 用户不存在
     */
    @Override
    public UserVO getUserById(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return convertToVO(user);
    }

    /**
     * 获取当前登录用户
     * 从JWT token中解析用户ID，查询用户信息
     * 
     * @return 当前用户信息
     * @throws BusinessException 未登录
     */
    @Override
    public UserVO getCurrentUser() {
        Long userId = jwtUtil.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException("请先登录");
        }
        return getUserById(userId);
    }

    /**
     * 更新用户位置信息
     * 
     * @param userId 用户ID
     * @param dto 位置信息
     */
    @Override
    public void updateLocation(Long userId, LocationDTO dto) {
        log.info("更新用户位置: userId={}, lat={}, lng={}", userId, dto.getLatitude(), dto.getLongitude());
        userLocationService.saveLocation(userId, dto);
    }

    /**
     * 获取用户最新位置
     * 
     * @param userId 用户ID
     * @return 最新位置信息
     */
    @Override
    public UserLocation getUserLocation(Long userId) {
        return userLocationService.getLatestLocation(userId);
    }

    /**
     * 获取用户列表
     * 查询所有未删除的用户
     * 
     * @return 用户列表
     */
    @Override
    public List<UserVO> getUserList() {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getDeleted, 0)
               .orderByDesc(SysUser::getCreateTime);
        List<SysUser> users = userMapper.selectList(wrapper);
        return users.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    /**
     * 删除用户（逻辑删除）
     * 不能删除管理员账号
     * 
     * @param id 用户ID
     * @throws BusinessException 用户不存在或为管理员
     */
    @Override
    public void deleteUser(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (user.getRole() != null && user.getRole() == 1) {
            throw new BusinessException("不能删除管理员账号");
        }
        userMapper.deleteById(id);
    }

    /**
     * 设置用户家庭成员角色
     * 
     * @param id 用户ID
     * @param familyRole 家庭成员角色（爸爸/妈妈/老婆）
     * @throws BusinessException 用户不存在
     */
    @Override
    public void setFamilyRole(Long id, String familyRole) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setFamilyRole(familyRole);
        userMapper.updateById(user);
    }

    /**
     * 将实体转换为VO
     * 同时生成JWT token
     * 
     * @param user 用户实体
     * @return 用户VO
     */
    private UserVO convertToVO(SysUser user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRole(user.getRole());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setOpenid(user.getOpenid());
        vo.setFamilyRole(user.getFamilyRole());
        vo.setToken(jwtUtil.generateToken(user.getId(), user.getUsername()));
        return vo;
    }
}
