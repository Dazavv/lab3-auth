package com.hs.lab3.eventservice.client;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    @Bean
    public RequestInterceptor eventServiceInterceptor(
            @Value("${service.token}") String token
    ) {
        return template -> template.header("Authorization", "Bearer " + token);
    }
}
