package com.yapps.senaempresa.model.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class InventoryDto {
    private Long inventoryId;
    private Long productId;
    private Integer stock;
    private LocalDateTime lastUpdateDate;
}
