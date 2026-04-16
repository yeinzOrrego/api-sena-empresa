package com.yapps.senaempresa.model.dto;

import lombok.Builder;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import java.util.Date;

@Data
@Builder
public class NewUserDto {

    @NotBlank(message = "La contraseña es obligatoria")
    private String userPassword;

    @NotBlank(message = "El No de identificación es obligatorio")
    private String userIdentification;
    
    @NotNull(message = "El tipo de identificación es obligatorio")
    private Long userTypeIdentification;
    
    private String userAddress;

    private String userCellular;

    @NotNull(message = "El usuario creador no puede ser nulo")
    private Long userCreated;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El formato del correo electrónico no es válido")
    private String userEmail;

    @NotNull(message = "La fecha de creación es obligatoria")
    private Date dateCreated;

    @NotBlank(message = "El primer nombre es obligatorio")
    private String userFirstname;

    @NotBlank(message = "El apellido es obligatorio")
    private String userLastname;
}
