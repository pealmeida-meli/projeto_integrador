package com.mercadolibre.bootcamp.projeto_integrador.service;

import com.mercadolibre.bootcamp.projeto_integrador.dto.BatchRequestDto;
import com.mercadolibre.bootcamp.projeto_integrador.exceptions.IncompatibleCategoryException;
import com.mercadolibre.bootcamp.projeto_integrador.exceptions.MaxSizeException;
import com.mercadolibre.bootcamp.projeto_integrador.exceptions.NotFoundException;
import com.mercadolibre.bootcamp.projeto_integrador.exceptions.UnauthorizedManagerException;
import com.mercadolibre.bootcamp.projeto_integrador.model.Manager;
import com.mercadolibre.bootcamp.projeto_integrador.model.Product;
import com.mercadolibre.bootcamp.projeto_integrador.model.Section;
import com.mercadolibre.bootcamp.projeto_integrador.repository.ISectionRepository;
import com.mercadolibre.bootcamp.projeto_integrador.service.interfaces.IManagerService;
import com.mercadolibre.bootcamp.projeto_integrador.service.interfaces.IProductService;
import com.mercadolibre.bootcamp.projeto_integrador.service.interfaces.ISectionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SectionService implements ISectionService {
    private final ISectionRepository sectionRepository;
    private final IManagerService managerService;
    private final IProductService productService;

    public SectionService(ISectionRepository sectionRepository, IManagerService managerService,
                          IProductService productService) {
        this.sectionRepository = sectionRepository;
        this.managerService = managerService;
        this.productService = productService;
    }

    @Override
    public Section findById(long sectionCode) {
        return sectionRepository.findById(sectionCode).orElseThrow(() -> new NotFoundException("Section"));
    }

    @Override
    public Section update(Section section, List<BatchRequestDto> batchesToInsert, long managerId) {
        Map<Long, Product> products = productService.getProductMap(batchesToInsert);

        ensureManagerHasPermissionInSection(managerId, section);
        ensureSectionHasCompatibleCategory(section, products);
        ensureSectionHasSpace(section, batchesToInsert.size());

        sectionRepository.save(section);

        return section;
    }

    private void ensureSectionHasCompatibleCategory(Section section, Map<Long, Product> products) {
        List<Product> invalidProducts = products.values()
                .stream()
                .filter(product -> !product.getCategory().equals(section.getCategory()))
                .collect(Collectors.toList());

        List<String> productNames = invalidProducts.stream().map(Product::getProductName).collect(Collectors.toList());

        if (invalidProducts.size() > 0)
            throw new IncompatibleCategoryException(productNames);
    }

    private void ensureManagerHasPermissionInSection(long managerId, Section section) {
        Manager manager = managerService.findById(managerId);

        if (section.getManager().getManagerId() != managerId)
            throw new UnauthorizedManagerException(manager.getName());
    }

    private void ensureSectionHasSpace(Section section, int batchCount) {
        if (section.getAvailableSlots() < batchCount) {
            throw new MaxSizeException("Section");
        }
        section.setCurrentBatches(section.getCurrentBatches() + batchCount);
    }

}
