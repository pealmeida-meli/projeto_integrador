package com.mercadolibre.bootcamp.projeto_integrador.exceptions;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static java.lang.String.format;

public class EmptyStockException extends CustomException {
    public EmptyStockException(String productName) {
        super("Empty stock",
                format("The product %s doesn't have stock", productName),
                HttpStatus.NOT_FOUND,
                LocalDateTime.now());
    }
}
