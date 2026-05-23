package com.attus.processojudicial.infrastructure.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProcessoEventConsumer {

    @KafkaListener(topics = "processo-criado", groupId = "processo-judicial-group")
    public void consumirProcessoCriado(ProcessoCriadoEvent event) {
        log.info("   Evento recebido via Kafka - Processo criado:");
        log.info("   ID: {}", event.getProcessoId());
        log.info("   Número: {}", event.getNumero());
        log.info("   Assunto: {}", event.getAssunto());
        log.info("   Vara: {}", event.getVara());
        log.info("   Status: {}", event.getStatus());
        log.info("   Criado em: {}", event.getCriadoEm());
    }
}