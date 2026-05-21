package com.attus.processojudicial.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ProcessoRequestDTO {
    @NotBlank
    private String numero;
    @NotBlank
    private String assunto;
    @NotBlank
    private String vara;
    @NotNull
    private LocalDate dataAbertura;
}