package com.aiguide.platform.module.user.model.req;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class RegisterReq {
    @NotBlank(message = "账号不能为空")
    @Size(min = 3, max = 32, message = "账号长度3-32位")
    private String username;
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 32, message = "密码长度6-32位")
    private String password;
    @NotBlank(message = "昵称不能为空")
    private String nickname;
    private String email;
    private String phone;
}
