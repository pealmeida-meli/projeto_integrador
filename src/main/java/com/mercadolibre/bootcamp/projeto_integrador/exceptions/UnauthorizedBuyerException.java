package com.mercadolibre.bootcamp.projeto_integrador.exceptions;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class UnauthorizedBuyerException extends CustomException {
    public UnauthorizedBuyerException(long buyerId, long purchaseId) {
        super("Id " + buyerId + " is not authorized.", "Id " + buyerId + " is not authorized to perform actions in " +
                "purchaseorder " + purchaseId + ".", HttpStatus.UNAUTHORIZED, LocalDateTime.now());
    }
}
