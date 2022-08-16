package com.mercadolibre.bootcamp.projeto_integrador.controller;

import com.mercadolibre.bootcamp.projeto_integrador.dto.PurchaseHistoryResponseDto;
import com.mercadolibre.bootcamp.projeto_integrador.service.interfaces.IPurchaseHistoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class PurchaseHistoryController {
    private final IPurchaseHistoryService service;

    public PurchaseHistoryController(IPurchaseHistoryService service) {
        this.service = service;
    }

    @GetMapping("purchase-history")
    public List<PurchaseHistoryResponseDto> getPurchaseHistory(@RequestHeader("Buyer-Id") long buyerId) {
        return service.getHistory(buyerId);
    }
}
