package com.attus.processojudicial.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(name = "pessoa_fisica")
@DiscriminatorValue("PESSOA_FISICA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PessoaFisica extends Parte {

    @Column(nullable = false, length = 20)
    private String documento;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Column(length = 10)
    private String cep;

    private String logradouro;
    private String bairro;
    private String cidade;

    @Column(length = 2)
    private String uf;
}
