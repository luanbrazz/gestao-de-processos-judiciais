package com.attus.processojudicial.api.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponseDTO> handleNaoEncontrado(RecursoNaoEncontradoException ex) {
        log.warn("Recurso não encontrado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErroResponseDTO(
                        HttpStatus.NOT_FOUND.value(),
                        "Não Encontrado",
                        ex.getMessage(),
                        LocalDateTime.now(),
                        null
                ));
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<ErroResponseDTO> handleRegraDeNegocio(RegraDeNegocioException ex) {
        log.warn("Regra de negócio violada: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ErroResponseDTO(
                        HttpStatus.UNPROCESSABLE_ENTITY.value(),
                        "Erro de Negócio",
                        ex.getMessage(),
                        LocalDateTime.now(),
                        null
                ));
    }

    @ExceptionHandler(ErroDeIntegracaoException.class)
    public ResponseEntity<ErroResponseDTO> handleIntegracao(ErroDeIntegracaoException ex) {
        log.error("Erro de integração: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(new ErroResponseDTO(
                        HttpStatus.BAD_GATEWAY.value(),
                        "Erro de Integração",
                        ex.getMessage(),
                        LocalDateTime.now(),
                        null
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponseDTO> handleValidacao(MethodArgumentNotValidException ex) {
        List<String> detalhes = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .toList();

        log.warn("Erro de validação: {}", detalhes);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErroResponseDTO(
                        HttpStatus.BAD_REQUEST.value(),
                        "Erro de Validação",
                        "Campos inválidos",
                        LocalDateTime.now(),
                        detalhes
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponseDTO> handleGenerico(Exception ex) {
        log.error("Erro inesperado: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErroResponseDTO(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Erro Interno",
                        "Ocorreu um erro inesperado",
                        LocalDateTime.now(),
                        null
                ));
    }
}