package com.pap.springbootservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SSEApplication {
    public static void main(String[] args) {
        SpringApplication.run(SSEApplication.class, args);
    }
}
