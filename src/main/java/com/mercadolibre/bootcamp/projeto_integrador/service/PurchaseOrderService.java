package com.mercadolibre.bootcamp.projeto_integrador.service;

import com.mercadolibre.bootcamp.projeto_integrador.dto.BatchBuyerResponseDto;
import com.mercadolibre.bootcamp.projeto_integrador.dto.BatchPurchaseOrderRequestDto;
import com.mercadolibre.bootcamp.projeto_integrador.dto.PurchaseOrderRequestDto;
import com.mercadolibre.bootcamp.projeto_integrador.dto.PurchaseOrderResponseDto;
import com.mercadolibre.bootcamp.projeto_integrador.exceptions.BatchOutOfStockException;
import com.mercadolibre.bootcamp.projeto_integrador.exceptions.NotFoundException;
import com.mercadolibre.bootcamp.projeto_integrador.exceptions.PurchaseOrderAlreadyClosedException;
import com.mercadolibre.bootcamp.projeto_integrador.exceptions.UnauthorizedBuyerException;
import com.mercadolibre.bootcamp.projeto_integrador.model.Batch;
import com.mercadolibre.bootcamp.projeto_integrador.model.BatchPurchaseOrder;
import com.mercadolibre.bootcamp.projeto_integrador.model.Buyer;
import com.mercadolibre.bootcamp.projeto_integrador.model.PurchaseOrder;
import com.mercadolibre.bootcamp.projeto_integrador.repository.IBatchPurchaseOrderRepository;
import com.mercadolibre.bootcamp.projeto_integrador.repository.IBatchRepository;
import com.mercadolibre.bootcamp.projeto_integrador.repository.IBuyerRepository;
import com.mercadolibre.bootcamp.projeto_integrador.repository.IPurchaseOrderRepository;
import com.mercadolibre.bootcamp.projeto_integrador.service.interfaces.IPurchaseOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PurchaseOrderService implements IPurchaseOrderService {
    private final IBuyerRepository buyerRepository;
    private final IPurchaseOrderRepository purchaseOrderRepository;
    private final IBatchRepository batchRepository;
    private final IBatchPurchaseOrderRepository batchPurchaseOrderRepository;

    public PurchaseOrderService(IBuyerRepository buyerRepository, IPurchaseOrderRepository purchaseOrderRepository,
                                IBatchRepository batchRepository,
                                IBatchPurchaseOrderRepository batchPurchaseOrderRepository) {
        this.buyerRepository = buyerRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.batchRepository = batchRepository;
        this.batchPurchaseOrderRepository = batchPurchaseOrderRepository;
    }

    /**
     * Metodo que cria um carrinho (PurchaseOrder) novo ou insere/atualiza itens em um carrinho existente.
     *
     * @param request objeto PurchaseOrderRequestDto.
     * @return valor BigDecimal do valor total em carrinho.
     */
    @Transactional
    @Override
    public PurchaseOrderResponseDto create(PurchaseOrderRequestDto request, long buyerId) {
        Buyer buyer = findBuyer(buyerId);
        PurchaseOrder purchaseOrder = getPurchaseOrder(buyer, request.getOrderStatus());

        return new PurchaseOrderResponseDto(purchaseOrder.getPurchaseId(), getPurchaseInStock(request.getBatch(),
                purchaseOrder));
    }

    /**
     * Metodo que atualiza o carrinho (PurchaseOrder) para fechado.
     *
     * @param purchaseOrderId identificador do carrinho.
     * @return valor BigDecimal do valor total da compra.
     */
    @Transactional
    @Override
    public PurchaseOrderResponseDto update(long purchaseOrderId, long buyerId) {
        PurchaseOrder foundOrder = findPurchaseOrder(purchaseOrderId, buyerId);

        foundOrder.setOrderStatus("Closed");
        purchaseOrderRepository.save(foundOrder);

        return new PurchaseOrderResponseDto(foundOrder.getPurchaseId(), foundOrder.getBatchPurchaseOrders().stream()
                .map(batchPurchaseOrder -> batchPurchaseOrder.getUnitPrice().multiply(new BigDecimal(batchPurchaseOrder.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    /**
     * Metodo que remove produto do carrinho.
     *
     * @param purchaseOrderId identificador do carrinho (PurchaseOrder).
     * @param batchDto        objeto BatchPurchaseOrderRequestDto com id do batch a ser retirado do carrinho.
     * @param buyerId         identificador do comprador.
     */
    @Transactional
    @Override
    public void dropProducts(long purchaseOrderId, BatchPurchaseOrderRequestDto batchDto, long buyerId) {
        batchPurchaseOrderRepository.delete(returnToStock(findBatchPurchaseOrder(findPurchaseOrder(purchaseOrderId,
                buyerId), findBatchById(batchDto.getBatchNumber()))));
    }

    /**
     * Método que busca a lista de compras do carrinho (PurchaseOrder) do cliente.
     *
     * @param buyerId         long.
     * @param purchaseOrderId long.
     * @return PurchaseOrder.
     * @throws NotFoundException if not exist opened Purchase
     */
    @Transactional
    @Override
    public List<BatchBuyerResponseDto> getBatches(long buyerId, long purchaseOrderId) {
        Buyer buyer = findBuyer(buyerId);
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findOneByPurchaseIdAndBuyer(purchaseOrderId, buyer);
        if (purchaseOrder == null) {
            throw new NotFoundException("Purchase");
        }
        return mapListBatchPurchaseToListDto(purchaseOrder.getBatchPurchaseOrders());
    }

    private BatchPurchaseOrder returnToStock(BatchPurchaseOrder batchPurchaseOrder) {
        batchPurchaseOrder.getBatch().setCurrentQuantity(batchPurchaseOrder.getBatch().getCurrentQuantity() + batchPurchaseOrder.getQuantity());
        return batchPurchaseOrder;
    }

    private PurchaseOrder getPurchaseOrder(Buyer buyer, String orderStatus) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findOnePurchaseOrderByBuyerAndOrderStatusIsLike(buyer,
                "Opened");
        if (purchaseOrder == null) {
            purchaseOrder = new PurchaseOrder();
            purchaseOrder.setBuyer(buyer);
            purchaseOrder.setDate(LocalDateTime.now());
            purchaseOrderRepository.save(purchaseOrder);
        }
        purchaseOrder.setOrderStatus(orderStatus);
        return purchaseOrder;
    }

    private BigDecimal getPurchaseInStock(BatchPurchaseOrderRequestDto batchDto, PurchaseOrder purchase) {
        Optional<Batch> batchFound =
                batchRepository.findOneByBatchNumberAndCurrentQuantityGreaterThanEqualAndDueDateAfterOrderByDueDate(batchDto.getBatchNumber(),
                        batchDto.getQuantity(), LocalDate.now().plusDays(21));

        if (batchFound.isEmpty()) throw new BatchOutOfStockException(batchDto.getBatchNumber());

        batchFound.get().setCurrentQuantity(batchFound.get().getCurrentQuantity() - batchDto.getQuantity());

        saveBatchPurchaseOrder(batchFound.get(), batchDto, purchase);
        return sumTotalPrice(purchase);
    }

    private BigDecimal sumTotalPrice(PurchaseOrder purchase) {
        return purchase.getBatchPurchaseOrders().stream()
                .map(batchPurchaseOrder -> batchPurchaseOrder.getUnitPrice().multiply(new BigDecimal(batchPurchaseOrder.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void saveBatchPurchaseOrder(Batch batch, BatchPurchaseOrderRequestDto batchDto, PurchaseOrder purchase) {
        // Se já existir a tabela nxm entre um batch e uma purchase ela só é atualizada com a nova quantidade.
        BatchPurchaseOrder batchPurchaseOrder;
        try {
            batchPurchaseOrder = findBatchPurchaseOrder(purchase, batch);
        } catch (NotFoundException ex) {
            batchPurchaseOrder = new BatchPurchaseOrder();
            batchPurchaseOrder.setPurchaseOrder(purchase);
            batchPurchaseOrder.setBatch(batch);
            batchPurchaseOrder.setUnitPrice(batch.getProductPrice());
        }
        batchPurchaseOrder.setQuantity(batchPurchaseOrder.getQuantity() + batchDto.getQuantity());
        batchPurchaseOrderRepository.save(batchPurchaseOrder);

        if (purchase.getBatchPurchaseOrders() == null) {
            purchase.setBatchPurchaseOrders(new ArrayList<>());
        }
        if (!purchase.getBatchPurchaseOrders().contains(batchPurchaseOrder)) {
            purchase.getBatchPurchaseOrders().add(batchPurchaseOrder);
        }
    }

    private PurchaseOrder findPurchaseOrder(long purchaseOrderId, long buyerId) {
        Optional<PurchaseOrder> foundOrder = purchaseOrderRepository.findById(purchaseOrderId);
        if (foundOrder.isEmpty()) throw new NotFoundException("Purchase order");
        if (foundOrder.get().getBuyer().getBuyerId() != buyerId)
            throw new UnauthorizedBuyerException(buyerId, purchaseOrderId);
        if (foundOrder.get().getOrderStatus().equals("Closed"))
            throw new PurchaseOrderAlreadyClosedException(foundOrder.get().getPurchaseId());
        return foundOrder.get();
    }

    private Buyer findBuyer(long buyerId) {
        Optional<Buyer> foundBuyer = buyerRepository.findById(buyerId);
        if (foundBuyer.isEmpty()) throw new NotFoundException("Buyer");
        return foundBuyer.get();
    }

    private Batch findBatchById(long batchNumber) {
        Optional<Batch> foundBatch = batchRepository.findById(batchNumber);
        if (foundBatch.isEmpty()) throw new NotFoundException("Batch");
        return foundBatch.get();
    }

    private BatchPurchaseOrder findBatchPurchaseOrder(PurchaseOrder purchase, Batch batch) {
        Optional<BatchPurchaseOrder> foundBatchPurchaseOrder =
                batchPurchaseOrderRepository.findOneByPurchaseOrderAndBatch(purchase, batch);
        if (foundBatchPurchaseOrder.isEmpty()) throw new NotFoundException("Batch PurchaseOrder");
        return foundBatchPurchaseOrder.get();
    }

    private List<BatchBuyerResponseDto> mapListBatchPurchaseToListDto(List<BatchPurchaseOrder> batches) {
        return batches.stream()
                .map(BatchBuyerResponseDto::new)
                .collect(Collectors.toList());
    }
}
