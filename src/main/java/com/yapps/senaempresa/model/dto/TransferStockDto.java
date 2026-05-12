package com.yapps.senaempresa.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TransferStockDto {
    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotBlank(message = "Inventory source is required")
    private String inventorySource;

    @NotNull(message = "Quantity to transfer is required")
    @Min(value = 1, message = "Quantity must be greater than zero")
    private Integer quantity;

    @NotNull(message = "Received user is required")
    private Long receivedBy;
    
    @NotNull(message = "Delivered user is required")
    private Long deliveredBy;

}
