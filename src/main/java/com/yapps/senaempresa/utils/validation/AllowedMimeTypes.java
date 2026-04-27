package com.yapps.senaempresa.utils.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MimeTypeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface AllowedMimeTypes {
    String message() default "El formato del archivo adjunto no es válido. Solo se permiten imágenes JPG.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    
    // Lista de MimeTypes permitidos
    String[] types();
}
