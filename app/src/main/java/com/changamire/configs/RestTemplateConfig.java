package com.changamire.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

/**
 * REST Template Configuration
 * <p>
 * This configuration sets up RestTemplate for external payment gateway API calls.
 * Includes base URL configuration and authorization header interceptor with API key.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
@Configuration
public class RestTemplateConfig {
    @Value("${motapa.api.base-url}")
    private String baseUrl;

    @Value("${motapa.api.secret-key}")
    private String secretKey;

    @Bean
    public RestTemplate motapaRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(baseUrl));

        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + secretKey);
            request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return execution.execute(request, body);
        });

        return restTemplate;
    }
}