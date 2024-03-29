package com.github.api.githubintegration.config;

import com.github.api.githubintegration.service.GithubClient;
import com.github.api.githubintegration.service.impl.GithubClientImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SystemConfig {

    @Bean
    public RestTemplate restTemplate() {
        final RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        return restTemplate;
    }
    @Bean
    public GithubClient githubClient(RestTemplate restTemplate){
        return new GithubClientImpl(restTemplate);
    }

}
