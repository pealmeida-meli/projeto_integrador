package com.mercadolibre.bootcamp.projeto_integrador.dto;

import com.mercadolibre.bootcamp.projeto_integrador.config.PaginationConfig;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class PaginatedResponseDto<T> {
    private final List<T> items;
    private final int currentPage;
    private final int perPage;
    private final int firstPage;
    private final int lastPage;
    private final long totalElements;

    public PaginatedResponseDto(Page<T> page) {
        firstPage = PaginationConfig.getFirstPage();
        items = page.getContent();
        currentPage = PaginationConfig.getCurrentPage(page);
        lastPage = PaginationConfig.getLastPage(page);
        perPage = page.getSize();
        totalElements = page.getTotalElements();
    }
}
