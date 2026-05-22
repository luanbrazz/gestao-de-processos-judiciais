package com.attus.processojudicial.infrastructure.client;

import com.attus.processojudicial.application.dto.EnderecoDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ViaCepService {

    private static final String CIRCUIT_BREAKER_NAME = "viacep";
    private static final String CACHE_NAME = "enderecos";

    private final ViaCepClient viaCepClient;

    @Cacheable(value = CACHE_NAME, key = "#cep")
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "fallbackEndereco")
    @Retry(name = CIRCUIT_BREAKER_NAME)
    public EnderecoDTO buscarEndereco(String cep) {
        log.info("Buscando endereço para CEP: {} (sem cache)", cep);
        String cepLimpo = cep.replaceAll("-", "");
        return viaCepClient.buscarEnderecoPorCep(cepLimpo);
    }

    public EnderecoDTO fallbackEndereco(String cep, Exception ex) {
        log.warn("Circuit Breaker ativado para CEP: {}. Causa: {}", cep, ex.getMessage());
        EnderecoDTO fallback = new EnderecoDTO();
        fallback.setCep(cep);
        return fallback;
    }
}