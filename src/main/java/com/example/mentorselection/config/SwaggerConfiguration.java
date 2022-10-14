package com.example.mentorselection.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.List;

@Configuration
public class SwaggerConfiguration {
    // 添加输入token全局验证按钮
    @Bean
    public Docket createRestApi() {
        SecurityContext t = SecurityContext.builder()
                .securityReferences(List.of(new SecurityReference("token",
                        new AuthorizationScope[]{new AuthorizationScope("global", "")})))
                .build();
        // 移除 Basic Error Controller
        return new Docket(DocumentationType.OAS_30)
                .select()
                .paths(PathSelectors.regex("(?!/error.*).*"))
                .build()
                .securitySchemes(List.of(new ApiKey("token", "token", "header")))
                .securityContexts(List.of(t));
    }
}
