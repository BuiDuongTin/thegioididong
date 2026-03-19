package com.hutech.buiduongtin.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get("src/main/resources/static/images");
        String uploadPath = uploadDir.toFile().getAbsolutePath();

        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:/" + uploadPath + "/");
    }
}
