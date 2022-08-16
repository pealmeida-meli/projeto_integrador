package com.mercadolibre.bootcamp.projeto_integrador.service;

import com.mercadolibre.bootcamp.projeto_integrador.dto.BatchResponseDto;
import com.mercadolibre.bootcamp.projeto_integrador.dto.ProductDetailsResponseDto;
import com.mercadolibre.bootcamp.projeto_integrador.dto.ProductResponseDto;
import com.mercadolibre.bootcamp.projeto_integrador.dto.WarehouseResponseDto;
import com.mercadolibre.bootcamp.projeto_integrador.exceptions.BadRequestException;
import com.mercadolibre.bootcamp.projeto_integrador.exceptions.EmptyStockException;
import com.mercadolibre.bootcamp.projeto_integrador.exceptions.NotFoundException;
import com.mercadolibre.bootcamp.projeto_integrador.model.Batch;
import com.mercadolibre.bootcamp.projeto_integrador.model.Product;
import com.mercadolibre.bootcamp.projeto_integrador.repository.IBatchRepository;
import com.mercadolibre.bootcamp.projeto_integrador.repository.IProductRepository;
import com.mercadolibre.bootcamp.projeto_integrador.service.interfaces.IManagerService;
import com.mercadolibre.bootcamp.projeto_integrador.service.interfaces.IProductService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService implements IProductService {
    private final IProductRepository productRepository;
    private final IBatchRepository batchRepository;
    private final IManagerService managerService;

    public ProductService(IProductRepository productRepository, IBatchRepository batchRepository,
                          IManagerService managerService) {
        this.productRepository = productRepository;
        this.batchRepository = batchRepository;
        this.managerService = managerService;
    }

    /**
     * Metodo que retorna todos armazens que contenham um determinado item com as quantidades totais.
     *
     * @param productId long representando o id do produto
     * @return ProductResponseDto contendo o id do produto com uma lista de códigos de armazens com quantidades do
     * produto.
     */
    @Override
    public ProductResponseDto getWarehouses(long productId, long managerId) {
        managerService.findById(managerId);
        List<Batch> batchList =
                batchRepository.findAllByProduct(productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Product")));
        List<WarehouseResponseDto> warehouses = new ArrayList<>();

        batchList.stream()
                .collect(Collectors.groupingBy(b -> b.getInboundOrder().getSection().getWarehouse().getWarehouseCode(),
                        Collectors.summingInt(Batch::getCurrentQuantity)))
                .forEach((warehouseCode, totalQuantity) -> warehouses.add(new WarehouseResponseDto(warehouseCode,
                        totalQuantity)));
        return new ProductResponseDto(productId, warehouses);
    }

    /**
     * Método que retorna os detalhes do produto.
     *
     * @param productId ID do produto
     * @param managerId ID do representante
     * @return Detalhes do produto
     */
    @Override
    public ProductDetailsResponseDto getProductDetails(long productId, long managerId, String orderBy) {
        ensureManagerExists(managerId);
        Product product = productRepository.findById(productId).orElseThrow(() -> new NotFoundException("product"));
        List<BatchResponseDto> batches = batchRepository.findAllByProduct(product)
                .stream()
                .filter(batch -> batch.getInboundOrder().getSection().getManager().getManagerId() == managerId)
                .map(BatchResponseDto::new)
                .collect(Collectors.toList());

        if (batches.isEmpty())
            throw new EmptyStockException(product.getProductName());

        return new ProductDetailsResponseDto(productId, sortBatches(batches, orderBy));
    }

    private List<BatchResponseDto> sortBatches(List<BatchResponseDto> batches, String orderBy) throws BadRequestException {
        if (StringUtils.isBlank(orderBy))
            return batches;

        switch (orderBy.toUpperCase()) {
            case "L":
                return batches.stream()
                        .sorted(Comparator.comparing(BatchResponseDto::getBatchNumber))
                        .collect(Collectors.toList());
            case "Q":
                return batches.stream()
                        .sorted(Comparator.comparing(BatchResponseDto::getCurrentQuantity))
                        .collect(Collectors.toList());
            case "V":
                return batches.stream()
                        .sorted(Comparator.comparing(BatchResponseDto::getDueDate))
                        .collect(Collectors.toList());
            default:
                throw new BadRequestException("Parâmetro de ordenação inválido. L: batchNumber, Q: currentQuantity, " +
                        "V: dueDate");
        }
    }

    private void ensureManagerExists(long managerId) {
        managerService.findById(managerId);
    }
}
