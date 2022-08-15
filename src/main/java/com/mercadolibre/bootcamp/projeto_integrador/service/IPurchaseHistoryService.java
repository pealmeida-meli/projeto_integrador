package com.mercadolibre.bootcamp.projeto_integrador.service;

import com.mercadolibre.bootcamp.projeto_integrador.dto.PurchaseHistoryResponseDto;

import java.util.List;

public interface IPurchaseHistoryService {
    List<PurchaseHistoryResponseDto> getHistory(long buyerId);
}
