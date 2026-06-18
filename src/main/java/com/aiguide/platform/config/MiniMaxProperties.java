package com.aiguide.platform.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.ai.minimax")
public class MiniMaxProperties {
    private boolean mockEnabled = false;
    private String apiKey;
    private String groupId;
    private String model = "MiniMax-M2.7";
    private String baseUrl = "https://api.minimaxi.com/anthropic";
    private int maxTokens = 700;
    private int connectTimeoutMs = 5000;
    private int readTimeoutMs = 60000;
}
