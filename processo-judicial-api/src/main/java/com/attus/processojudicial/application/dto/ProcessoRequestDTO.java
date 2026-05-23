package com.attus.processojudicial.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "DTO para criação e atualização de Processo Judicial")
public class ProcessoRequestDTO {

    @NotBlank
    @Schema(example = "0001234-55.2026.8.26.0100", description = "Número único do processo no padrão CNJ")
    private String numero;

    @NotBlank
    @Schema(example = "Execução Fiscal - IPTU", description = "Assunto principal do processo")
    private String assunto;

    @NotBlank
    @Schema(example = "3ª Vara da Fazenda Pública", description = "Vara onde o processo tramita")
    private String vara;

    @NotNull
    @Schema(example = "2026-05-20", description = "Data de abertura do processo")
    private LocalDate dataAbertura;
}