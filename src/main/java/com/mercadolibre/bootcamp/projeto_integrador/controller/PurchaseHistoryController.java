package com.mercadolibre.bootcamp.projeto_integrador.controller;

import com.mercadolibre.bootcamp.projeto_integrador.dto.PaginatedResponseDto;
import com.mercadolibre.bootcamp.projeto_integrador.dto.PurchaseHistoryItemResponseDto;
import com.mercadolibre.bootcamp.projeto_integrador.service.interfaces.IPurchaseHistoryService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class PurchaseHistoryController {
    private final IPurchaseHistoryService service;

    public PurchaseHistoryController(IPurchaseHistoryService service) {
        this.service = service;
    }

    @GetMapping("purchase-history")
    public PaginatedResponseDto<PurchaseHistoryItemResponseDto> getPurchaseHistory(@RequestHeader("Buyer-Id") long buyerId, Pageable pageable) {
        return service.getHistory(buyerId, pageable);
    }
}
