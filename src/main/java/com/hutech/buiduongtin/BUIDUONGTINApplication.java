package com.hutech.buiduongtin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class BUIDUONGTINApplication {

    public static void main(String[] args) {
        SpringApplication.run(BUIDUONGTINApplication.class, args);
    }

}
