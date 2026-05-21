package com.attus.processojudicial.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MovimentacaoRequestDTO {
    @NotBlank
    private String descricao;
}