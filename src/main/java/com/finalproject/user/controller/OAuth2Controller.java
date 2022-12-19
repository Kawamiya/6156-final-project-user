package com.finalproject.user.controller;

import com.alibaba.fastjson.JSON;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.finalproject.user.common.to.LoginParam;
import com.finalproject.user.common.to.SocialUser;
import com.finalproject.user.common.to.SocialUserResponse;
import com.finalproject.user.common.utils.HttpUtils;
import com.finalproject.user.common.utils.JWTUtils;
import com.finalproject.user.common.utils.R;
import com.finalproject.user.entity.UserEntity;
import com.finalproject.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate;
import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Transactional
@RestController
@CrossOrigin
@RequestMapping("/")
public class OAuth2Controller {

    private AmazonSNS amazonSNS = null;
    @Autowired
    private UserService userService;

    @Autowired
    RedisTemplate<String,String> redisTemplate;

    @Autowired
    private NotificationMessagingTemplate notificationMessagingTemplate;


    @GetMapping(value = "/oauth2/login")
    public R thirdPartLogin(@RequestParam("code") String code) throws Exception {

        Map<String, String> map = new HashMap<>();
        map.put("redirect_uri","https://d21yfvmibfq3ep.cloudfront.net/auth/google/callback");
        map.put("client_id","20995927639-bp9m91hd1r1km1mr96os6lb30vlo1fpq.apps.googleusercontent.com");
        map.put("grant_type","authorization_code");
        map.put("code",code);
        map.put("client_secret","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");



        HttpResponse response = HttpUtils.doPost("https://oauth2.googleapis.com", "/token", "post", new HashMap<>(), map, new HashMap<>());

        //processing
        if (response.getStatusLine().getStatusCode() == 200) {
            //using code to get access_token
            String json = EntityUtils.toString(response.getEntity());

            SocialUserResponse socialUserResponse = JSON.parseObject(json, SocialUserResponse.class);

            //using access token to get user information

            Map<String, String> userMap = new HashMap<>();
            userMap.put("access_token",socialUserResponse.getAccess_token());
            HttpResponse userInfoResponse = HttpUtils.doGet("https://www.googleapis.com", "/oauth2/v1/userinfo", "get", new HashMap<>(), userMap);
            if(userInfoResponse.getStatusLine().getStatusCode() == 200){
                String userJson = EntityUtils.toString(userInfoResponse.getEntity());
                SocialUser socialUser = JSON.parseObject(userJson, SocialUser.class);


                //1） if it's the first time, register
                UserEntity user = userService.login(socialUser);
                //generate token and save it into redis
                String token = JWTUtils.createToken(user.getId());
                try{
                    redisTemplate.opsForValue().set(token, JSON.toJSONString(user), 1, TimeUnit.DAYS);
                }catch (Exception e){
                    Message<String> message = new GenericMessage<>("redis error");
                    notificationMessagingTemplate.send(message);
                    throw e;
                }

                //return token and userId
                return R.ok().put("token",token).put("user_id",user.getId());
            }else {
                return R.error("login failed，can not get Google User");
            }

        } else {
            Message<String> message = new GenericMessage<>("google login error");
            notificationMessagingTemplate.send(message);

            return R.error("login failed，do not have user authorization");
        }
    }

    @GetMapping(value = "/login")
    public R login(@RequestBody LoginParam loginParam){
        UserEntity user = userService.getOne(new QueryWrapper<UserEntity>().eq("username", loginParam.getUsername()).eq("password", loginParam.getPassword()));
        if(user!=null){
            String token = JWTUtils.createToken(user.getId());
            redisTemplate.opsForValue().set(token, JSON.toJSONString(user), 1, TimeUnit.DAYS);
            return R.ok().put("token",token).put("user_id",user.getId());
        }else {
            return R.error("do not have this user");
        }
    }


    @GetMapping("/logout")
    public R logout(@RequestHeader("Authorization") String token){
        redisTemplate.delete(token);
        return R.ok();
    }

    @PostMapping("/register")
    public R register(@RequestBody LoginParam loginParam) throws Exception {

        String account = loginParam.getUsername();
        String password = loginParam.getPassword();
        String nickname = loginParam.getNickname();
        if (StringUtils.isBlank(account) || StringUtils.isBlank(password) || StringUtils.isBlank(nickname)){
            return R.error("invalid user");
        }
        UserEntity userEntity =userService.getOne(new QueryWrapper<UserEntity>().eq("username",account));

        if (userEntity!=null){
            return R.error("user has already existed");
        }

        Map<String, String> body = new HashMap<>();
        body.put("test",loginParam.getNickname());

        Map<String, String> header = new HashMap<>();
        header.put("X-APISpace-Token","dnd7z1a4kch0cek3grfdm2ty4kcwrcra");
        header.put("Authorization-Type","apikey");
        header.put("Content-Type","application/x-www-form-urlencoded");

        HttpResponse response = HttpUtils.doPost(
                "https://eolink.o.apispace.com",
                "/text-filters/api/v1/forward/text_filter/",
                "post",
                new HashMap<>(),
                header,
                body);
        class SensitiveWordResponse{
            String code;
            String message;
            String data;
            String use_time;
        };
        if (response.getStatusLine().getStatusCode() == 200) {
            String testJson = EntityUtils.toString(response.getEntity());
            SensitiveWordResponse sensitiveWordResponse = JSON.parseObject(testJson, SensitiveWordResponse.class);
            userEntity.setNickname(nickname);
        }



//        Request request = new Request.Builder()
//                .url("https://eolink.o.apispace.com/text-filters/api/v1/forward/text_filter/")
//                .method("POST",body)
//                .addHeader("X-APISpace-Token","dnd7z1a4kch0cek3grfdm2ty4kcwrcra")
//                .addHeader("Authorization-Type","apikey")
//                .addHeader("Content-Type","application/x-www-form-urlencoded")
//                .build();
//
//        Response response = client.newCall(request).execute();


        userEntity = new UserEntity();
        userEntity.setUsername(account);
        userEntity.setPassword(password);
//        userEntity.setNickname(nickname);
        userEntity.setCreateTime(new Date());
        userEntity.setLastLogin(new Date());
        userEntity.setAvatar("https://ui-avatars.com/api/?name="+userEntity.getNickname()+"");
        userService.save(userEntity);

        String token = JWTUtils.createToken(userEntity.getId());
        redisTemplate.opsForValue().set(token, JSON.toJSONString(userEntity), 1,TimeUnit.DAYS);

        return R.ok().setData(token);
    }





}
