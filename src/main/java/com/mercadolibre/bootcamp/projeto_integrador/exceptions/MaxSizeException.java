package com.mercadolibre.bootcamp.projeto_integrador.exceptions;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class MaxSizeException extends CustomException {
    public MaxSizeException(String name) {
        super(name, name + " does not have enough space", HttpStatus.BAD_REQUEST, LocalDateTime.now());
    }
}
