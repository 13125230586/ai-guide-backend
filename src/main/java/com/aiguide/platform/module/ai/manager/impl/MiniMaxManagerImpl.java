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

    private static final String CONTENT_TYPE_IMAGE_URL = "image_url";

    private static final String DEFAULT_TEXT_MODEL = "MiniMax-M2.7";

    private static final String DEFAULT_VISION_MODEL = "MiniMax-VL-01";

    private static final String DEFAULT_SYSTEM_PROMPT = "你是一位专业的多语种智能导游，回答要准确、清晰、简洁。";

    private static final String OPENAI_PATH = "/chat/completions";

    private static final String ANTHROPIC_PATH = "/v1/messages";

    private static final String MCP_VLM_PATH = "/v1/coding_plan/vlm";

    private static final String OPENAI_BASE_URL = "https://api.minimaxi.com/v1";

    private static final String MCP_BASE_URL = "https://api.minimaxi.com";

    private static final String OPENAI_AUTH_PREFIX = "Bearer ";

    private static final String MM_API_SOURCE = "MM-API-Source";

    private static final String MM_API_SOURCE_VALUE = "Minimax-MCP";

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

    @Override
    public String chatWithImages(String systemPrompt, String userMessage, List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return chat(systemPrompt, userMessage);
        }
        if (miniMaxProperties.isMockEnabled() || StringUtils.isBlank(miniMaxProperties.getApiKey())) {
            return buildMockReply(userMessage) + " 已收到" + imageUrls.size() + "张图片。";
        }
        if (StringUtils.isBlank(userMessage)) {
            return "AI 请求内容不能为空";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.AUTHORIZATION, OPENAI_AUTH_PREFIX + miniMaxProperties.getApiKey());

        String requestBody = buildVisionRequestBody(userMessage, systemPrompt, imageUrls);
        String requestUrl = buildOpenAiRequestUrl();

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    requestUrl,
                    new HttpEntity<String>(requestBody, headers),
                    String.class
            );
            if (!response.getStatusCode().is2xxSuccessful() || StringUtils.isBlank(response.getBody())) {
                return "AI暂时无法识别图片，请稍后重试。";
            }
            String reply = extractOpenAiReply(response.getBody());
            if (StringUtils.isBlank(reply)) {
                return "AI暂时无法识别图片，请稍后重试。";
            }
            return sanitizeModelContent(reply);
        } catch (HttpStatusCodeException exception) {
            log.error("MiniMax图片识别失败，状态码:{}, 响应体:{}", exception.getStatusCode(), exception.getResponseBodyAsString(), exception);
            return "AI图片识别服务暂时不可用，请稍后重试。";
        } catch (RestClientException exception) {
            log.error("MiniMax图片识别失败", exception);
            return "AI图片识别服务暂时不可用，请稍后重试。";
        } catch (Exception exception) {
            log.error("MiniMax图片识别解析失败", exception);
            return "AI图片识别服务暂时不可用，请稍后重试。";
        }
    }

    @Override
    public String understandImage(String prompt, String imageUrl) {
        if (miniMaxProperties.isMockEnabled() || StringUtils.isBlank(miniMaxProperties.getApiKey())) {
            return "【Mock 图片识别】已收到图片，请根据景点建筑、文字标识、自然景观和游客问题进行判断。问题摘要：" + prompt;
        }
        if (StringUtils.isBlank(prompt)) {
            return "图片识别请求内容不能为空";
        }
        if (StringUtils.isBlank(imageUrl)) {
            return "图片地址不能为空";
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.AUTHORIZATION, OPENAI_AUTH_PREFIX + miniMaxProperties.getApiKey());
        headers.set(MM_API_SOURCE, MM_API_SOURCE_VALUE);
        String requestBody = buildMcpVlmRequest(prompt, imageUrl);
        String requestUrl = buildMcpVlmUrl();
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    requestUrl,
                    new HttpEntity<String>(requestBody, headers),
                    String.class
            );
            if (!response.getStatusCode().is2xxSuccessful() || StringUtils.isBlank(response.getBody())) {
                return "AI暂时无法识别图片，请稍后重试。";
            }
            return parseMcpVlmResponse(response.getBody());
        } catch (HttpStatusCodeException exception) {
            log.error("MiniMax图片理解失败，状态码:{}, 响应体:{}", exception.getStatusCode(), exception.getResponseBodyAsString(), exception);
            return "AI图片识别服务暂时不可用，请稍后重试。";
        } catch (RestClientException exception) {
            log.error("MiniMax图片理解失败", exception);
            return "AI图片识别服务暂时不可用，请稍后重试。";
        } catch (Exception exception) {
            log.error("MiniMax图片理解解析失败", exception);
            return "AI图片识别服务暂时不可用，请稍后重试。";
        }
    }

    private String buildRequestUrl() {
        return miniMaxProperties.getBaseUrl() + ANTHROPIC_PATH;
    }

    private String buildOpenAiRequestUrl() {
        String normalized = StringUtils.defaultIfBlank(miniMaxProperties.getBaseUrl(), OPENAI_BASE_URL).trim();
        if (normalized.endsWith(OPENAI_PATH)) {
            return normalized;
        }
        if (StringUtils.containsIgnoreCase(normalized, "/anthropic")) {
            normalized = OPENAI_BASE_URL;
        }
        if (normalized.endsWith("/")) {
            return normalized + "chat/completions";
        }
        if (normalized.endsWith("/v1")) {
            return normalized + OPENAI_PATH;
        }
        return normalized + OPENAI_PATH;
    }

    private String buildMcpVlmUrl() {
        String normalized = StringUtils.defaultIfBlank(miniMaxProperties.getBaseUrl(), MCP_BASE_URL).trim();
        if (normalized.endsWith(MCP_VLM_PATH)) {
            return normalized;
        }
        if (normalized.endsWith("/")) {
            return normalized + "v1/coding_plan/vlm";
        }
        if (normalized.endsWith("/v1")) {
            return normalized + "/coding_plan/vlm";
        }
        if (StringUtils.containsIgnoreCase(normalized, "/anthropic")) {
            return MCP_BASE_URL + MCP_VLM_PATH;
        }
        return normalized + MCP_VLM_PATH;
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

    private String buildVisionRequestBody(String prompt, String systemPrompt, List<String> imageUrls) {
        try {
            List<Object> messages = new ArrayList<Object>();
            messages.add(buildOpenAiSimpleMessage(ROLE_SYSTEM, StringUtils.defaultIfBlank(systemPrompt, DEFAULT_SYSTEM_PROMPT)));
            messages.add(buildOpenAiUserMessage(prompt, imageUrls));

            Map<String, Object> request = new LinkedHashMap<String, Object>();
            request.put("model", DEFAULT_VISION_MODEL);
            request.put("max_tokens", miniMaxProperties.getMaxTokens() > 0 ? miniMaxProperties.getMaxTokens() : DEFAULT_MAX_TOKENS);
            request.put("messages", messages);
            return objectMapper.writeValueAsString(request);
        } catch (Exception exception) {
            throw new IllegalStateException("MiniMax 图片识别请求构建失败", exception);
        }
    }

    private String buildMcpVlmRequest(String prompt, String imageUrl) {
        try {
            McpVlmRequest request = new McpVlmRequest();
            request.setPrompt(prompt);
            request.setImageUrl(imageUrl);
            return objectMapper.writeValueAsString(request);
        } catch (Exception exception) {
            throw new IllegalStateException("MiniMax 图片理解请求构建失败", exception);
        }
    }

    private Map<String, Object> buildOpenAiSimpleMessage(String role, String content) {
        Map<String, Object> message = new LinkedHashMap<String, Object>();
        message.put("role", role);
        message.put("content", content);
        return message;
    }

    private Map<String, Object> buildOpenAiUserMessage(String prompt, List<String> imageUrls) {
        List<Object> contentParts = new ArrayList<Object>();
        Map<String, Object> textPart = new LinkedHashMap<String, Object>();
        textPart.put("type", CONTENT_TYPE_TEXT);
        textPart.put("text", prompt);
        contentParts.add(textPart);
        for (String imageUrl : imageUrls) {
            if (StringUtils.isBlank(imageUrl)) {
                continue;
            }
            Map<String, Object> imagePayload = new LinkedHashMap<String, Object>();
            imagePayload.put("url", imageUrl);
            Map<String, Object> imagePart = new LinkedHashMap<String, Object>();
            imagePart.put("type", CONTENT_TYPE_IMAGE_URL);
            imagePart.put("image_url", imagePayload);
            contentParts.add(imagePart);
        }
        Map<String, Object> userMessage = new LinkedHashMap<String, Object>();
        userMessage.put("role", ROLE_USER);
        userMessage.put("content", contentParts);
        return userMessage;
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

    private String extractOpenAiReply(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode messageNode = root.path("choices").path(0).path("message");
            String reply = extractReplyFromNode(messageNode.path("content"));
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

    private String parseMcpVlmResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode baseResp = root.path("base_resp");
            if (baseResp.isObject() && baseResp.path("status_code").asInt(0) != 0) {
                return "AI图片识别失败：" + baseResp.path("status_msg").asText("未知错误");
            }
            String content = root.path("content").asText("");
            if (StringUtils.isBlank(content)) {
                content = responseBody;
            }
            return sanitizeModelContent(content);
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

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class McpVlmRequest {

        private String prompt;

        @JsonProperty("image_url")
        private String imageUrl;
    }

}
