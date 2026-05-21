package com.attus.processojudicial.application.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MovimentacaoResponseDTO {
    private Long id;
    private String descricao;
    private LocalDateTime dataMovimentacao;
}