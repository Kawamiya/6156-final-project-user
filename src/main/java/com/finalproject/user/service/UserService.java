package com.finalproject.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.finalproject.user.common.to.SocialUser;
import com.finalproject.user.common.utils.PageUtils;
import com.finalproject.user.entity.UserEntity;

import java.util.Map;

/**
 *
 *
 * @author TB
 * @email tb17074107@gmail.com
 * @date 2022-12-01 20:51:55
 */
public interface UserService extends IService<UserEntity> {

    PageUtils queryPage(Map<String, Object> params);
    PageUtils queryPageByUsername(Map<String, Object> params);

    UserEntity login(SocialUser socialUser);
}

