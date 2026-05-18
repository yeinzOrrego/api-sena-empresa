package com.yapps.senaempresa.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransferNotificationDto {
    private Long movementId;
    private String productName;
    private Integer quantity;
    private String status;
    private String receivedByName;
}