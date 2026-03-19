package com.hutech.buiduongtin.database;

import com.hutech.buiduongtin.repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * DatabaseLoader - Được thay thế bởi DataInitializer.
 * Giữ lại file này nhưng không làm gì để tránh conflict.
 */
@Configuration
public class DatabaseLoader {

    @Bean
    CommandLineRunner initDatabase(CategoryRepository categoryRepository) {
        // Logic đã được chuyển sang DataInitializer.java
        return args -> {
        };
    }
}
