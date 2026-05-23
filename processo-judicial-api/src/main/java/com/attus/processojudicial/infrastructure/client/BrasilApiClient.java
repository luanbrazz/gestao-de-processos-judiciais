package com.attus.processojudicial.infrastructure.client;

import com.attus.processojudicial.application.dto.CnpjDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "brasilapi", url = "https://brasilapi.com.br/api")
public interface BrasilApiClient {

    @GetMapping("/cnpj/v1/{cnpj}")
    CnpjDTO buscarCnpj(@PathVariable("cnpj") String cnpj);
}
