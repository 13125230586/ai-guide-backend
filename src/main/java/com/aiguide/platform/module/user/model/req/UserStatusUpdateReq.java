package com.aiguide.platform.module.user.model.req;

import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class UserStatusUpdateReq {
    @NotNull
    private Long id;
    @NotNull
    private Integer userStatus;
}
