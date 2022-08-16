package com.mercadolibre.bootcamp.projeto_integrador.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class BatchPurchaseOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long batchPurchaseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id_purchase_id")
    @JsonIgnore
    private PurchaseOrder purchaseOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_number_batch_number")
    private Batch batch;

    @Column(precision = 9, scale = 2)
    private BigDecimal unitPrice;

    private int quantity;
}
