package com.finalproject.user.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.finalproject.user.common.to.SocialUserResponse;
import com.finalproject.user.common.utils.Query;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.finalproject.user.entity.UserEntity;
import com.finalproject.user.service.UserService;
import com.finalproject.user.common.utils.PageUtils;
import com.finalproject.user.common.utils.R;



/**
 *
 *
 * @author TB
 * @email tb17074107@gmail.com
 * @date 2022-12-01 20:51:55
 */
@CrossOrigin
@RestController
@RequestMapping("user/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("user:user:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = userService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("user:user:info")
    public R info(@PathVariable("id") Long id){
		UserEntity user = userService.getById(id);

        return R.ok().put("user", user);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    //@RequiresPermissions("user:user:save")
    public R save(@RequestBody UserEntity user){
        if(StringUtils.isNullOrEmpty(user.getAvatar())){
            user.setAvatar("https://ui-avatars.com/api/?name="+user.getUsername()+"");
        }
		userService.save(user);
        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    //@RequiresPermissions("user:user:update")
    public R update(@RequestBody UserEntity user){
		userService.updateById(user);

        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    //@RequiresPermissions("user:user:delete")
    public R delete(@RequestBody Long[] ids){
		userService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    @GetMapping("/search")
    public R searchByUsername(@RequestParam Map<String, Object> params){


        try {
            PageUtils page = userService.queryPageByUsername(params);

            return R.ok().put("page", page);
        }catch (Exception e){
            return R.error("username error");
        }
    }

}
