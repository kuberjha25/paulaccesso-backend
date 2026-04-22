package com.paulaccesso.visitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VisitorApplication {
    public static void main(String[] args) {
        SpringApplication.run(VisitorApplication.class, args);
        System.out.println("========================================");
        System.out.println("Kuber!");
        System.out.println("========================================");
    }
}