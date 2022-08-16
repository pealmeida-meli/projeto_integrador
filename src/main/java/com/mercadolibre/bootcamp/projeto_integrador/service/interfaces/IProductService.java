package com.mercadolibre.bootcamp.projeto_integrador.service.interfaces;

import com.mercadolibre.bootcamp.projeto_integrador.dto.ProductDetailsResponseDto;
import com.mercadolibre.bootcamp.projeto_integrador.dto.ProductResponseDto;

public interface IProductService {
    ProductResponseDto getWarehouses(long productId, long managerId);

    ProductDetailsResponseDto getProductDetails(long productId, long managerId, String orderBy);
}
