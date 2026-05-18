package com.yapps.senaempresa.model.dto;

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
public class ResolveTransferDto {

    @NotNull(message = "Movement ID is required")
    private Long movementId;

    @NotBlank(message = "Resolution status is required (A or R)")
    private String status;

    @NotNull(message = "Delivered user is required")
    private Long deliveredBy;

}