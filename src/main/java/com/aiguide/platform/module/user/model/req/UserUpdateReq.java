package com.aiguide.platform.module.user.model.req;

import lombok.Data;

@Data
public class UserUpdateReq {
    private String nickname;
    private String avatarUrl;
    private String email;
    private String phone;
}
