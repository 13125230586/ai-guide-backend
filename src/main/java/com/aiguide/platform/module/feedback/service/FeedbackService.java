package com.aiguide.platform.module.feedback.service;

import com.aiguide.platform.common.model.PageResponse;
import com.aiguide.platform.model.entity.UserFeedback;
import com.aiguide.platform.module.feedback.model.req.FeedbackCreateReq;
import com.aiguide.platform.module.feedback.model.req.FeedbackPageReq;
import com.aiguide.platform.module.feedback.model.req.FeedbackReplyReq;
import com.aiguide.platform.module.feedback.model.vo.FeedbackVO;
import com.baomidou.mybatisplus.extension.service.IService;

public interface FeedbackService extends IService<UserFeedback> {
    Long createFeedback(Long userId, FeedbackCreateReq req);

    PageResponse<FeedbackVO> pageMyFeedbacks(Long userId, FeedbackPageReq req);

    PageResponse<FeedbackVO> pageAllFeedbacks(FeedbackPageReq req);

    void replyFeedback(FeedbackReplyReq req);
}
