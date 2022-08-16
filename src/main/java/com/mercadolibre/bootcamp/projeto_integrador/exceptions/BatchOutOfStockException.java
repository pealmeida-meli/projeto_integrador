package com.mercadolibre.bootcamp.projeto_integrador.exceptions;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class BatchOutOfStockException extends CustomException {
    public BatchOutOfStockException(long id) {
        super("Batch", "Batch with id " + id + " is out of stock", HttpStatus.BAD_REQUEST, LocalDateTime.now());
    }
}
