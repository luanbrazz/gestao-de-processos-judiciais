package com.attus.processojudicial.application.dto;

import com.attus.processojudicial.domain.enums.TipoParte;
import lombok.Data;

import java.util.UUID;

@Data
public class ParteResponseDTO {
    private UUID id;
    private TipoParte tipo;
    private String nome;
    private String documento;
    private String cep;
    private String logradouro;
    private String bairro;
    private String cidade;
    private String uf;
}