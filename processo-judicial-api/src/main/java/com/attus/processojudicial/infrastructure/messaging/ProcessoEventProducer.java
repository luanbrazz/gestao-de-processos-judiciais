package com.attus.processojudicial.infrastructure.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProcessoEventProducer {

    private static final String TOPIC = "processo-criado";

    private final KafkaTemplate<String, ProcessoCriadoEvent> kafkaTemplate;

    public void publicarProcessoCriado(ProcessoCriadoEvent event) {
        log.info("Publicando evento processo-criado para processo: {}", event.getNumero());
        kafkaTemplate.send(TOPIC, event.getProcessoId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Evento publicado com sucesso. Tópico: {}, Offset: {}",
                                TOPIC, result.getRecordMetadata().offset());
                    } else {
                        log.error("Falha ao publicar evento para processo: {}", event.getNumero(), ex);
                    }
                });
    }
}