package com.aiguide.platform.module.user.model.req;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class LoginReq {
    @NotBlank(message = "账号不能为空")
    private String username;
    @NotBlank(message = "密码不能为空")
    private String password;
}
