package com.yapps.senaempresa.model.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PlantationInventoryDto {
    private String plantationInventoryId;
    private Long productId;
    private Integer producedQuantity;
    private Integer availableQuantity;
    private LocalDateTime productionDate;
}
