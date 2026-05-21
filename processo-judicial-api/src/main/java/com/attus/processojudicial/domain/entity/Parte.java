package com.attus.processojudicial.domain.entity;

import com.attus.processojudicial.domain.enums.TipoParte;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "parte")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Parte {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processo_id", nullable = false)
    private Processo processo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoParte tipo;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, length = 20)
    private String documento;

    @Column(length = 10)
    private String cep;

    private String logradouro;
    private String bairro;
    private String cidade;

    @Column(length = 2)
    private String uf;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    public void prePersist() {
        criadoEm = LocalDateTime.now();
    }
}