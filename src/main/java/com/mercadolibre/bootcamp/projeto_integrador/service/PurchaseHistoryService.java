package com.mercadolibre.bootcamp.projeto_integrador.service;

import com.mercadolibre.bootcamp.projeto_integrador.dto.PurchaseHistoryResponseDto;
import com.mercadolibre.bootcamp.projeto_integrador.exceptions.NotFoundException;
import com.mercadolibre.bootcamp.projeto_integrador.model.Buyer;
import com.mercadolibre.bootcamp.projeto_integrador.repository.IBatchPurchaseOrderRepository;
import com.mercadolibre.bootcamp.projeto_integrador.repository.IBuyerRepository;
import com.mercadolibre.bootcamp.projeto_integrador.service.interfaces.IPurchaseHistoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PurchaseHistoryService implements IPurchaseHistoryService {
    private final IBatchPurchaseOrderRepository batchPurchaseOrderRepository;
    private final IBuyerRepository buyerRepository;

    public PurchaseHistoryService(IBatchPurchaseOrderRepository batchPurchaseOrderRepository,
                                  IBuyerRepository buyerRepository) {
        this.batchPurchaseOrderRepository = batchPurchaseOrderRepository;
        this.buyerRepository = buyerRepository;
    }

    @Override
    public List<PurchaseHistoryResponseDto> getHistory(long buyerId) {
        var buyer = tryFindBuyerById(buyerId);
        return batchPurchaseOrderRepository.findAllByPurchaseOrder_BuyerAndPurchaseOrder_OrderStatusOrderByPurchaseOrder_Date(buyer, "Closed")
                .stream()
                .map(PurchaseHistoryResponseDto::new)
                .collect(Collectors.toList());
    }

    private Buyer tryFindBuyerById(long buyerId) {
        return buyerRepository.findById(buyerId).orElseThrow(() -> new NotFoundException("Buyer"));
    }
}
