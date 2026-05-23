package com.attus.processojudicial.application.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class MovimentacaoResponseDTO {
    private UUID id;
    private String descricao;
    private LocalDateTime dataMovimentacao;
}