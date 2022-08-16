package com.mercadolibre.bootcamp.projeto_integrador.exceptions;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class BadRequestException extends CustomException {
    public BadRequestException(String message) {
        super("Bad request", message, HttpStatus.BAD_REQUEST, LocalDateTime.now());
    }
}
