package com.finalproject.user.service.impl;

import com.finalproject.user.common.to.SocialUser;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.finalproject.user.common.utils.PageUtils;
import com.finalproject.user.common.utils.Query;

import com.finalproject.user.dao.UserDao;
import com.finalproject.user.entity.UserEntity;
import com.finalproject.user.service.UserService;


@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserDao, UserEntity> implements UserService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<UserEntity> page = this.page(
                new Query<UserEntity>().getPage(params),
                new QueryWrapper<UserEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageByUsername(Map<String, Object> params) {
        String username="";
        if(params.containsKey("username")){
            username = String.valueOf(params.get("username"));
            IPage<UserEntity> page = this.page(
                    new Query<UserEntity>().getPage(params),
                    new QueryWrapper<UserEntity>().like("username",username)
            );

            return new PageUtils(page);
        } else {


            return null;
        }


    }

    @Override
    public UserEntity login(SocialUser socialUser) {

        //具有登录和注册逻辑
        String uid = socialUser.getId();

        //1、判断当前社交用户是否已经登录过系统
        UserEntity userEntity = this.baseMapper.selectOne(new QueryWrapper<UserEntity>().eq("social_uuid", uid));

        if (userEntity != null) {
            //这个用户已经注册过
            //更新用户的最后登录时间
            userEntity.setLastLogin(new Date());
            this.baseMapper.updateById(userEntity);

            return userEntity;
        } else {
            //2、没有查到当前社交用户对应的记录我们就需要注册一个
            UserEntity register = new UserEntity();
            //3、将社交用户的信息设置为默认账户信息
            //register.setCreateTime(new Date());
            //register.setLastLogin(new Date());

            register.setAvatar(socialUser.getPicture());
            register.setEmail(socialUser.getEmail());
            register.setNickname(socialUser.getName());
            register.setSocialUuid(uid);

            register.setUsername(socialUser.getEmail());
            //baseMapper.insert(register);
            baseMapper.insert(register);
            return register;
        }

    }

}
