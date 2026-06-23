package com.xxx.equipment.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("设备管理系统 API")
                        .description("基于 Spring Boot 3 的设备管理系统接口文档")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("admin")
                                .email("admin@xxx.com")))
                .addSecurityItem(new SecurityRequirement().addList("Authorization"))
                .components(new Components()
                        .addSecuritySchemes("Authorization",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT Token")));
    }

    @Bean
    public GroupedOpenApi loginApi() {
        return GroupedOpenApi.builder()
                .group("登录管理")
                .pathsToMatch("/login")
                .build();
    }

    @Bean
    public GroupedOpenApi deviceApi() {
        return GroupedOpenApi.builder()
                .group("设备管理")
                .pathsToMatch("/device/**")
                .build();
    }

    @Bean
    public GroupedOpenApi inventoryApi() {
        return GroupedOpenApi.builder()
                .group("库存管理")
                .pathsToMatch("/inventory/**")
                .build();
    }

    @Bean
    public GroupedOpenApi reportApi() {
        return GroupedOpenApi.builder()
                .group("采购报表")
                .pathsToMatch("/report/**")
                .build();
    }

    @Bean
    public GroupedOpenApi purchaseOrderApi() {
        return GroupedOpenApi.builder()
                .group("采购订单管理")
                .pathsToMatch("/purchase-order/**")
                .build();
    }
}
