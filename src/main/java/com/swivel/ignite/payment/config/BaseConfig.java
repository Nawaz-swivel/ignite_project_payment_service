package com.swivel.ignite.payment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Base component configuration
 */
@Configuration
public class BaseConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
