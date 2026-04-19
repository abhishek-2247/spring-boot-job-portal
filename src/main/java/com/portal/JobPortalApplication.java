package com.portal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Skill Development & Job Connect Portal
 * EAD Mini Project — Spring Boot 3.2
 *
 * SDG 8: Decent Work & Economic Growth
 * SDG 4: Quality Education
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class JobPortalApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobPortalApplication.class, args);
        System.out.println("""
                ╔══════════════════════════════════════════════╗
                ║   Skill Development & Job Connect Portal      ║
                ║   Running at http://localhost:8090            ║
                ║   H2 Console: http://localhost:8090/h2-console║
                ╚══════════════════════════════════════════════╝
                """);
    }
}
