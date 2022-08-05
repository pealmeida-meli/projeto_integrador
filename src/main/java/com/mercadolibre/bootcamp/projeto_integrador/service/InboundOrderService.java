package com.mercadolibre.bootcamp.projeto_integrador.service;

import com.mercadolibre.bootcamp.projeto_integrador.dto.BatchRequestDto;
import com.mercadolibre.bootcamp.projeto_integrador.dto.InboundOrderRequestDto;
import com.mercadolibre.bootcamp.projeto_integrador.dto.InboundOrderResponseDto;
import com.mercadolibre.bootcamp.projeto_integrador.exceptions.NotFoundException;
import com.mercadolibre.bootcamp.projeto_integrador.model.Batch;
import com.mercadolibre.bootcamp.projeto_integrador.model.InboundOrder;
import com.mercadolibre.bootcamp.projeto_integrador.model.Product;
import com.mercadolibre.bootcamp.projeto_integrador.model.Section;
import com.mercadolibre.bootcamp.projeto_integrador.repository.IBatchRepository;
import com.mercadolibre.bootcamp.projeto_integrador.repository.IInboundOrderRepository;
import com.mercadolibre.bootcamp.projeto_integrador.repository.IProductRepository;
import com.mercadolibre.bootcamp.projeto_integrador.repository.ISectionRepository;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InboundOrderService implements IInboundOrderService {

    @Autowired
    private IProductRepository productRepository;

    @Autowired
    private ISectionRepository sectionRepository;

    @Autowired
    private IInboundOrderRepository inboundOrderRepository;

    @Autowired
    private IBatchRepository batchRepository;

    @Override
    @Transactional
    public InboundOrderResponseDto create(InboundOrderRequestDto request) {
        Optional<Section> section = sectionRepository.findById(request.getSectionCode());
        if (section.isEmpty())
            throw new NotFoundException("Section");

        Map<Long, Product> products = productRepository
                .findAllById(request.getBatchStock().stream().map(BatchRequestDto::getProductId).collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(Product::getProductId, product -> product));

        List<Batch> batches = request.getBatchStock()
                .stream()
                .map(dto -> mapDtoToBatch(dto, products))
                .collect(Collectors.toList());

        InboundOrder order = new InboundOrder();
        order.setSection(section.get());
        order.setOrderDate(LocalDate.now());

        inboundOrderRepository.save(order);
        batchRepository.saveAll(batches.stream().peek(batch -> batch.setInboundOrder(order)).collect(Collectors.toList()));

        return new InboundOrderResponseDto() {{ setBatchStock(batches); }};
    }

    @Override
    public InboundOrderResponseDto update(InboundOrderRequestDto inboundOrderRequestDto) {
        return null;
    }

    private Batch mapDtoToBatch(BatchRequestDto dto, Map<Long, Product> products) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.typeMap(BatchRequestDto.class, Batch.class).addMappings(mapper -> {
            Converter<Long, Product> converter = context -> products.get(context.getSource());
            mapper.using(converter).map(BatchRequestDto::getProductId, Batch::setProduct);
        });
        Batch batch = modelMapper.map(dto, Batch.class);
        if (batch.getProduct() == null)
            throw new NotFoundException("Product");
        return batch;
    }
}
