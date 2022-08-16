package com.mercadolibre.bootcamp.projeto_integrador.controller;

import com.mercadolibre.bootcamp.projeto_integrador.dto.BatchBuyerResponseDto;
import com.mercadolibre.bootcamp.projeto_integrador.service.interfaces.IBatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fresh-products")
public class FreshProductsController {
    @Autowired
    private IBatchService batchService;

    @GetMapping
    public List<BatchBuyerResponseDto> findBatches(@RequestParam(required = false) String category) {
        return category != null ? batchService.findBatchByCategory(category) : batchService.findAll();
    }
}
