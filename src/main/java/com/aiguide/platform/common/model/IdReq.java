package com.aiguide.platform.common.model;

import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class IdReq {
    @NotNull(message = "ID不能为空")
    private Long id;
}
