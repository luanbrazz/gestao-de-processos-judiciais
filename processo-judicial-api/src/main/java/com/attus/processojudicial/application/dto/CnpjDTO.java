package com.attus.processojudicial.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CnpjDTO {

    private String cnpj;

    @JsonProperty("razao_social")
    private String razaoSocial;

    @JsonProperty("nome_fantasia")
    private String nomeFantasia;

    @JsonProperty("natureza_juridica")
    private String naturezaJuridica;

    @JsonProperty("cnae_fiscal_descricao")
    private String cnaeFiscalDescricao;

    @JsonProperty("cnae_fiscal")
    private Integer cnaeFiscal;

    @JsonProperty("descricao_situacao_cadastral")
    private String descricaoSituacaoCadastral;

    private String cep;
    private String logradouro;
    private String bairro;
    private String municipio;
    private String uf;

    @JsonProperty("numero")
    private String numero;

    @JsonProperty("complemento")
    private String complemento;
}
