package com.attus.processojudicial.application.dto;

import com.attus.processojudicial.domain.enums.TipoParte;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "DTO base para cadastro de Parte. Use tipoPessoa='PESSOA_FISICA' ou 'PESSOA_JURIDICA'.")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "tipoPessoa", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PessoaFisicaRequestDTO.class, name = "PESSOA_FISICA"),
        @JsonSubTypes.Type(value = PessoaJuridicaRequestDTO.class, name = "PESSOA_JURIDICA")
})
public abstract class ParteRequestDTO {

    @NotNull
    @Schema(example = "REU", description = "Tipo da parte (AUTOR ou REU)", allowableValues = {"AUTOR", "REU"})
    private TipoParte tipo;

    @NotBlank
    @Schema(example = "João da Silva", description = "Nome completo ou razão social")
    private String nome;

    @NotNull
    @Schema(example = "PESSOA_FISICA", description = "Tipo de pessoa (PESSOA_FISICA ou PESSOA_JURIDICA)",
            allowableValues = {"PESSOA_FISICA", "PESSOA_JURIDICA"})
    private String tipoPessoa;
}
