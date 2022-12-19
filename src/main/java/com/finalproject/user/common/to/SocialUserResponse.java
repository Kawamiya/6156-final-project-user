package com.finalproject.user.common.to;

import lombok.Data;

@Data
public class SocialUserResponse {

    private String access_token;

    private long expires_in;

    private String refresh_token;

    private String scope;

    private String token_type;

    private String id_token;

}
