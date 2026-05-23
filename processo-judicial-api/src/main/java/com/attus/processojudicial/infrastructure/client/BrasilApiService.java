package com.attus.processojudicial.infrastructure.client;

import com.attus.processojudicial.application.dto.CnpjDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrasilApiService {

    private static final String CIRCUIT_BREAKER_NAME = "brasilapi";
    private static final String CACHE_NAME = "cnpjs";

    private final BrasilApiClient brasilApiClient;

    @Cacheable(value = CACHE_NAME, key = "#cnpj")
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "fallbackCnpj")
    @Retry(name = CIRCUIT_BREAKER_NAME)
    public CnpjDTO buscarCnpj(String cnpj) {
        log.info("Buscando dados do CNPJ: {} (sem cache)", cnpj);
        String cnpjLimpo = cnpj.replaceAll("[.\\-/]", "");
        return brasilApiClient.buscarCnpj(cnpjLimpo);
    }

    public CnpjDTO fallbackCnpj(String cnpj, Exception ex) {
        log.warn("Circuit Breaker ativado para CNPJ: {}. Causa: {}", cnpj, ex.getMessage());
        CnpjDTO fallback = new CnpjDTO();
        fallback.setCnpj(cnpj);
        return fallback;
    }
}
