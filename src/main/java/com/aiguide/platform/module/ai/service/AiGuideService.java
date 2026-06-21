package com.aiguide.platform.module.ai.service;

import com.aiguide.platform.common.model.PageResponse;
import com.aiguide.platform.module.ai.model.req.*;
import com.aiguide.platform.module.ai.model.vo.AiChatRecordVO;
import com.aiguide.platform.module.ai.model.vo.AiGuideLogVO;
import com.aiguide.platform.module.ai.model.vo.AiGuideVO;

public interface AiGuideService {
    AiGuideVO explainScenic(Long userId, AiGuideReq req);

    AiGuideVO compareScenic(Long userId, AiCompareReq req);

    AiGuideVO recommendRoute(Long userId, AiRecommendReq req);

    AiGuideVO commonQuestion(Long userId, AiGuideReq req);

    AiGuideVO recognizeScenic(Long userId, AiRecognizeReq req);

    AiGuideVO translateAnswer(Long userId, AiGuideReq req);

    PageResponse<AiGuideLogVO> pageAiLogs(AiLogPageReq req);

    PageResponse<AiChatRecordVO> pageChatRecords(Long userId, AiRecordPageReq req);
}
