package com.mercadolibre.bootcamp.projeto_integrador.dto;

import com.mercadolibre.bootcamp.projeto_integrador.model.BatchPurchaseOrder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class PurchaseHistoryResponseDto {
    private String productName;
    private BigDecimal productPrice;
    private String sellerName;
    private String sellerEmail;
    private LocalDateTime timestamp;
    private int quantity;

    public PurchaseHistoryResponseDto(BatchPurchaseOrder batchPurchaseOrder) {
        setProductName(batchPurchaseOrder.getBatch().getProduct().getProductName());
        setProductPrice(batchPurchaseOrder.getBatch().getProductPrice());
        setSellerName(batchPurchaseOrder.getBatch().getProduct().getSeller().getName());
        setSellerEmail(batchPurchaseOrder.getBatch().getProduct().getSeller().getEmail());
        setTimestamp(batchPurchaseOrder.getPurchaseOrder().getDate());
        setQuantity(batchPurchaseOrder.getQuantity());
    }

    @SuppressWarnings("unused")
    public BigDecimal getTotal() {
        return productPrice.multiply(new BigDecimal(quantity));
    }
}
