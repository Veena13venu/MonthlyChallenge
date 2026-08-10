package com.monthlychallenge.infrastructure.config;

import com.monthlychallenge.domain.service.DaySummaryDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfig {

    @Bean
    public DaySummaryDomainService daySummaryDomainService() {
        return new DaySummaryDomainService();
    }
}
