package com.aiguide.platform.module.route.model.req;

import com.aiguide.platform.common.model.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RoutePageReq extends PageRequest {
    private String theme;
    private String routeName;
    private Integer routeStatus;
    private String suitableCrowd;
}
