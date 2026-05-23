package com.attus.processojudicial.application.dto;

import com.attus.processojudicial.domain.enums.TipoParte;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

import java.util.UUID;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "tipoPessoa", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PessoaFisicaResponseDTO.class, name = "PESSOA_FISICA"),
        @JsonSubTypes.Type(value = PessoaJuridicaResponseDTO.class, name = "PESSOA_JURIDICA")
})
public abstract class ParteResponseDTO {
    private UUID id;
    private TipoParte tipo;
    private String nome;
    private String tipoPessoa;
    private String documento;
    private String cep;
    private String logradouro;
    private String bairro;
    private String cidade;
    private String uf;
}
