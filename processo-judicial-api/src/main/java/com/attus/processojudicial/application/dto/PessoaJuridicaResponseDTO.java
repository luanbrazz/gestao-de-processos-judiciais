package com.attus.processojudicial.application.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PessoaJuridicaResponseDTO extends ParteResponseDTO {
    private String razaoSocial;
    private String cnae;
    private String naturezaJuridica;
    private String situacao;
}
