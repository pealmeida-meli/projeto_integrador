package com.mercadolibre.bootcamp.projeto_integrador.service;

import com.mercadolibre.bootcamp.projeto_integrador.dto.PaginatedResponseDto;
import com.mercadolibre.bootcamp.projeto_integrador.dto.PurchaseHistoryItemResponseDto;
import com.mercadolibre.bootcamp.projeto_integrador.exceptions.NotFoundException;
import com.mercadolibre.bootcamp.projeto_integrador.model.Buyer;
import com.mercadolibre.bootcamp.projeto_integrador.repository.IBatchPurchaseOrderRepository;
import com.mercadolibre.bootcamp.projeto_integrador.repository.IBuyerRepository;
import com.mercadolibre.bootcamp.projeto_integrador.service.interfaces.IPurchaseHistoryService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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
    public PaginatedResponseDto<PurchaseHistoryItemResponseDto> getHistory(long buyerId, Pageable pageable) {
        var buyer = tryFindBuyerById(buyerId);

        var orders = pageable.getSort().map(this::mapOrder).toList();
        var newPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(orders));

        var page = batchPurchaseOrderRepository.findAllByPurchaseOrder_BuyerAndPurchaseOrder_OrderStatus(buyer, "Closed", newPageable)
                .map(PurchaseHistoryItemResponseDto::new);

        return new PaginatedResponseDto<>(page);
    }

    private Buyer tryFindBuyerById(long buyerId) {
        return buyerRepository.findById(buyerId).orElseThrow(() -> new NotFoundException("Buyer"));
    }

    private Sort.Order mapOrder(Sort.Order order) {
        return new Sort.Order(
                order.getDirection(),
                PurchaseHistoryItemResponseDto.sortMap.getOrDefault(order.getProperty(), order.getProperty()),
                order.getNullHandling()
        );
    }
}
