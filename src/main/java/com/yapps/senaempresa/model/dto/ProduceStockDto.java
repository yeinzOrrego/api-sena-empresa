package com.yapps.senaempresa.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProduceStockDto {
    @NotBlank(message = "Plantation Inventory ID is required")
    private String plantationInventoryId;

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Quantity to produce is required")
    @Min(value = 1, message = "Quantity must be greater than zero")
    private Integer producedQuantity;

    @NotNull(message = "Quantity to make available is required")
    @Min(value = 1, message = "Quantity must be greater than zero")
    private Integer availableQuantity;
}
