package com.attus.processojudicial.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "pessoa_juridica")
@DiscriminatorValue("PESSOA_JURIDICA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PessoaJuridica extends Parte {

    @Column(nullable = false, length = 20)
    private String documento;

    @Column(name = "razao_social")
    private String razaoSocial;

    private String cnae;

    @Column(name = "natureza_juridica")
    private String naturezaJuridica;

    private String situacao;

    @Column(length = 10)
    private String cep;

    private String logradouro;
    private String bairro;
    private String cidade;

    @Column(length = 2)
    private String uf;
}
