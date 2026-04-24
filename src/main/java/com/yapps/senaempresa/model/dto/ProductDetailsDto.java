package com.yapps.senaempresa.model.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailsDto {
    private Long productId;
    private String name;
    private BigDecimal price;
    private String barCode;
    private Long unitMeasure;
    private String status;
}
