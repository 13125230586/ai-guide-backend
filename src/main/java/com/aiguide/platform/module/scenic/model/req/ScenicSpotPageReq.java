package com.aiguide.platform.module.scenic.model.req;

import com.aiguide.platform.common.model.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ScenicSpotPageReq extends PageRequest {
    private Long categoryId;
    private String city;
    private String spotName;
    private Integer spotStatus;
    private String keyword;
}
