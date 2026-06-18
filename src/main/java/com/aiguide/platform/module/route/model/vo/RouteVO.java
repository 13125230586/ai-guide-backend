package com.aiguide.platform.module.route.model.vo;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class RouteVO {
    private Long id;
    private String routeName;
    private String theme;
    private String coverUrl;
    private String summary;
    private String description;
    private String suggestDuration;
    private String suitableCrowd;
    private Integer routeStatus;
    private Date createTime;
    private List<RouteSpotVO> scenicSpots;

    @Data
    public static class RouteSpotVO {
        private Long id;
        private Long categoryId;
        private String categoryName;
        private String spotName;
        private String city;
        private String address;
        private java.math.BigDecimal longitude;
        private java.math.BigDecimal latitude;
        private String coverUrl;
        private String summary;
        private String description;
        private String openTime;
        private String suggestDuration;
        private String tips;
        private Integer hotScore;
        private Integer spotStatus;
        private Integer viewCount;
        private Date createTime;
        private Integer sortNo;
        private String stayDuration;
    }
}
