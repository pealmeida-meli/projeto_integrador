package com.mercadolibre.bootcamp.projeto_integrador.repository;

import com.mercadolibre.bootcamp.projeto_integrador.model.Batch;
import com.mercadolibre.bootcamp.projeto_integrador.model.BatchPurchaseOrder;
import com.mercadolibre.bootcamp.projeto_integrador.model.Buyer;
import com.mercadolibre.bootcamp.projeto_integrador.model.PurchaseOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface IBatchPurchaseOrderRepository extends PagingAndSortingRepository<BatchPurchaseOrder, Long> {
    Optional<BatchPurchaseOrder> findOneByPurchaseOrderAndBatch(PurchaseOrder purchaseOrder, Batch batch);

    Page<BatchPurchaseOrder> findAllByPurchaseOrder_BuyerAndPurchaseOrder_OrderStatus(Buyer buyer, String orderStatus, Pageable pageable);
}
