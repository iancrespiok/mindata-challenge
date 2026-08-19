package com.ian.challenge.infrastructure.config;

import com.ian.challenge.application.services.SearchCountService;
import com.ian.challenge.application.services.SearchRegistrationService;
import com.ian.challenge.application.port.in.GetSearchCountUseCase;
import com.ian.challenge.application.port.in.RegisterSearchUseCase;
import com.ian.challenge.domain.port.out.SearchEventPublisher;
import com.ian.challenge.domain.port.out.SearchRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public RegisterSearchUseCase registerSearchUseCase(SearchEventPublisher eventPublisher) {
        return new SearchRegistrationService(eventPublisher);
    }

    @Bean
    public GetSearchCountUseCase getSearchCountUseCase(SearchRepository searchRepository) {
        return new SearchCountService(searchRepository);
    }
}