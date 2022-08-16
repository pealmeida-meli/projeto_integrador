package com.mercadolibre.bootcamp.projeto_integrador.exceptions;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class MaxSizeException extends CustomException {
    /**
     * Lança uma CustomException com HTTP Status 400.
     *
     * @param name
     * @throws CustomException
     */
    public MaxSizeException(String name) {
        super(name, name + " does not have enough space", HttpStatus.BAD_REQUEST, LocalDateTime.now());
    }
}
