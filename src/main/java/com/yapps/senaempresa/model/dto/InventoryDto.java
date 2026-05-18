package com.yapps.senaempresa.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryDto {
    private Long inventoryId;
    private Long productId;
    private Integer stock;
    private LocalDateTime lastUpdateDate;
}
