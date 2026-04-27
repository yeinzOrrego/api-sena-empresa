package com.yapps.senaempresa.model.dto;

import java.math.BigDecimal;

import org.springframework.web.multipart.MultipartFile;
import com.yapps.senaempresa.utils.validation.AllowedMimeTypes;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewProductDto {

    @NotBlank(message = "El nombre del producto es obligatorio")
    private String name;

    @NotNull(message = "La unidad de medida es obligatoria")
    private Long unitMeasure;

    @NotNull(message = "El precio es obligatorio")
    private BigDecimal price;

    @NotBlank(message = "El código de barras o de referencia es obligatorio")
    private String barCode;

    @NotNull(message = "El usuario creador no puede ser nulo")
    private Long userCreated;

    @AllowedMimeTypes(types = {"image/jpeg"})
    private MultipartFile image;
    
}
