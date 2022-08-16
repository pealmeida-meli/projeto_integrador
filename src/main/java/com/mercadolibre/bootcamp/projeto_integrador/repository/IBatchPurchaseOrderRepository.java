package com.mercadolibre.bootcamp.projeto_integrador.repository;

import com.mercadolibre.bootcamp.projeto_integrador.model.Batch;
import com.mercadolibre.bootcamp.projeto_integrador.model.BatchPurchaseOrder;
import com.mercadolibre.bootcamp.projeto_integrador.model.Buyer;
import com.mercadolibre.bootcamp.projeto_integrador.model.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IBatchPurchaseOrderRepository extends JpaRepository<BatchPurchaseOrder, Long> {
    Optional<BatchPurchaseOrder> findOneByPurchaseOrderAndBatch(PurchaseOrder purchaseOrder, Batch batch);
    List<BatchPurchaseOrder> findAllByPurchaseOrder_BuyerAndPurchaseOrder_OrderStatusOrderByPurchaseOrder_Date(Buyer buyer, String orderStatus);
}
