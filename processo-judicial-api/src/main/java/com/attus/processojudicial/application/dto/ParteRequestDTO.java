package com.attus.processojudicial.application.dto;

import com.attus.processojudicial.domain.enums.TipoParte;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ParteRequestDTO {
    @NotNull
    private TipoParte tipo;
    @NotBlank
    private String nome;
    @NotBlank
    private String documento;
    private String cep;
}