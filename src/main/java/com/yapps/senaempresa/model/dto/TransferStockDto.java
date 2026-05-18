package com.yapps.senaempresa.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransferStockDto {
    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Inventory source is required")
    private Long sourceInventory;

    @NotNull(message = "Quantity to transfer is required")
    @Min(value = 1, message = "Quantity must be greater than zero")
    private Integer quantity;

    @NotNull(message = "Received user is required")
    private Long receivedBy;

}
