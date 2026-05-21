package com.attus.processojudicial.api.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ErroResponseDTO {
    private int status;
    private String erro;
    private String mensagem;
    private LocalDateTime timestamp;
    private List<String> detalhes;
}