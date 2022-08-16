package com.mercadolibre.bootcamp.projeto_integrador.exceptions;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class ManagerNotFoundException extends CustomException {
    public ManagerNotFoundException(long id) {
        super("Manager not found", "Manager with id " + id + " not found", HttpStatus.NOT_FOUND, LocalDateTime.now());
    }
}
