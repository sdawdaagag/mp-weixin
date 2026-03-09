package com.kitchen.vo;

import lombok.Data;

@Data
public class UserVO {

    private Long id;
    private String username;
    private Integer role;
    private String nickname;
    private String avatar;
    private String token;
}
