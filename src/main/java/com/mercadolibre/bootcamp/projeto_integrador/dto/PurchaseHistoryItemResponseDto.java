package com.mercadolibre.bootcamp.projeto_integrador.dto;

import com.mercadolibre.bootcamp.projeto_integrador.model.BatchPurchaseOrder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
public class PurchaseHistoryItemResponseDto {
    public static final Map<String, String> sortMap = Map.of(
            "productName", "Batch_Product_productName",
            "productPrice", "Batch_productPrice",
            "sellerName", "Batch_Product_Seller_name",
            "sellerEmail", "Batch_Product_Seller_email",
            "timestamp", "PurchaseOrder_date",
            "quantity", "quantity");

    private final String productName;
    private final BigDecimal productPrice;
    private final String sellerName;
    private final String sellerEmail;
    private final LocalDateTime timestamp;
    private final int quantity;

    public PurchaseHistoryItemResponseDto(BatchPurchaseOrder batchPurchaseOrder) {
        productName = batchPurchaseOrder.getBatch().getProduct().getProductName();
        productPrice = batchPurchaseOrder.getBatch().getProductPrice();
        sellerName = batchPurchaseOrder.getBatch().getProduct().getSeller().getName();
        sellerEmail = batchPurchaseOrder.getBatch().getProduct().getSeller().getEmail();
        timestamp = batchPurchaseOrder.getPurchaseOrder().getDate();
        quantity = batchPurchaseOrder.getQuantity();
    }

    @SuppressWarnings("unused")
    public BigDecimal getTotal() {
        return productPrice.multiply(new BigDecimal(quantity));
    }
}
