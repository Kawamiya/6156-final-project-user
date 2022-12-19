package com.finalproject.user.common.to;

import lombok.Data;

@Data
public class LoginParam {

    private String username;

    private String password;

    private String nickname;

    private String avatar = "/static/img/logo.b3a48c0.png";
}
