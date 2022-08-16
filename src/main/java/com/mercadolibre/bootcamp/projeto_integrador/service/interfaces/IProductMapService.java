package com.mercadolibre.bootcamp.projeto_integrador.service.interfaces;

import com.mercadolibre.bootcamp.projeto_integrador.dto.BatchRequestDto;
import com.mercadolibre.bootcamp.projeto_integrador.service.ProductMapService;

import java.util.List;

public interface IProductMapService {
    ProductMapService.ProductMap getProductMap(List<BatchRequestDto> batchesDto);
}
