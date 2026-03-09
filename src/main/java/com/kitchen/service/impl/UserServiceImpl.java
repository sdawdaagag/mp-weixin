package com.kitchen.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kitchen.dto.LoginDTO;
import com.kitchen.entity.SysUser;
import com.kitchen.exception.BusinessException;
import com.kitchen.mapper.UserMapper;
import com.kitchen.service.UserService;
import com.kitchen.util.JwtUtil;
import com.kitchen.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;

    @Override
    public UserVO login(String username, String password) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        SysUser user = userMapper.selectOne(wrapper);

        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new BusinessException("密码错误");
        }

        return convertToVO(user);
    }

    @Override
    public UserVO getUserById(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return convertToVO(user);
    }

    @Override
    public UserVO getCurrentUser() {
        Long userId = jwtUtil.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException("请先登录");
        }
        return getUserById(userId);
    }

    private UserVO convertToVO(SysUser user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRole(user.getRole());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setToken(jwtUtil.generateToken(user.getId(), user.getUsername()));
        return vo;
    }
}
