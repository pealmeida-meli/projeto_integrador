package com.mercadolibre.bootcamp.projeto_integrador.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mercadolibre.bootcamp.projeto_integrador.model.enums.ProductCategory;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long sectionCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_code")
    private Warehouse warehouse;

    @Column(columnDefinition = ProductCategory.mysqlDefinition)
    @Enumerated(EnumType.STRING)
    private ProductCategory category;

    private int maxBatches;

    private int currentBatches;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Manager manager;

    public int getAvailableSlots() {
        return maxBatches - currentBatches;
    }

}
