package com.yapps.senaempresa.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewUserDto {

    @NotBlank(message = "El No de identificación es obligatorio")
    private String userIdentification;
    
    @NotNull(message = "El tipo de identificación es obligatorio")
    private Long userTypeIdentification;
    
    private String userAddress;

    private String userCellular;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El formato del correo electrónico no es válido")
    private String userEmail;

    @NotBlank(message = "El primer nombre es obligatorio")
    private String userFirstname;

    @NotBlank(message = "El apellido es obligatorio")
    private String userLastname;
}
