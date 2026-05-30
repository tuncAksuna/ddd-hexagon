package com.project.hexagonal;

import com.project.hexagonal.shared.application.annotation.AppMapper;
import com.project.hexagonal.shared.application.annotation.DomainService;
import com.project.hexagonal.shared.infrastructure.annotation.DomainMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@EnableCaching
@ComponentScan(
        includeFilters = {
                @ComponentScan.Filter(
                type = FilterType.ANNOTATION, value = {
                DomainService.class,
                DomainMapper.class,
                AppMapper.class
        })})
@SpringBootApplication(scanBasePackages = "com.project.hexagonal")
public class OfferApplication {

    public static void main(String[] args) {
        SpringApplication.run(OfferApplication.class, args);
    }
}
