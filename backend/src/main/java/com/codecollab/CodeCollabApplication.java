package com.codecollab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * CodeCollab - Code Review & PR Management System
 * Main application entry point.
 * @EnableScheduling enables the SLA breach detection scheduler.
 */
@SpringBootApplication
@EnableScheduling
public class CodeCollabApplication {
    public static void main(String[] args) {
        SpringApplication.run(CodeCollabApplication.class, args);
    }
}
