package com.mercadolibre.bootcamp.projeto_integrador.util;

import com.mercadolibre.bootcamp.projeto_integrador.model.Manager;
import com.mercadolibre.bootcamp.projeto_integrador.model.Section;
import com.mercadolibre.bootcamp.projeto_integrador.model.Warehouse;
import com.mercadolibre.bootcamp.projeto_integrador.model.enums.ProductCategory;

public class SectionGenerator {
    public static Section getFreshSection(Warehouse warehouse, Manager manager) {
        Section section = new Section();
        section.setCurrentBatches(1);
        section.setCategory(ProductCategory.FRESH);
        section.setWarehouse(warehouse);
        section.setManager(manager);
        section.setMaxBatches(10);
        return section;
    }

    public static Section getFreshSectionWith1SlotAvailable() {
        return Section.builder()
                .sectionCode(1)
                .category(ProductCategory.FRESH)
                .maxBatches(8)
                .currentBatches(7)
                .manager(ManagerGenerator.getManagerWithId())
                .warehouse(WarehouseGenerator.newWarehouse())
                .build();
    }

    public static Section getFreshSectionWith10SlotsAvailable() {
        return Section.builder()
                .sectionCode(2)
                .category(ProductCategory.FRESH)
                .maxBatches(10)
                .currentBatches(0)
                .manager(ManagerGenerator.getManagerWithId())
                .warehouse(WarehouseGenerator.newWarehouse())
                .build();
    }

    public static Section getCrowdedFreshSection() {
        return Section.builder()
                .sectionCode(3)
                .category(ProductCategory.FRESH)
                .maxBatches(20)
                .currentBatches(20)
                .manager(ManagerGenerator.getManagerWithId())
                .warehouse(WarehouseGenerator.newWarehouse())
                .build();
    }
}
