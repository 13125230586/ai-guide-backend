package com.aiguide.platform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.aiguide.platform.mapper")
public class AiGuideBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(AiGuideBackendApplication.class, args);
    }
}
