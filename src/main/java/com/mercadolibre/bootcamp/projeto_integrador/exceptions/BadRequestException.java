package com.mercadolibre.bootcamp.projeto_integrador.exceptions;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class BadRequestException extends CustomException {
    /**
     * Lança uma CustomException com HTTP Status 400.
     *
     * @param message
     * @throws CustomException
     */
    public BadRequestException(String message) {
        super("Bad request", message, HttpStatus.BAD_REQUEST, LocalDateTime.now());
    }
}
