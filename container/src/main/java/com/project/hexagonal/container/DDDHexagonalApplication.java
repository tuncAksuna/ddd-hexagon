package com.project.hexagonal.container;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.project.hexagonal")
@EnableJpaRepositories(basePackages = "com.project.hexagonal.infra")
public class DDDHexagonalApplication {

    public static void main(String[] args) {
        SpringApplication.run(DDDHexagonalApplication.class, args);
    }
}
