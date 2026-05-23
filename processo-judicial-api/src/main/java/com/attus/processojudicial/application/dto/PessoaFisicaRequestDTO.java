package com.attus.processojudicial.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "DTO para cadastro de Pessoa Física. Busca endereço automaticamente via ViaCEP.")
public class PessoaFisicaRequestDTO extends ParteRequestDTO {

    @CPF
    @Schema(example = "123.456.789-09", description = "CPF da pessoa física (validado pelo algoritmo da Receita Federal)")
    private String documento;

    @NotNull(message = "Data de nascimento é obrigatória para Pessoa Física")
    @Past(message = "Data de nascimento deve ser uma data no passado")
    @Schema(example = "1990-01-15", description = "Data de nascimento — obrigatória. A parte deve ter 18 anos ou mais.")
    private LocalDate dataNascimento;

    @Schema(example = "12030145", description = "CEP para busca automática de endereço via ViaCEP (opcional)")
    private String cep;
}
