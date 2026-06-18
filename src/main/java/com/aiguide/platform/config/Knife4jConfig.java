package com.aiguide.platform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class Knife4jConfig {
    @Bean
    public Docket defaultApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(springfox.documentation.builders.RequestHandlerSelectors.basePackage("com.aiguide.platform"))
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("AI多语种智能导游服务平台")
                .description("AI多语种智能导游服务平台接口文档")
                .version("1.0")
                .contact(new Contact("aiguide", "", ""))
                .build();
    }
}
