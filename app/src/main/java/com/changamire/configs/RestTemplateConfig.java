package com.changamire.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

// RestTemplateConfig.java
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