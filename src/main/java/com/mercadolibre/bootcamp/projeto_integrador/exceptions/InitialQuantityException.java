package com.mercadolibre.bootcamp.projeto_integrador.exceptions;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class InitialQuantityException extends CustomException {
    /**
     * Lança uma CustomException com HTTP Status 400.
     *
     * @param newInitialQuantity
     * @param selledProductsQuantity
     * @throws CustomException
     */
    public InitialQuantityException(int newInitialQuantity, int selledProductsQuantity) {
        super("Invalid batch quantity",
                "Couldn't update batch initial quantity. New initial quantity: " + newInitialQuantity +
                ". Selled products: " + selledProductsQuantity, HttpStatus.BAD_REQUEST, LocalDateTime.now());
    }
}
