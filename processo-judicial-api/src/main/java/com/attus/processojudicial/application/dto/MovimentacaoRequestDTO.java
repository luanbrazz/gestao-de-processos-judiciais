package com.attus.processojudicial.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "DTO para registro de movimentação em um processo")
public class MovimentacaoRequestDTO {

    @NotBlank
    @Schema(example = "Petição inicial protocolada na 3ª Vara",
            description = "Descrição detalhada da movimentação processual")
    private String descricao;
}