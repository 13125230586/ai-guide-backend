package com.aiguide.platform.module.admin.model.vo;

import lombok.Data;
import java.util.List;

@Data
public class AdminDashboardVO {
    private long totalUsers;
    private long totalScenicSpots;
    private long totalRoutes;
    private long totalFavorites;
    private long totalFeedbacks;
    private long pendingFeedbacks;
    private long totalAiCalls;
    private long todayAiCalls;
    private List<HotScenicVO> hotScenics;
}
