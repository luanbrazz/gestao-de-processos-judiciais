package com.attus.processojudicial.api.exception;

public class ErroDeIntegracaoException extends RuntimeException {
    public ErroDeIntegracaoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}