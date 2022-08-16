package com.mercadolibre.bootcamp.projeto_integrador.exceptions;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

public class IncompatibleCategoryException extends CustomException {
    public IncompatibleCategoryException(List<String> productNames) {
        super("Incompatible category",
                "The following products have incompatible category with the section: " + String.join(", ",
                        productNames), HttpStatus.BAD_REQUEST, LocalDateTime.now());
    }
}
