package com.mercadolibre.bootcamp.projeto_integrador.util;

import com.mercadolibre.bootcamp.projeto_integrador.dto.ProductResponseDto;
import com.mercadolibre.bootcamp.projeto_integrador.model.Product;
import com.mercadolibre.bootcamp.projeto_integrador.model.enums.ProductCategory;

public class ProductsGenerator {
    public static Product newProductFresh() {
        return Product.builder()
                .productName("Maça")
                .brand("Nacional")
                .category(ProductCategory.FRESH)
                .build();
    }

    public static Product newProductChilled() {
        return Product.builder()
                .productName("Iogurte")
                .brand("Holandês")
                .category(ProductCategory.CHILLED)
                .build();
    }

    public static Product newProductFrozen() {
        return Product.builder()
                .productName("Açaí")
                .brand("Frooty")
                .category(ProductCategory.FROZEN)
                .build();
    }

    public static ProductResponseDto newProductResponseDto() {
        ProductResponseDto product = new ProductResponseDto(1, WarehouseGenerator.newListWarehouseResponseDto());
        return product;
    }
}
