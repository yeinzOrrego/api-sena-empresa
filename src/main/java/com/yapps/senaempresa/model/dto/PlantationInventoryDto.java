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
public class PlantationInventoryDto {
    private Long plantationInventoryId;
    private String inventoryCode;
    private Long productId;
    private Integer producedQuantity;
    private Integer availableQuantity;
    private LocalDateTime productionDate;
}
