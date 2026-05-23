package com.attus.processojudicial.application.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class PessoaFisicaResponseDTO extends ParteResponseDTO {
    private LocalDate dataNascimento;
}
