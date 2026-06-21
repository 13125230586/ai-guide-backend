package com.aiguide.platform.module.ai.manager;

import java.util.List;

public interface MiniMaxManager {
    /**
     * 调用 MiniMax 模型，返回回答文本
     */
    String chat(String systemPrompt, String userMessage);

    /**
     * 调用 MiniMax 视觉模型，返回图片理解文本
     */
    String chatWithImages(String systemPrompt, String userMessage, List<String> imageUrls);

    /**
     * 调用 MiniMax 图片理解接口，返回图片分析文本
     */
    String understandImage(String prompt, String imageUrl);
}
