package com.aiguide.platform.module.scenic.model.req;

import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class CategoryStatusUpdateReq {
    @NotNull
    private Long id;
    @NotNull
    private Integer categoryStatus;
}
