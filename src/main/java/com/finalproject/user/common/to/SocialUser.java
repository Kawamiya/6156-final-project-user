package com.finalproject.user.common.to;

import lombok.Data;

@Data
public class SocialUser {
    private String id;

    private String email;

    private String name;

    private String given_name;

    private String family_name;

    private String picture;

    private String locale;

    private String hd;
}
