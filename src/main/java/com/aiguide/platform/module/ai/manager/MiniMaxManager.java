package com.aiguide.platform.module.ai.manager;

public interface MiniMaxManager {
    /**
     * 调用 MiniMax 模型，返回回答文本
     */
    String chat(String systemPrompt, String userMessage);
}
