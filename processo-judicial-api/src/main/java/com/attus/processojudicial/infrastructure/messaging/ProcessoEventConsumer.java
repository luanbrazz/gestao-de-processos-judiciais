package com.attus.processojudicial.infrastructure.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProcessoEventConsumer {

    @KafkaListener(topics = "processo-criado", groupId = "processo-judicial-group")
    public void consumirProcessoCriado(ProcessoCriadoEvent event) {
        try {
            log.info("✅ EVENTO Kafka [ProcessoCriado] recebido → ID: {} | Número: {} | Assunto: '{}' | Vara: {} | Status: {} | Criado em: {}",
                    event.getProcessoId(),
                    event.getNumero(),
                    event.getAssunto(),
                    event.getVara(),
                    event.getStatus(),
                    event.getCriadoEm());

            log.debug("Detalhes completos do evento: {}", event);

        } catch (Exception e) {
            log.error("❌ Erro ao processar evento ProcessoCriado do Kafka | ID: {}",
                    event != null ? event.getProcessoId() : "N/A", e);
        }
    }
}