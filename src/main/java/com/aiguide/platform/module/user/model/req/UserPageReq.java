package com.aiguide.platform.module.user.model.req;

import com.aiguide.platform.common.model.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserPageReq extends PageRequest {
    private String username;
    private String roleCode;
    private Integer userStatus;
}
