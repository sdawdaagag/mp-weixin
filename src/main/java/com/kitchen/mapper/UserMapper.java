package com.kitchen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kitchen.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<SysUser> {
}
