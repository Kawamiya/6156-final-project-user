package com.finalproject.user.dao;

import com.finalproject.user.entity.UserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 
 * 
 * @author TB
 * @email tb17074107@gmail.com
 * @date 2022-12-01 20:51:55
 */
@Mapper
public interface UserDao extends BaseMapper<UserEntity> {
	
}
