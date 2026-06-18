package com.aiguide.platform.module.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiguide.platform.common.constant.BusinessConstant;
import com.aiguide.platform.common.exception.ErrorCode;
import com.aiguide.platform.common.model.PageResponse;
import com.aiguide.platform.common.util.ThrowUtils;
import com.aiguide.platform.config.MiniMaxProperties;
import com.aiguide.platform.mapper.AiChatRecordMapper;
import com.aiguide.platform.mapper.AiGuideLogMapper;
import com.aiguide.platform.mapper.ScenicSpotMapper;
import com.aiguide.platform.mapper.SysUserMapper;
import com.aiguide.platform.model.entity.AiChatRecord;
import com.aiguide.platform.model.entity.AiGuideLog;
import com.aiguide.platform.model.entity.ScenicSpot;
import com.aiguide.platform.model.entity.SysUser;
import com.aiguide.platform.module.ai.manager.MiniMaxManager;
import com.aiguide.platform.module.ai.model.req.*;
import com.aiguide.platform.module.ai.model.vo.AiChatRecordVO;
import com.aiguide.platform.module.ai.model.vo.AiGuideLogVO;
import com.aiguide.platform.module.ai.model.vo.AiGuideVO;
import com.aiguide.platform.module.ai.service.AiGuideService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AiGuideServiceImpl implements AiGuideService {

    @Resource
    private MiniMaxManager miniMaxManager;
    @Resource
    private MiniMaxProperties miniMaxProperties;
    @Resource
    private AiGuideLogMapper aiGuideLogMapper;
    @Resource
    private AiChatRecordMapper aiChatRecordMapper;
    @Resource
    private ScenicSpotMapper scenicSpotMapper;
    @Resource
    private SysUserMapper sysUserMapper;

    private static final String SYSTEM_PROMPT = "你是一位专业的多语种智能导游，擅长为游客提供景点讲解、文化介绍、路线建议和旅行问答服务。请用简洁、友好、专业的语气回答游客问题。";

    @Override
    public AiGuideVO explainScenic(Long userId, AiGuideReq req) {
        ThrowUtils.throwIf(req.getScenicSpotId() == null, ErrorCode.PARAMS_ERROR, "景点ID不能为空");
        ScenicSpot spot = scenicSpotMapper.selectById(req.getScenicSpotId());
        ThrowUtils.throwIf(spot == null, ErrorCode.NOT_FOUND_ERROR, "景点不存在");

        String langHint = getLangHint(req.getLanguageCode());
        String prompt = String.format(
                "请为游客详细讲解以下景点，包括历史背景、文化特色、参观亮点和注意事项。\n景点名称：%s\n景点简介：%s\n游客问题：%s\n%s",
                spot.getSpotName(), spot.getSummary() != null ? spot.getSummary() : "", req.getQuestion(), langHint
        );

        return callAndLog(userId, BusinessConstant.AI_BIZ_EXPLAIN, req.getLanguageCode(),
                req.getScenicSpotId(), null, prompt, req.getQuestion());
    }

    @Override
    public AiGuideVO compareScenic(Long userId, AiCompareReq req) {
        ScenicSpot spotA = scenicSpotMapper.selectById(req.getScenicSpotIdA());
        ScenicSpot spotB = scenicSpotMapper.selectById(req.getScenicSpotIdB());
        ThrowUtils.throwIf(spotA == null || spotB == null, ErrorCode.NOT_FOUND_ERROR, "景点不存在");

        String langHint = getLangHint(req.getLanguageCode());
        String prompt = String.format(
                "请对比以下两个景点，分析各自的特色、适合人群和游览建议。\n景点A：%s - %s\n景点B：%s - %s\n%s",
                spotA.getSpotName(), spotA.getSummary() != null ? spotA.getSummary() : "",
                spotB.getSpotName(), spotB.getSummary() != null ? spotB.getSummary() : "", langHint
        );

        return callAndLog(userId, BusinessConstant.AI_BIZ_COMPARE, req.getLanguageCode(),
                null, null, prompt, "对比" + spotA.getSpotName() + "和" + spotB.getSpotName());
    }

    @Override
    public AiGuideVO recommendRoute(Long userId, AiRecommendReq req) {
        String langHint = getLangHint(req.getLanguageCode());
        String prompt = String.format(
                "请根据以下游客需求推荐合适的游玩路线和景点安排。\n时间预算：%s\n兴趣偏好：%s\n出行人群：%s\n%s",
                req.getTimeBudget() != null ? req.getTimeBudget() : "未指定",
                req.getInterest() != null ? req.getInterest() : "未指定",
                req.getCrowd() != null ? req.getCrowd() : "未指定", langHint
        );

        return callAndLog(userId, BusinessConstant.AI_BIZ_RECOMMEND, req.getLanguageCode(),
                null, null, prompt, "路线推荐");
    }

    @Override
    public AiGuideVO commonQuestion(Long userId, AiGuideReq req) {
        String langHint = getLangHint(req.getLanguageCode());
        String context = "";
        if (req.getScenicSpotId() != null) {
            ScenicSpot spot = scenicSpotMapper.selectById(req.getScenicSpotId());
            if (spot != null) {
                context = "\n当前景点：" + spot.getSpotName() + " - " + (spot.getSummary() != null ? spot.getSummary() : "");
            }
        }
        String prompt = String.format("作为智能导游，请回答游客的问题。\n游客问题：%s%s\n%s",
                req.getQuestion(), context, langHint);

        return callAndLog(userId, BusinessConstant.AI_BIZ_QUESTION, req.getLanguageCode(),
                req.getScenicSpotId(), null, prompt, req.getQuestion());
    }

    @Override
    public AiGuideVO translateAnswer(Long userId, AiGuideReq req) {
        String langHint = getLangHint(req.getLanguageCode());
        String prompt = String.format(
                "请将以下导游讲解内容翻译为目标语言，保持专业性和可读性。\n原文：%s\n%s",
                req.getQuestion(), langHint
        );

        return callAndLog(userId, BusinessConstant.AI_BIZ_TRANSLATE, req.getLanguageCode(),
                req.getScenicSpotId(), null, prompt, req.getQuestion());
    }

    @Override
    public PageResponse<AiGuideLogVO> pageAiLogs(AiLogPageReq req) {
        LambdaQueryWrapper<AiGuideLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(req.getBizType() != null, AiGuideLog::getBizType, req.getBizType());
        wrapper.eq(req.getSuccessFlag() != null, AiGuideLog::getSuccessFlag, req.getSuccessFlag());
        wrapper.eq(req.getLanguageCode() != null, AiGuideLog::getLanguageCode, req.getLanguageCode());
        wrapper.orderByDesc(AiGuideLog::getCreateTime);

        Page<AiGuideLog> page = aiGuideLogMapper.selectPage(
                new Page<>(req.getCurrent(), req.getPageSize()), wrapper);
        List<AiGuideLogVO> voList = page.getRecords().stream().map(this::toLogVO).collect(Collectors.toList());
        return PageResponse.of(page, voList);
    }

    @Override
    public PageResponse<AiChatRecordVO> pageChatRecords(Long userId, AiRecordPageReq req) {
        LambdaQueryWrapper<AiChatRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(userId != null, AiChatRecord::getUserId, userId);
        wrapper.eq(req.getQuestionType() != null && !req.getQuestionType().trim().isEmpty(), AiChatRecord::getQuestionType, req.getQuestionType());
        wrapper.and(req.getKeyword() != null && !req.getKeyword().trim().isEmpty(), query -> query
                .like(AiChatRecord::getQuestionContent, req.getKeyword())
                .or().like(AiChatRecord::getAnswerContent, req.getKeyword())
                .or().like(AiChatRecord::getPromptText, req.getKeyword()));
        wrapper.orderByDesc(AiChatRecord::getCreateTime);

        Page<AiChatRecord> page = aiChatRecordMapper.selectPage(
                new Page<>(req.getCurrent(), req.getPageSize()), wrapper);
        List<AiChatRecordVO> voList = page.getRecords().stream().map(this::toChatRecordVO).collect(Collectors.toList());
        return PageResponse.of(page, voList);
    }

    private AiGuideVO callAndLog(Long userId, String bizType, String languageCode,
                                  Long scenicSpotId, Long routeId,
                                  String prompt, String requestSummary) {
        long start = System.currentTimeMillis();
        AiGuideLog logEntity = new AiGuideLog();
        logEntity.setUserId(userId);
        logEntity.setModelName(miniMaxProperties.getModel());
        logEntity.setBizType(bizType);
        logEntity.setLanguageCode(languageCode);
        logEntity.setScenicSpotId(scenicSpotId);
        logEntity.setRouteId(routeId);
        logEntity.setRequestSummary(truncate(requestSummary, 4000));

        String answer = null;
        boolean success = false;
        try {
            log.info("AI调用开始 userId:{} bizType:{} scenicSpotId:{} routeId:{}", userId, bizType, scenicSpotId, routeId);
            answer = miniMaxManager.chat(SYSTEM_PROMPT, prompt);
            long cost = System.currentTimeMillis() - start;
            success = true;
            logEntity.setResponseSummary(truncate(answer, 4000));
            logEntity.setSuccessFlag(1);
            logEntity.setCostMillis(cost);
            log.info("AI调用完成 userId:{} bizType:{} costMillis:{}", userId, bizType, cost);
        } catch (Exception e) {
            long cost = System.currentTimeMillis() - start;
            logEntity.setSuccessFlag(0);
            logEntity.setCostMillis(cost);
            logEntity.setErrorMessage(truncate(e.getMessage(), 1000));
            log.error("AI调用失败 userId:{} bizType:{} costMillis:{}", userId, bizType, cost, e);
            answer = "AI服务暂时不可用，请稍后重试。";
        } finally {
            persistChatRecord(userId, bizType, languageCode, scenicSpotId, routeId, prompt, requestSummary, answer);
            aiGuideLogMapper.insert(logEntity);
        }

        AiGuideVO vo = new AiGuideVO();
        vo.setAnswer(answer);
        vo.setLanguageCode(languageCode);
        vo.setCostMillis(logEntity.getCostMillis());
        return vo;
    }

    private String getLangHint(String languageCode) {
        if (languageCode == null) return "";
        switch (languageCode) {
            case "en-US": return "Please answer in English.";
            case "ja-JP": return "日本語で回答してください。";
            case "ko-KR": return "한국어로 답변해주세요.";
            default: return "请用中文回答。";
        }
    }

    private String truncate(String text, int maxLen) {
        if (text == null) return null;
        return text.length() > maxLen ? text.substring(0, maxLen) : text;
    }

    private void persistChatRecord(Long userId, String bizType, String languageCode,
                                   Long scenicSpotId, Long routeId,
                                   String prompt, String requestSummary, String answer) {
        AiChatRecord record = new AiChatRecord();
        record.setUserId(userId);
        record.setScenicSpotId(scenicSpotId);
        record.setRouteId(routeId);
        record.setQuestionType(bizType);
        record.setQuestionContent(truncate(requestSummary, 4000));
        record.setPromptText(truncate(prompt, 4000));
        record.setAnswerContent(truncate(answer, 4000));
        record.setModelName(miniMaxProperties.getModel());
        record.setLanguageCode(languageCode);
        aiChatRecordMapper.insert(record);
    }

    private AiGuideLogVO toLogVO(AiGuideLog entity) {
        AiGuideLogVO vo = new AiGuideLogVO();
        vo.setId(entity.getId());
        vo.setUserId(entity.getUserId());
        vo.setModelName(entity.getModelName());
        vo.setBizType(entity.getBizType());
        vo.setLanguageCode(entity.getLanguageCode());
        vo.setScenicSpotId(entity.getScenicSpotId());
        vo.setRouteId(entity.getRouteId());
        vo.setRequestSummary(entity.getRequestSummary());
        vo.setResponseSummary(entity.getResponseSummary());
        vo.setSuccessFlag(entity.getSuccessFlag());
        vo.setCostMillis(entity.getCostMillis());
        vo.setErrorMessage(entity.getErrorMessage());
        vo.setCreateTime(entity.getCreateTime());
        if (entity.getUserId() != null) {
            SysUser user = sysUserMapper.selectById(entity.getUserId());
            if (user != null) vo.setUsername(user.getUsername());
        }
        return vo;
    }

    private AiChatRecordVO toChatRecordVO(AiChatRecord entity) {
        AiChatRecordVO vo = new AiChatRecordVO();
        vo.setId(entity.getId());
        vo.setUserId(entity.getUserId());
        vo.setScenicSpotId(entity.getScenicSpotId());
        vo.setRouteId(entity.getRouteId());
        vo.setQuestionType(entity.getQuestionType());
        vo.setQuestionContent(entity.getQuestionContent());
        vo.setPromptText(entity.getPromptText());
        vo.setAnswerContent(entity.getAnswerContent());
        vo.setModelName(entity.getModelName());
        vo.setLanguageCode(entity.getLanguageCode());
        vo.setCreateTime(entity.getCreateTime());
        if (entity.getUserId() != null) {
            SysUser user = sysUserMapper.selectById(entity.getUserId());
            if (user != null) vo.setUsername(user.getUsername());
        }
        return vo;
    }
}
