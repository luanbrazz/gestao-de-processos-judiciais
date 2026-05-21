package com.attus.processojudicial.application.dto;

import com.attus.processojudicial.domain.enums.StatusProcesso;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProcessoResponseDTO {
    private Long id;
    private String numero;
    private String assunto;
    private String vara;
    private StatusProcesso status;
    private LocalDate dataAbertura;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
    private List<ParteResponseDTO> partes;
    private List<MovimentacaoResponseDTO> movimentacoes;
}