package com.attus.processojudicial.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.br.CNPJ;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "DTO para cadastro de Pessoa Jurídica. Busca dados da empresa automaticamente via BrasilAPI.")
public class PessoaJuridicaRequestDTO extends ParteRequestDTO {

    @CNPJ
    @Schema(example = "12.345.678/0001-90", description = "CNPJ da empresa (validado). Dispara busca automática na BrasilAPI.")
    private String documento;
}
