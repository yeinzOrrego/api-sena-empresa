package com.yapps.senaempresa.model.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class InventoryHistoryDto {
    private Long historyId;
    private ProductDetailsDto product;
    private Integer quantity;
    private String description;
    private LocalDateTime dateCreated;
}
