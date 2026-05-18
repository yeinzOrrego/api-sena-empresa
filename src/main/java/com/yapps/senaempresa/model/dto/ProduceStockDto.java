package com.yapps.senaempresa.model.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProduceStockDto {
    @NotBlank(message = "Inventory code is required")
    private String inventoryCode;

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Quantity to produce is required")
    @Min(value = 1, message = "Quantity must be greater than zero")
    private Integer producedQuantity;

    @NotNull(message = "Quantity to make available is required")
    @Min(value = 1, message = "Quantity must be greater than zero")
    private Integer availableQuantity;

    @NotNull(message = "Production date is required")
    @PastOrPresent(message = "Production date cannot be in the past")
    private LocalDateTime productionDate;
}
