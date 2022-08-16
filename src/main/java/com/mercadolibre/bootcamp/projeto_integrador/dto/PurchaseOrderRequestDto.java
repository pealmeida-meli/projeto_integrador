package com.mercadolibre.bootcamp.projeto_integrador.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
public class PurchaseOrderRequestDto {
    @NotNull(message = "O status da compra não pode estar vazio")
    @Pattern(regexp = "^(Closed|Opened)$", message = "Status só pode ser Opened ou Closed")
    private String orderStatus;

    @NotNull(message = "Objeto batch é obrigatório")
    private @Valid BatchPurchaseOrderRequestDto batch;
}
