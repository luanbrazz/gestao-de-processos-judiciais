package com.attus.processojudicial.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Configuration
public class ElasticsearchConfig {

    @Value("${spring.application.name}")
    private String appName;

    @PostConstruct
    public void verificarConexaoElasticsearch() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject("http://localhost:9200", String.class);
            log.info("Elasticsearch conectado com sucesso para app: {}", appName);
        } catch (Exception e) {
            log.warn("Elasticsearch não disponível: {} - logs locais serão usados", e.getMessage());
        }
    }
}