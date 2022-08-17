package com.mercadolibre.bootcamp.projeto_integrador.service.interfaces;

import com.mercadolibre.bootcamp.projeto_integrador.dto.PaginatedResponseDto;
import com.mercadolibre.bootcamp.projeto_integrador.dto.PurchaseHistoryItemResponseDto;
import org.springframework.data.domain.Pageable;

public interface IPurchaseHistoryService {
    PaginatedResponseDto<PurchaseHistoryItemResponseDto> getHistory(long buyerId, Pageable pageable);
}
