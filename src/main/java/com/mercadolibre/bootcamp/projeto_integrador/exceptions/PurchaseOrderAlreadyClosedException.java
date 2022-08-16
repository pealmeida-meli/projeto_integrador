package com.mercadolibre.bootcamp.projeto_integrador.exceptions;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class PurchaseOrderAlreadyClosedException extends CustomException {
    /**
     * Lança uma CustomException com HTTP Status 400.
     *
     * @param name
     * @throws CustomException
     */
    public PurchaseOrderAlreadyClosedException(long id) {
        super("Purchase Order", "Purchase order with id " + id + " is already closed", HttpStatus.BAD_REQUEST,
                LocalDateTime.now());
    }
}
