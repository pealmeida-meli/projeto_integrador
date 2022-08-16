package com.mercadolibre.bootcamp.projeto_integrador.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchPurchaseOrderRequestDto {

    @NotNull(message = "O id do batch não pode estar vazio")
    @Positive(message = "O id do batch deve ser um número positivo")
    private long batchNumber;

    @NotNull(message = "A quantidade do produto deve ser informada")
    @Min(value = 0, message = "A quantidade do produto deve ser maior ou igual a 0")
    private int quantity;
}
