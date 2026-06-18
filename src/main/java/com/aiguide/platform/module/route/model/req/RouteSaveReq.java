package com.aiguide.platform.module.route.model.req;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class RouteSaveReq {
    private Long id;
    @NotBlank(message = "路线名称不能为空")
    private String routeName;
    private String theme;
    private String coverUrl;
    private String summary;
    private String description;
    private String suggestDuration;
    private String suitableCrowd;
    // 关联景点 [{scenicSpotId, sortNo, stayDuration}]
    private List<RouteSpotItem> spots;

    @Data
    public static class RouteSpotItem {
        private Long scenicSpotId;
        private Integer sortNo;
        private String stayDuration;
    }
}
