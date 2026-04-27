package com.hutech.buiduongtin.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI buiduongtinOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("BUIDUONGTIN API")
                        .version("v1")
                        .description("REST API cho he thong thuong mai dien tu"));
    }
}
