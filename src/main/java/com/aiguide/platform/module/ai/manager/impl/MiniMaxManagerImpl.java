package com.aiguide.platform.module.ai.manager.impl;

import com.aiguide.platform.config.MiniMaxProperties;
import com.aiguide.platform.module.ai.manager.MiniMaxManager;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * MiniMax 调用管理器实现。
 *
 * <p>这里直接按参考项目的 Anthropic 兼容格式组织请求，
 * 兼容普通文本问答，后续如果需要扩展图片能力也可以继续沿用同一套结构。</p>
 */
@Slf4j
@Component
public class MiniMaxManagerImpl implements MiniMaxManager {

    private static final String ROLE_SYSTEM = "system";

    private static final String ROLE_USER = "user";

    private static final String CONTENT_TYPE_TEXT = "text";

    private static final String DEFAULT_TEXT_MODEL = "MiniMax-M2.7";

    private static final String DEFAULT_SYSTEM_PROMPT = "你是一位专业的多语种智能导游，回答要准确、清晰、简洁。";

    private static final String ANTHROPIC_PATH = "/v1/messages";

    private static final String ANTHROPIC_VERSION_HEADER = "anthropic-version";

    private static final String ANTHROPIC_VERSION = "2023-06-01";

    private static final int DEFAULT_MAX_TOKENS = 1200;

    private final MiniMaxProperties miniMaxProperties;

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    public MiniMaxManagerImpl(MiniMaxProperties miniMaxProperties, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.miniMaxProperties = miniMaxProperties;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public String chat(String systemPrompt, String userMessage) {
        if (miniMaxProperties.isMockEnabled() || StringUtils.isBlank(miniMaxProperties.getApiKey())) {
            return buildMockReply(userMessage);
        }
        if (StringUtils.isBlank(userMessage)) {
            return "AI 请求内容不能为空";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Api-Key", miniMaxProperties.getApiKey());
        headers.set(ANTHROPIC_VERSION_HEADER, ANTHROPIC_VERSION);

        String requestBody = buildRequestBody(userMessage, systemPrompt);
        String requestUrl = buildRequestUrl();

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    requestUrl,
                    new HttpEntity<String>(requestBody, headers),
                    String.class
            );
            if (!response.getStatusCode().is2xxSuccessful() || StringUtils.isBlank(response.getBody())) {
                return "AI暂时无法回答，请稍后重试。";
            }
            String reply = extractReply(response.getBody());
            if (StringUtils.isBlank(reply)) {
                return "AI暂时无法回答，请稍后重试。";
            }
            return sanitizeModelContent(reply);
        } catch (HttpStatusCodeException exception) {
            log.error("MiniMax调用失败，状态码:{}, 响应体:{}", exception.getStatusCode(), exception.getResponseBodyAsString(), exception);
            return "AI服务暂时不可用，请稍后重试。";
        } catch (RestClientException exception) {
            log.error("MiniMax调用失败", exception);
            return "AI服务暂时不可用，请稍后重试。";
        } catch (Exception exception) {
            log.error("MiniMax响应解析失败", exception);
            return "AI服务暂时不可用，请稍后重试。";
        }
    }

    private String buildRequestUrl() {
        return miniMaxProperties.getBaseUrl() + ANTHROPIC_PATH;
    }

    private String buildRequestBody(String prompt, String systemPrompt) {
        try {
            List<Object> content = new ArrayList<Object>();
            Map<String, Object> textBlock = new LinkedHashMap<String, Object>();
            textBlock.put("type", CONTENT_TYPE_TEXT);
            textBlock.put("text", prompt);
            content.add(textBlock);

            List<Object> messages = new ArrayList<Object>();
            Map<String, Object> userMessage = new LinkedHashMap<String, Object>();
            userMessage.put("role", ROLE_USER);
            userMessage.put("content", content);
            messages.add(userMessage);

            Map<String, Object> request = new LinkedHashMap<String, Object>();
            request.put("model", StringUtils.defaultIfBlank(miniMaxProperties.getModel(), DEFAULT_TEXT_MODEL));
            request.put("system", StringUtils.defaultIfBlank(systemPrompt, DEFAULT_SYSTEM_PROMPT));
            request.put("max_tokens", miniMaxProperties.getMaxTokens() > 0 ? miniMaxProperties.getMaxTokens() : DEFAULT_MAX_TOKENS);
            request.put("messages", messages);

            return objectMapper.writeValueAsString(request);
        } catch (Exception exception) {
            throw new IllegalStateException("MiniMax 请求构建失败", exception);
        }
    }

    private String extractReply(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            String reply = extractReplyFromNode(root.path("content"));
            if (StringUtils.isBlank(reply)) {
                reply = extractReplyFromNode(root.path("choices"));
            }
            if (StringUtils.isBlank(reply)) {
                reply = extractReplyFromNode(root);
            }
            if (StringUtils.isBlank(reply)) {
                reply = responseBody;
            }
            return extractReplyFromString(reply);
        } catch (Exception exception) {
            return extractReplyFromString(responseBody);
        }
    }

    private String sanitizeModelContent(String content) {
        if (content == null) {
            return null;
        }
        String sanitized = content.replaceAll("(?is)<think>.*?</think>", "");
        sanitized = sanitized.replaceAll("(?is)^\\s*<think>.*$", "");
        return sanitized.trim();
    }

    private String buildMockReply(String userMessage) {
        return "【Mock AI回复】基于当前导游知识上下文，建议先了解景点的历史背景、文化特色和参观路线。问题摘要：" + userMessage;
    }

    private String extractReplyFromNode(JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return null;
        }
        if (node.isTextual()) {
            return node.asText();
        }
        if (node.isArray()) {
            StringBuilder builder = new StringBuilder();
            Iterator<JsonNode> iterator = node.iterator();
            while (iterator.hasNext()) {
                String part = extractReplyFromNode(iterator.next());
                if (StringUtils.isBlank(part)) {
                    continue;
                }
                if (builder.length() > 0) {
                    builder.append("\n");
                }
                builder.append(part.trim());
            }
            return builder.toString();
        }
        if (node.isObject()) {
            if (node.has("text") && node.get("text").isTextual()) {
                return node.get("text").asText();
            }
            if (node.has("content")) {
                String nested = extractReplyFromNode(node.get("content"));
                if (StringUtils.isNotBlank(nested)) {
                    return nested;
                }
            }
            if (node.has("reply") && node.get("reply").isTextual()) {
                return node.get("reply").asText();
            }
            if (node.has("choices")) {
                return extractReplyFromNode(node.get("choices"));
            }
        }
        return null;
    }

    private String extractReplyFromString(String rawText) {
        if (StringUtils.isBlank(rawText)) {
            return rawText;
        }
        String trimmed = rawText.trim();
        if (!(StringUtils.startsWith(trimmed, "{") || StringUtils.startsWith(trimmed, "["))) {
            return trimmed;
        }
        try {
            JsonNode nested = objectMapper.readTree(trimmed);
            String nestedReply = extractReplyFromNode(nested);
            return StringUtils.defaultIfBlank(nestedReply, trimmed);
        } catch (Exception exception) {
            return trimmed;
        }
    }

}
