package com.aiguide.platform.config;

import java.time.Duration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate 配置类。
 *
 * <p>用于统一创建带连接超时和读取超时的 RestTemplate，供 AI 调用、
 * 第三方服务调用等 HTTP 场景复用，避免每个调用点重复配置。</p>
 */
@Configuration
public class RestTemplateConfig {

    /**
     * 创建 RestTemplate 实例。
     *
     * <p>这里统一注入超时配置，保证调用第三方接口时不会无限等待。</p>
     *
     * @param restTemplateBuilder RestTemplate 构造器
     * @param miniMaxProperties AI 配置
     * @return 带超时配置的 RestTemplate
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder, MiniMaxProperties miniMaxProperties) {
        return restTemplateBuilder
            .setConnectTimeout(Duration.ofMillis(miniMaxProperties.getConnectTimeoutMs()))
            .setReadTimeout(Duration.ofMillis(miniMaxProperties.getReadTimeoutMs()))
            .build();
    }
}
