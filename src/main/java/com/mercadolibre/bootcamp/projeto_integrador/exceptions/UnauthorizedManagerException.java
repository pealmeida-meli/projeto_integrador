package com.mercadolibre.bootcamp.projeto_integrador.exceptions;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class UnauthorizedManagerException extends CustomException {
    public UnauthorizedManagerException(String managerName) {
        super(managerName + " is not authorized.", managerName + " is not authorized to perform this action.",
                HttpStatus.FORBIDDEN, LocalDateTime.now());
    }
}
