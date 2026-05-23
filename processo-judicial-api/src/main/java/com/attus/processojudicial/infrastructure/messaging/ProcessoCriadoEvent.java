package com.attus.processojudicial.infrastructure.messaging;

import com.attus.processojudicial.domain.enums.StatusProcesso;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessoCriadoEvent {
    private UUID processoId;
    private String numero;
    private String assunto;
    private String vara;
    private StatusProcesso status;
    private LocalDateTime criadoEm;
}