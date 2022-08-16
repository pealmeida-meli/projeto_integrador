package com.mercadolibre.bootcamp.projeto_integrador.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class PurchaseOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long purchaseId;
    private LocalDateTime date;
    private String orderStatus;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="buyer_id", nullable = false)
    @JsonIgnore
    private Buyer buyer;

    @OneToMany(mappedBy = "purchaseOrder", fetch = FetchType.LAZY)
    private List<BatchPurchaseOrder> batchPurchaseOrders;
    
}
