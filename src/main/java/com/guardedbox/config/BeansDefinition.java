package com.guardedbox.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class used to define beans.
 * 
 * @author s3curitybug@gmail.com
 *
 */
@Configuration
public class BeansDefinition {

    /**
     * Bean: RestTemplate.
     * 
     * @param restTemplateBuilder RestTemplateBuilder.
     * @return RestTemplate.
     */
    @Bean
    public RestTemplate restTemplate(
            RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }

}
