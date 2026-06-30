package com.burguer.restaurant.controller;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.burguer.restaurant.dto.RespostaErroApi;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<RespostaErroApi> tratarRecursoNaoEncontrado(NoSuchElementException excecao) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new RespostaErroApi(
                        OffsetDateTime.now(),
                        HttpStatus.NOT_FOUND.value(),
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        excecao.getMessage(),
                        Map.of()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<RespostaErroApi> tratarRegraNegocio(IllegalArgumentException excecao) {
        return ResponseEntity.badRequest().body(
                new RespostaErroApi(
                        OffsetDateTime.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        excecao.getMessage(),
                        Map.of()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespostaErroApi> tratarValidacao(MethodArgumentNotValidException excecao) {
        Map<String, String> errosCampos = excecao.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        erroCampo -> erroCampo.getDefaultMessage() == null ? "valor invalido" : erroCampo.getDefaultMessage(),
                        (first, second) -> first));

        return ResponseEntity.badRequest().body(
                new RespostaErroApi(
                        OffsetDateTime.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        "Falha de validacao",
                        errosCampos));
    }
}
