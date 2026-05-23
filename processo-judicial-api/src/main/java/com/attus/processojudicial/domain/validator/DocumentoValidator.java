package com.attus.processojudicial.domain.validator;

import com.attus.processojudicial.api.exception.RegraDeNegocioException;

/**
 * Validador de documentos brasileiros (CPF e CNPJ).
 * Verifica os dígitos verificadores pelo algoritmo oficial da Receita Federal.
 * Lança {@link RegraDeNegocioException} (HTTP 422) para documentos inválidos.
 */
public final class DocumentoValidator {

    private DocumentoValidator() {}


    /**
     * Valida CPF pelo algoritmo dos dígitos verificadores.
     * Aceita CPF com ou sem formatação (ex: "123.456.789-09" ou "12345678909").
     */
    public static void validarCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            throw new RegraDeNegocioException("CPF não informado");
        }

        String digitos = cpf.replaceAll("[^0-9]", "");

        if (digitos.length() != 11) {
            throw new RegraDeNegocioException(
                    "CPF inválido: deve conter 11 dígitos numéricos (informado: " + digitos.length() + ")");
        }

        if (todosDigitosIguais(digitos)) {
            throw new RegraDeNegocioException(
                    "CPF inválido: sequência de dígitos repetidos não é permitida");
        }

        int primeiroDigito = calcularDigitoVerificadorCpf(digitos, 9, 10);
        if (primeiroDigito != Character.getNumericValue(digitos.charAt(9))) {
            throw new RegraDeNegocioException("CPF inválido: dígito verificador incorreto");
        }

        int segundoDigito = calcularDigitoVerificadorCpf(digitos, 10, 11);
        if (segundoDigito != Character.getNumericValue(digitos.charAt(10))) {
            throw new RegraDeNegocioException("CPF inválido: dígito verificador incorreto");
        }
    }

    private static int calcularDigitoVerificadorCpf(String digitos, int tamanho, int peso) {
        int soma = 0;
        for (int i = 0; i < tamanho; i++) {
            soma += Character.getNumericValue(digitos.charAt(i)) * (peso - i);
        }
        int resultado = 11 - (soma % 11);
        return resultado >= 10 ? 0 : resultado;
    }

    /**
     * Valida CNPJ pelo algoritmo dos dígitos verificadores.
     * Aceita CNPJ com ou sem formatação (ex: "12.345.678/0001-90" ou "12345678000190").
     */
    public static void validarCnpj(String cnpj) {
        if (cnpj == null || cnpj.isBlank()) {
            throw new RegraDeNegocioException("CNPJ não informado");
        }

        String digitos = cnpj.replaceAll("[^0-9]", "");

        if (digitos.length() != 14) {
            throw new RegraDeNegocioException(
                    "CNPJ inválido: deve conter 14 dígitos numéricos (informado: " + digitos.length() + ")");
        }

        if (todosDigitosIguais(digitos)) {
            throw new RegraDeNegocioException(
                    "CNPJ inválido: sequência de dígitos repetidos não é permitida");
        }

        int primeiroDigito = calcularDigitoVerificadorCnpj(digitos, new int[]{5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2});
        if (primeiroDigito != Character.getNumericValue(digitos.charAt(12))) {
            throw new RegraDeNegocioException("CNPJ inválido: dígito verificador incorreto");
        }

        int segundoDigito = calcularDigitoVerificadorCnpj(digitos, new int[]{6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2});
        if (segundoDigito != Character.getNumericValue(digitos.charAt(13))) {
            throw new RegraDeNegocioException("CNPJ inválido: dígito verificador incorreto");
        }
    }

    private static int calcularDigitoVerificadorCnpj(String digitos, int[] pesos) {
        int soma = 0;
        for (int i = 0; i < pesos.length; i++) {
            soma += Character.getNumericValue(digitos.charAt(i)) * pesos[i];
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    private static boolean todosDigitosIguais(String digitos) {
        return digitos.chars().distinct().count() == 1;
    }
}
