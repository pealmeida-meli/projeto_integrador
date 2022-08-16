package com.mercadolibre.bootcamp.projeto_integrador.controller;

import com.mercadolibre.bootcamp.projeto_integrador.dto.ProductDetailsResponseDto;
import com.mercadolibre.bootcamp.projeto_integrador.dto.ProductResponseDto;
import com.mercadolibre.bootcamp.projeto_integrador.service.interfaces.IProductService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class ProductController {
    private final IProductService service;

    public ProductController(IProductService service) {
        this.service = service;
    }

    @GetMapping("/fresh-products/warehouse")
    public ProductResponseDto getWarehouses(@RequestParam long productId,
                                            @RequestHeader("Manager-Id") long managerId) {
        return service.getWarehouses(productId, managerId);
    }

    @GetMapping("/fresh-products/list")
    public ProductDetailsResponseDto getProductDetails(@RequestParam long productId,
                                                       @RequestParam(required = false) String orderBy,
                                                       @RequestHeader("Manager-Id") long managerId) {
        return service.getProductDetails(productId, managerId, orderBy);
    }
}
