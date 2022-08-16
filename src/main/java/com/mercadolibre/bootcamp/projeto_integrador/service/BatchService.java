package com.mercadolibre.bootcamp.projeto_integrador.service;

import com.mercadolibre.bootcamp.projeto_integrador.dto.BatchBuyerResponseDto;
import com.mercadolibre.bootcamp.projeto_integrador.dto.BatchDueDateResponseDto;
import com.mercadolibre.bootcamp.projeto_integrador.dto.BatchRequestDto;
import com.mercadolibre.bootcamp.projeto_integrador.exceptions.BadRequestException;
import com.mercadolibre.bootcamp.projeto_integrador.exceptions.InitialQuantityException;
import com.mercadolibre.bootcamp.projeto_integrador.exceptions.NotFoundException;
import com.mercadolibre.bootcamp.projeto_integrador.exceptions.UnauthorizedManagerException;
import com.mercadolibre.bootcamp.projeto_integrador.model.*;
import com.mercadolibre.bootcamp.projeto_integrador.model.enums.ProductCategory;
import com.mercadolibre.bootcamp.projeto_integrador.repository.IBatchRepository;
import com.mercadolibre.bootcamp.projeto_integrador.service.interfaces.IBatchService;
import com.mercadolibre.bootcamp.projeto_integrador.service.interfaces.IManagerService;
import com.mercadolibre.bootcamp.projeto_integrador.service.interfaces.IProductService;
import com.mercadolibre.bootcamp.projeto_integrador.service.interfaces.ISectionService;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class BatchService implements IBatchService {
    private final int minimumExpirationDays = 20;
    private final IBatchRepository batchRepository;
    private final IManagerService managerService;
    private final ISectionService sectionService;
    private final IProductService productService;

    public BatchService(IBatchRepository batchRepository, IManagerService managerService,
                        ISectionService sectionService, IProductService productService) {
        this.batchRepository = batchRepository;
        this.managerService = managerService;
        this.sectionService = sectionService;
        this.productService = productService;
    }

    @Override
    public List<Batch> createAll(List<BatchRequestDto> batchesDto, InboundOrder order) {
        Map<Long, Product> products = productService.getProductMap(batchesDto);
        List<Batch> batches = buildBatchesForCreate(batchesDto, order, products);
        return batchRepository.saveAll(batches);
    }

    @Override
    public List<Batch> updateAll(InboundOrder order, List<BatchRequestDto> batchesDto) {
        Map<Long, Product> products = productService.getProductMap(batchesDto);
        List<Long> batchNumbersToUpdate = batchesDto.stream()
                .map(BatchRequestDto::getBatchNumber)
                .filter(batchNumber -> batchNumber > 0L)
                .collect(Collectors.toList());

        List<Batch> batchesToUpdate = batchRepository.findAllById(batchNumbersToUpdate);

        boolean isAllFromSameOrder = batchesToUpdate
                .stream()
                .allMatch(batch -> batch.getInboundOrder().getOrderNumber() == order.getOrderNumber());

        if (!isAllFromSameOrder)
            throw new BadRequestException("Unable to update batches of different orders");

        Map<Long, BatchRequestDto> batchesDtoMap = batchesDto.stream()
                .filter(dto -> dto.getBatchNumber() > 0L)
                .collect(Collectors.toMap(BatchRequestDto::getBatchNumber, dto -> dto));

        Stream<Batch> updatedBatches = batchesToUpdate.stream()
                .map(batch -> updateBatchFromDto(batch, batchesDtoMap.get(batch.getBatchNumber()), products));

        Stream<Batch> batchesToInsert = batchesDto.stream()
                .filter(dto -> dto.getBatchNumber() == 0L)
                .map(dto -> mapDtoToBatch(dto, order, products))
                .peek(batch -> batch.setCurrentQuantity(batch.getInitialQuantity()));

        List<Batch> batchesToSave = Stream.concat(updatedBatches, batchesToInsert).collect(Collectors.toList());

        return batchRepository.saveAll(batchesToSave);
    }

    /**
     * Método que busca a lista de Batches com estoque positovo e data de validade superior a 20 dias.
     *
     * @return List<Batch>
     */
    @Override
    public List<BatchBuyerResponseDto> findAll() {
        LocalDate minimumExpirationDate = LocalDate.now().plusDays(minimumExpirationDays);
        List<Batch> batches = batchRepository.findByCurrentQuantityGreaterThanAndDueDateAfter(0, minimumExpirationDate);
        if (batches.isEmpty()) {
            throw new NotFoundException("Products", "There are no products in stock");
        }
        return mapListBatchToListDto(batches);
    }

    /**
     * Método que busca a lista de Batches com estoque positivo e data de validade superior a 20 dias, filtrado por
     * categoria.
     *
     * @param categoryCode
     * @return List<Batch>
     */
    @Override
    public List<BatchBuyerResponseDto> findBatchByCategory(String categoryCode) {
        ProductCategory category = getCategory(categoryCode);
        LocalDate minimumExpirationDate = LocalDate.now().plusDays(minimumExpirationDays);
        List<Batch> batches = batchRepository
                .findByCurrentQuantityGreaterThanAndDueDateAfterAndProduct_CategoryIs(0, minimumExpirationDate,
                        category);
        if (batches.isEmpty()) {
            throw new NotFoundException("Products", "There are no products in stock in the requested category");
        }
        return mapListBatchToListDto(batches);
    }

    /**
     * Método que retorna os lotes filtrados por seção em ordem crescente da data de validade
     *
     * @param sectionCode  Código da seção
     * @param numberOfDays Número de dias a partir de hoje
     * @param managerId    ID do representante
     * @return Lista de lotes
     */
    @Override
    public List<BatchDueDateResponseDto> findBatchBySection(long sectionCode, int numberOfDays, long managerId) {
        if (numberOfDays < 0)
            throw new BadRequestException("The number of days to expiration can't be negative");

        Section section = sectionService.findById(sectionCode);

        Manager manager = tryFindManagerById(managerId);
        ensureManagerHasPermissionInSection(manager, section);

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(numberOfDays);

        return batchRepository.findByInboundOrder_SectionAndDueDateBetweenOrderByDueDate(section, startDate, endDate)
                .stream()
                .filter(batch -> batch.getCurrentQuantity() > 0)
                .map(BatchDueDateResponseDto::new)
                .collect(Collectors.toList());
    }

    /**
     * Método que retorna os lotes filtrados por categoria e data de vencimento e ordenados por data de vencimento
     *
     * @param categoryCode Código da categoria
     * @param numberOfDays Número de dias mínimo até expirar os produtos
     * @param orderDir     Direção da ordenação
     * @param managerId    ID do representante
     * @return Lista de lotes
     */
    @Override
    public List<BatchDueDateResponseDto> findBatchByCategoryAndDueDate(String categoryCode,
                                                                       int numberOfDays,
                                                                       String orderDir,
                                                                       long managerId) {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(numberOfDays);
        ProductCategory category = getCategory(categoryCode);
        String orderDirection = StringUtils.trimToEmpty(orderDir);

        tryFindManagerById(managerId);

        if (numberOfDays < 0)
            throw new BadRequestException("The number of days to expiration can't be negative");

        if (!StringUtils.equalsAnyIgnoreCase(orderDirection, "ASC", "DESC"))
            throw new BadRequestException("The order direction should be either ASC or DESC");

        List<Batch> batches = orderDirection.equalsIgnoreCase("ASC")
                ? batchRepository.findByProduct_CategoryAndDueDateBetweenOrderByDueDateAsc(category, startDate, endDate)
                : batchRepository.findByProduct_CategoryAndDueDateBetweenOrderByDueDateDesc(category, startDate,
                endDate);

        return batches.stream()
                .filter(batch -> batch.getInboundOrder().getSection().getManager().getManagerId() == managerId)
                .filter(batch -> batch.getCurrentQuantity() > 0)
                .map(BatchDueDateResponseDto::new)
                .collect(Collectors.toList());
    }

    private List<Batch> buildBatchesForCreate(List<BatchRequestDto> batchesDto, InboundOrder order, Map<Long,
            Product> products) {
        return batchesDto.stream()
                .map(dto -> mapDtoToBatch(dto, order, products))
                .peek(batch -> batch.setBatchNumber(0L))
                .peek(batch -> batch.setCurrentQuantity(batch.getInitialQuantity()))
                .collect(Collectors.toList());
    }

    private Batch updateBatchFromDto(Batch batch, BatchRequestDto dto, Map<Long, Product> products) {
        Product product = products.get(dto.getProductId());

        if (product == null) {
            throw new NotFoundException("Product");
        }

        batch.setProduct(product);
        batch.setCurrentTemperature(dto.getCurrentTemperature());
        batch.setMinimumTemperature(dto.getMinimumTemperature());
        batch.setManufacturingDate(dto.getManufacturingDate());
        batch.setManufacturingTime(dto.getManufacturingTime());
        batch.setDueDate(dto.getDueDate());
        batch.setProductPrice(dto.getProductPrice());

        int soldProducts = batch.getInitialQuantity() - batch.getCurrentQuantity();
        batch.setCurrentQuantity(dto.getInitialQuantity() - soldProducts);

        if (batch.getCurrentQuantity() < 0) {
            throw new InitialQuantityException(dto.getInitialQuantity(), soldProducts);
        }

        batch.setInitialQuantity(dto.getInitialQuantity());

        return batch;
    }

    private List<BatchBuyerResponseDto> mapListBatchToListDto(List<Batch> batches) {
        return batches.stream()
                .map(BatchBuyerResponseDto::new)
                .collect(Collectors.toList());
    }

    private ProductCategory getCategory(String categoryCode) {
        categoryCode = categoryCode.toUpperCase();
        switch (categoryCode) {
            case "FS":
                return ProductCategory.FRESH;
            case "RF":
                return ProductCategory.CHILLED;
            case "FF":
                return ProductCategory.FROZEN;
            default:
                throw new BadRequestException("Invalid category, try again with one of the options: " +
                        "'FS', 'RF' or 'FF' for fresh, chilled or frozen products respectively.");
        }
    }

    private void ensureManagerHasPermissionInSection(Manager manager, Section section) {
        if (section.getManager().getManagerId() != manager.getManagerId())
            throw new UnauthorizedManagerException(manager.getName());
    }

    private Manager tryFindManagerById(long managerId) {
        return managerService.findById(managerId);
    }

    private static Batch mapDtoToBatch(BatchRequestDto dto, InboundOrder order, Map<Long, Product> products) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.typeMap(BatchRequestDto.class, Batch.class).addMappings(mapper -> {
            Converter<Long, Product> converter = context -> products.get(context.getSource());
            mapper.using(converter).map(BatchRequestDto::getProductId, Batch::setProduct);
        });
        Batch batch = modelMapper.map(dto, Batch.class);
        if (batch.getProduct() == null)
            throw new NotFoundException("Product");
        batch.setInboundOrder(order);
        return batch;
    }
}
