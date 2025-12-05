package com.agriculture.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI配置 (SpringDoc)
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("农业算枣微服务API文档")
                        .description("农业算枣智慧农业平台微服务架构API接口文档")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Agriculture Team")
                                .email("agriculture@example.com")));
    }
}
