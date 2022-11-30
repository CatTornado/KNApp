package com.example.kn.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Collections;

@Configuration
public class RestConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder,
                                     @Value("${kn.rest.timeout:5000}") long timeout) {
        RestTemplate restTemplate = builder
                .setConnectTimeout(Duration.ofMillis(timeout))
                .setReadTimeout(Duration.ofMillis(timeout))
                .build();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        restTemplate.getMessageConverters().add(converter);
        return restTemplate;
    }

}