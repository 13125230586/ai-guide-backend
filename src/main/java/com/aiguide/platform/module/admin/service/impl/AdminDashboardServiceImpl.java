package com.aiguide.platform.module.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.aiguide.platform.mapper.*;
import com.aiguide.platform.model.entity.*;
import com.aiguide.platform.module.admin.model.vo.AdminDashboardVO;
import com.aiguide.platform.module.admin.model.vo.HotScenicVO;
import com.aiguide.platform.module.admin.service.AdminDashboardService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class AdminDashboardServiceImpl implements AdminDashboardService {

    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private ScenicSpotMapper scenicSpotMapper;
    @Resource
    private GuideRouteMapper guideRouteMapper;
    @Resource
    private UserFavoriteMapper userFavoriteMapper;
    @Resource
    private UserFeedbackMapper userFeedbackMapper;
    @Resource
    private AiGuideLogMapper aiGuideLogMapper;

    @Override
    public AdminDashboardVO getDashboard() {
        AdminDashboardVO vo = new AdminDashboardVO();
        vo.setTotalUsers(sysUserMapper.selectCount(null));
        vo.setTotalScenicSpots(scenicSpotMapper.selectCount(null));
        vo.setTotalRoutes(guideRouteMapper.selectCount(null));
        vo.setTotalFavorites(userFavoriteMapper.selectCount(null));
        vo.setTotalFeedbacks(userFeedbackMapper.selectCount(null));

        // 待处理反馈
        LambdaQueryWrapper<UserFeedback> fbWrapper = new LambdaQueryWrapper<>();
        fbWrapper.eq(UserFeedback::getFeedbackStatus, 0);
        vo.setPendingFeedbacks(userFeedbackMapper.selectCount(fbWrapper));

        // AI 调用总数
        vo.setTotalAiCalls(aiGuideLogMapper.selectCount(null));

        // 今日 AI 调用
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        LambdaQueryWrapper<AiGuideLog> todayWrapper = new LambdaQueryWrapper<>();
        todayWrapper.ge(AiGuideLog::getCreateTime, cal.getTime());
        vo.setTodayAiCalls(aiGuideLogMapper.selectCount(todayWrapper));

        // 热门景点 Top 10
        LambdaQueryWrapper<ScenicSpot> hotWrapper = new LambdaQueryWrapper<>();
        hotWrapper.orderByDesc(ScenicSpot::getHotScore).last("LIMIT 10");
        List<ScenicSpot> hotSpots = scenicSpotMapper.selectList(hotWrapper);
        List<HotScenicVO> hotList = new ArrayList<>();
        for (ScenicSpot spot : hotSpots) {
            HotScenicVO h = new HotScenicVO();
            h.setId(spot.getId());
            h.setSpotName(spot.getSpotName());
            h.setCity(spot.getCity());
            h.setViewCount(spot.getViewCount());
            h.setHotScore(spot.getHotScore());
            hotList.add(h);
        }
        vo.setHotScenics(hotList);
        return vo;
    }
}
