package com.aiguide.platform.module.user.model.vo;

import lombok.Data;
import java.util.Date;

@Data
public class UserVO {
    private Long id;
    private String username;
    private String nickname;
    private String avatarUrl;
    private String roleCode;
    private Integer userStatus;
    private String email;
    private String phone;
    private Date lastLoginTime;
    private Date createTime;
}
