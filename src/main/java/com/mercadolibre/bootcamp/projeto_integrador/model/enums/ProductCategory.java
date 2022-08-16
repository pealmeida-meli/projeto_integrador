package com.mercadolibre.bootcamp.projeto_integrador.model.enums;

import lombok.Getter;

public enum ProductCategory {
    FRESH("FS"),
    CHILLED("RF"),
    FROZEN("FF");

    public static final String mysqlDefinition = "enum('FRESH', 'CHILLED', 'FROZEN')";

    @Getter
    private final String code;

    ProductCategory(String code) {
        this.code = code;
    }
}
