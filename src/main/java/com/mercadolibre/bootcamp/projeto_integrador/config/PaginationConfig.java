package com.mercadolibre.bootcamp.projeto_integrador.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;

@Configuration
public class PaginationConfig {
    public static final int MAX_PAGE_SIZE = 100;
    public static final boolean ONE_INDEXED_PARAMETERS = true;
    public static final String SIZE_PARAMETER_NAME = "perPage";

    public static int getFirstPage() {
        return ONE_INDEXED_PARAMETERS ? 1 : 0;
    }

    public static <T> int getLastPage(Page<T> page) {
        return ONE_INDEXED_PARAMETERS ? page.getTotalPages() : Math.max(page.getTotalPages() - 1, 0);
    }

    public static <T> int getCurrentPage(Page<T> page) {
        return page.getPageable().getPageNumber() + getFirstPage();
    }

    @Bean
    public PageableHandlerMethodArgumentResolverCustomizer paginationCustomizer() {
        return pageableResolver -> {
            pageableResolver.setMaxPageSize(MAX_PAGE_SIZE);
            pageableResolver.setOneIndexedParameters(ONE_INDEXED_PARAMETERS);
            pageableResolver.setSizeParameterName(SIZE_PARAMETER_NAME);
        };
    }
}
