package com.yapps.senaempresa.model.dto;

import java.math.BigDecimal;

import org.springframework.web.multipart.MultipartFile;
import com.yapps.senaempresa.utils.validation.AllowedMimeTypes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductDto {
    private Long productId;
    private String name;
    private BigDecimal price;
    private String barCode;
    private Long unitMeasure;
    @AllowedMimeTypes(types = {"image/jpeg"})
    private MultipartFile newAttachment;
    private Long currentAttachmentId;
    private String status;
}
