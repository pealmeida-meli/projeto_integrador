package com.mercadolibre.bootcamp.projeto_integrador.service;

import com.mercadolibre.bootcamp.projeto_integrador.dto.BatchRequestDto;
import com.mercadolibre.bootcamp.projeto_integrador.exceptions.NotFoundException;
import com.mercadolibre.bootcamp.projeto_integrador.model.Product;
import com.mercadolibre.bootcamp.projeto_integrador.repository.IProductRepository;
import com.mercadolibre.bootcamp.projeto_integrador.service.interfaces.IProductMapService;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequestScope
public class ProductMapService implements IProductMapService {
    private final IProductRepository productRepository;

    public ProductMapService(IProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Retorna mapa de produtos por ID
     *
     * @param batchesDto Lotes enviados no pedido de entrada
     * @return Mapa de produtos com identificador como chave
     */
    @Override
    public ProductMap getProductMap(List<BatchRequestDto> batchesDto) {
        Map<Long, Product> map = productRepository
                .findAllById(batchesDto.stream().map(BatchRequestDto::getProductId).collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(Product::getProductId, product -> product));

        return new ProductMap(map);
    }

    public static class ProductMap {
        private final Map<Long, Product> map;

        private ProductMap(@NonNull Map<Long, Product> map) {
            this.map = map;
        }

        public Product get(Long id) {
            Product product = map.get(id);
            if (product == null)
                throw new NotFoundException("product");
            return product;
        }

        public Collection<Product> values() {
            return map.values();
        }
    }
}