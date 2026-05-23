package com.attus.processojudicial.application.dto;

import com.attus.processojudicial.domain.enums.TipoParte;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "DTO para cadastro de Parte em um Processo Judicial")
public class ParteRequestDTO {

    @NotNull
    @Schema(example = "REU", description = "Tipo da parte (AUTOR ou REU)", allowableValues = {"AUTOR", "REU"})
    private TipoParte tipo;

    @NotBlank
    @Schema(example = "João da Silva", description = "Nome completo da parte")
    private String nome;

    @NotBlank
    @Schema(example = "123.456.789-00", description = "CPF ou CNPJ da parte")
    private String documento;

    @Schema(example = "12030145", description = "CEP para consulta automática de endereço via ViaCEP")
    private String cep;
}